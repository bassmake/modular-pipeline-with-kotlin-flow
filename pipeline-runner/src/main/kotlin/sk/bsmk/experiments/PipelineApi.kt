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

    suspend fun transform(input: Input): TransformationResult<Input, Output, Failure>
}

sealed class TransformationResult<out Input, out Output, out Failure>
data class TransformationSuccess<out Input, out Output, out Failure>(val output: Output) :
    TransformationResult<Input, Output, Failure>()

data class TransformationFailure<out Input, out Output, out Failure>(val input: Input, val failure: Failure) :
    TransformationResult<Input, Output, Failure>()