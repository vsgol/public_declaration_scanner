sealed class Result {
    class Success(val data: String) : Result()
    class Failure(val error: Throwable) : Result()
    object Loading : Result()
}
