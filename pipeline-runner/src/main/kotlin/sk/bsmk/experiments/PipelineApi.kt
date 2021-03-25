package sk.bsmk.experiments

import kotlinx.coroutines.flow.Flow

interface Source<Output> {
    val name: String

    val flow: Flow<Output>
}

interface Sink<Input, Error> {
    val name: String

    suspend fun collect(input: Input)
}

interface Transformation<Input, Output, Failure> {
    val name: String

    suspend fun transform(input: Input): TransformationResult<Output, Failure>
}

sealed class TransformationResult<out Output, out Failure>(val isSuccess: Boolean)
data class TransformationSuccess<out Output, out Failure>(val output: Output) : TransformationResult<Output, Failure>(true)
data class TransformationFailure<out Output, out Failure>(val failure: Failure) :
    TransformationResult<Output, Failure>(false)