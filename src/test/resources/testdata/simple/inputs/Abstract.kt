abstract class AbstractBase {
    abstract fun mustImplement(): String
    open fun overridable(): Unit = Unit
    fun concrete(): Int = 42
}
