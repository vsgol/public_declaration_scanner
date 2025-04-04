val lambda = { x: Int -> x * 2 }

fun applyTwice(f: (Int) -> Int): Int {
    return f(f(1))
}

fun main() {
    val local = { println("local") }
    applyTwice { it + 1 }
}
