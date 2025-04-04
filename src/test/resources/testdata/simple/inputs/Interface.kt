interface Worker {
    fun work(): Boolean
    fun greet(name: String): String
    suspend fun doSuspended(x: Int, y: Int): Unit
    val isActive: Boolean
    var score: Int
}