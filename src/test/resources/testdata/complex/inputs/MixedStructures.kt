abstract class A {
    fun aFun() {}

    object AO {
        val aoVal = 42

        interface AOI {
            fun call()
        }

        class AOC {
            fun deepFun() {}
        }
    }

    class AInner {
        class ADeep {
            val value = "deep"
        }

        fun innerFun() {}
    }
}

class B

object RootObject {
    val name = "root"

    class BInner {
        fun run() {}
    }

    interface Marker
}

interface MarkerTop

class Wrapper {
    fun process() {}

    val lambda = { x: Int -> x * x }

    class Nested1 {
        open class Nested2 {
            suspend fun nestedJob() {}
        }
    }
}
