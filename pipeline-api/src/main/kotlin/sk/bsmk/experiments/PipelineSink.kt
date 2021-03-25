package sk.bsmk.experiments

interface PipelineSink<Input, out Failure> {
    val name: String

    suspend fun drain(input: Input): DrainResult<Input, Failure>
}

sealed class DrainResult<out Input, out Failure>
object DrainSuccess: DrainResult<Nothing, Nothing>()
data class DrainFailure<Input, Failure>(val input: Input, val failure: Failure): DrainResult<Input, Failure>()
