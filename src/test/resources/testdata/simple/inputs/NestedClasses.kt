open class Outer {
    val outerProp: Int = 1

    class Inner {
        fun innerFun() {}
        val innerVal: String = "hello"
    }

    interface InnerInterface {
        fun iFaceFun()
    }

    object InnerObject {
        var flag: Boolean = true
    }

    abstract class AbstractInner {
        abstract fun abstractMethod()
        open fun overridable() {}
    }

    suspend fun outerSuspend(): Unit = Unit
}
