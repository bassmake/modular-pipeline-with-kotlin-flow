package sk.bsmk.experiments

interface PipelineTransformation<Input, Output, Failure> {
    val name: String

    suspend fun transform(input: Input): TransformResult<Input, Output, Failure>
}

sealed class TransformResult<out Input, out Output, out Failure>
data class TransformSuccess<Output>(val output: Output) :
    TransformResult<Nothing, Output, Nothing>()

data class TransformFailure<Input, Failure>(val input: Input, val failure: Failure) :
    TransformResult<Input, Nothing, Failure>()