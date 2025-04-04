class MyClass(val injected: Int) {

    val id: Int = 0
    var name: String = "Default"

    fun greet(user: String): String {
        return "Hello, $user"
    }

    suspend fun fetch(): Boolean = true

    inline fun compute(x: Int): Int = x * x

    private fun hidden() {}
    internal val internalVal = 42

    open fun overridable() {}
}
