import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.visibilityModifierType
import org.jetbrains.kotlin.psi.KtPsiFactory
import java.io.File

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("Usage: gradle run --args=<path-to-file-or-directory>")
        return
    }

    val disposable = Disposer.newDisposable()
    val configuration = CompilerConfiguration()
    val environment = KotlinCoreEnvironment.createForProduction(
        disposable,
        configuration,
        EnvironmentConfigFiles.JVM_CONFIG_FILES
    )

    val psiFactory = KtPsiFactory(environment.project)

    for (path in args) {
        val fileOrDir = File(path)
        val ktFiles = when {
            fileOrDir.isDirectory -> fileOrDir.walkTopDown().filter { it.extension == "kt" }.toList()
            fileOrDir.isFile && fileOrDir.extension == "kt" -> listOf(fileOrDir)
            else -> {
                println("Skipping unsupported input: $path")
                continue
            }
        }

        for (file in ktFiles) {
            val text = file.readText()
            val ktFile = psiFactory.createFile(file.name, text)

            val declarations = collectPublicDeclarations(ktFile)
            if (declarations.isNotEmpty()) {
                println("=== Public declarations in ${file.relativeTo(File(".")).path} ===")
                declarations.forEach { println(it) }
                println()
            }
        }
    }

    Disposer.dispose(disposable)
}


fun collectPublicDeclarations(file: KtFile): List<String> {
    data class Entry(
        val signature: String,
        val level: Int,
        val children: List<Entry> = emptyList()
    )

    fun recordTree(entry: Entry): List<String> {
        val indent = "  ".repeat(entry.level)
        return if (entry.children.isEmpty()) {
            listOf("$indent${entry.signature}")
        } else {
            buildList {
                add("$indent${entry.signature} {")
                entry.children.forEach { addAll(recordTree(it)) }
                add("$indent}")
            }
        }
    }

    fun collectModifiers(decl: KtModifierListOwner): String {
        val modifiers = mutableListOf<String>()

        if (decl.hasModifier(KtTokens.ABSTRACT_KEYWORD)) modifiers += "abstract"
        if (decl.hasModifier(KtTokens.OPEN_KEYWORD)) modifiers += "open"
        if (decl.hasModifier(KtTokens.SUSPEND_KEYWORD)) modifiers += "suspend"
        if (decl.hasModifier(KtTokens.INLINE_KEYWORD)) modifiers += "inline"

        return if (modifiers.isNotEmpty()) modifiers.joinToString(" ") + " " else ""
    }

    fun buildEntries(decl: KtDeclaration, level: Int): Entry? {
        val visibility = decl.visibilityModifierType()?.value ?: "public"
        if (visibility != "public") return null

        return when (decl) {
            is KtClassOrObject -> {
                val modifiers = collectModifiers(decl)
                val kind = when {
                    decl is KtClass && decl.isInterface() -> "interface"
                    decl is KtObjectDeclaration -> "object"
                    decl.hasModifier(KtTokens.SEALED_KEYWORD) -> "sealed class"
                    decl.hasModifier(KtTokens.DATA_KEYWORD) -> "data class"
                    else -> "class"
                }
                val name = decl.name ?: "<anonymous>"

                val children = decl.declarations.mapNotNull { buildEntries(it, level + 1) }
                val signature = "$modifiers$kind $name"
                Entry(signature, level, children)
            }

            is KtFunction -> {
                val modifiers = collectModifiers(decl)
                val name = decl.name ?: "<anonymous>"
                val params = decl.valueParameters.joinToString(", ") {
                    val pname = it.name ?: "_"
                    val ptype = it.typeReference?.text ?: "Any"
                    "$pname: $ptype"
                }
                val returnType = decl.typeReference?.text?.let { ": $it" } ?: ""
                Entry(modifiers + "fun $name($params)$returnType", level)
            }

            is KtProperty -> {
                val name = decl.name ?: "<anonymous>"
                val keyword = if (decl.isVar) "var" else "val"
                val type = decl.typeReference?.text
                    ?: if (decl.initializer is KtLambdaExpression) "lambda" else "Any"

                Entry("$keyword $name: $type", level)
            }

            else -> null
        }
    }

    return file.declarations
        .mapNotNull { buildEntries(it, 0) }
        .flatMap { recordTree(it) }
}
