package sk.bsmk.experiments

interface PipelineTransformation<Input, Output, Failure> {
    val name: String

    suspend fun transform(input: Input): TransformationResult<Input, Output, Failure>
}

sealed class TransformationResult<out Input, out Output, out Failure>
data class TransformationSuccess<out Input, out Output, out Failure>(val output: Output) :
    TransformationResult<Input, Output, Failure>()

data class TransformationFailure<out Input, out Output, out Failure>(val input: Input, val failure: Failure) :
    TransformationResult<Input, Output, Failure>()