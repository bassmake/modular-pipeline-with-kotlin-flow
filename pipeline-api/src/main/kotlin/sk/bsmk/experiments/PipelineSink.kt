package sk.bsmk.experiments

interface PipelineSink<in Input, out Failure> {
    val name: String

    suspend fun collect(input: Input): CollectResult<Failure>
}

sealed class CollectResult<out Failure>
class CollectSuccess<out Failure>: CollectResult<Failure>()
data class CollectFailure<out Failure>(val failure: Failure): CollectResult<Failure>()
