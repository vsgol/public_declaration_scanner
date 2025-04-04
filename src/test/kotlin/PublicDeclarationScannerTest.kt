import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.psi.KtFile
import java.io.File
import kotlin.test.*

class PublicDeclarationScannerTest {

    @Test
    fun runSimpleTests() {
        val (_, failed) = runTestSuite("simple")
        assertTrue(failed == 0, "Some tests in 'simple' suite failed")
    }

    @Test
    fun runComplexTests() {
        val (_, failed) = runTestSuite("complex")
        assertTrue(failed == 0, "Some tests in 'complex' suite failed")
    }

    private fun runTestSuite(name: String): Pair<Int, Int> {
        println("Running test suite: '$name'")

        val base = File("src/test/resources/testdata/$name")
        val inputDir = File(base, "inputs")
        val expectedDir = File(base, "outputs")

        var passed = 0
        var failed = 0

        val inputFiles = inputDir.listFiles { f -> f.extension == "kt" } ?: emptyArray()

        for (inputFile in inputFiles) {
            val expectedFile = File(expectedDir, inputFile.nameWithoutExtension + ".out")
            if (!expectedFile.exists()) {
                println("Skipped (missing .out): ${inputFile.name}")
                failed++
                continue
            }

            val actual = runScannerOnFile(inputFile).trim()
            val expected = expectedFile.readText().trim()

            if (actual == expected) {
                println("Passed: ${inputFile.name}")
                passed++
            } else {
                println("Failed: ${inputFile.name}")
                println("  Expected:\n$expected")
                println("  Actual:\n$actual\n")
                failed++
            }
        }

        println("Summary for '$name': $passed / ${passed + failed} passed, $failed failed.\n")

        return passed to failed
    }

    private fun runScannerOnFile(file: File): String {
        val disposable = Disposer.newDisposable()
        val configuration = CompilerConfiguration()
        val environment = KotlinCoreEnvironment.createForProduction(
            disposable,
            configuration,
            EnvironmentConfigFiles.JVM_CONFIG_FILES
        )
        val psiFactory = KtPsiFactory(environment.project)
        val ktFile: KtFile = psiFactory.createFile(file.name, file.readText())
        val result = collectPublicDeclarations(ktFile).joinToString("\n")
        Disposer.dispose(disposable)
        return result
    }
}
