package sk.bsmk.experiments

interface PipelineTransformation<Input, Output, Failure> {
    val name: String

    suspend fun transform(input: Input): TransformResult<Input, Output, Failure>
}

sealed class TransformResult<Input, Output, Failure>
data class TransformSuccess<Input, Output, Failure>(val output: Output) :
    TransformResult<Input, Output, Failure>()

data class TransformFailure<Input, Output, Failure>(val input: Input, val failure: Failure) :
    TransformResult<Input, Output, Failure>()