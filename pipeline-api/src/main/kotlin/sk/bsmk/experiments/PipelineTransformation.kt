package sk.bsmk.experiments

interface PipelineTransformation<Input, Output, Failure> {
    val name: String

    suspend fun transform(input: Input): TransformResult<Input, Output, Failure>
}

sealed class TransformResult<out Input, out Output, out Failure>
data class TransformSuccess<out Input, out Output, out Failure>(val output: Output) :
    TransformResult<Input, Output, Failure>()

data class TransformFailure<out Input, out Output, out Failure>(val input: Input, val failure: Failure) :
    TransformResult<Input, Output, Failure>()