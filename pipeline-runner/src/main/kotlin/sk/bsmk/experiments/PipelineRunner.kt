package sk.bsmk.experiments

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import mu.KotlinLogging
import kotlin.coroutines.coroutineContext
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalTime
fun main() = runBlocking {
    val pipeline = Pipeline(
        source = produceNumbers(10),
        transform = { input: Int ->
            if (input % 2 == 0) {
                TransformSuccess("Message $input")
            } else {
                TransformFailure("Number is odd")
            }
        },
        processOutput = { output -> println("Successful output: $output") },
        processFailure = { input, failure -> println("Something went wrong for $input: $failure") }
    )
    pipeline.run()
}

@ExperimentalCoroutinesApi
fun CoroutineScope.produceNumbers(until: Int = 10) = produce {
    var x = 1
    while (x <= until) send(x++)
    close()
}

sealed class TransformResult<out Output, out Failure>
data class TransformSuccess<Output>(val output: Output) : TransformResult<Output, Nothing>()
data class TransformFailure<Failure>(val failure: Failure) : TransformResult<Nothing, Failure>()

data class Pipeline<Input, Output, Failure>(
    val source: ReceiveChannel<Input>,
    val transform: (Input) -> TransformResult<Output, Failure>,
    val processOutput: (Output) -> Unit,
    val processFailure: (Input, Failure) -> Unit
) {

    private val log = KotlinLogging.logger { }

    suspend fun run() {
        for (input in source) {
            when (val result = transform(input)) {
                is TransformSuccess -> {
                    val output = result.output
                    log.debug { "Transformation successful: $output" }
                    processOutput(output)
                }
                is TransformFailure -> {
                    val failure = result.failure
                    log.debug { "Transformation failed: $failure" }
                    processFailure(input, failure)
                }
            }
        }
        coroutineContext.cancelChildren()
    }

}
