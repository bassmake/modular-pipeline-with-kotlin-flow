package sk.bsmk.experiments

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import mu.KotlinLogging
import kotlin.coroutines.coroutineContext

@ExperimentalCoroutinesApi
fun main() = runBlocking {
    val client = HttpClient(CIO)
    val pipeline = Pipeline(
        source = produceNumbers(10),
        transform = { input: Int ->
            println("Transforming $input -------------------------------------")
            transform(client, input)
        },
        processOutput = { output -> println("Successful output: $output.") },
        processFailure = { input, failure -> println("Something went wrong for $input: $failure.") }
    )
    pipeline.run()
}

@ExperimentalCoroutinesApi
fun CoroutineScope.produceNumbers(until: Int = 10) = produce {
    var x = 1
    while (x <= until) send(x++)
    close()
}

suspend fun fetchExcuse(client: HttpClient): String {
    val response: HttpResponse = client.get("http://programmingexcuses.com/")
    return response.readText()
}

suspend fun transform(client: HttpClient, input: Int): TransformResult<String, String> {
    return if (input % 2 == 0) {
        TransformSuccess(fetchExcuse(client))
    } else {
        TransformFailure("Number $input is odd")
    }
}

sealed class TransformResult<out Output, out Failure>
data class TransformSuccess<Output>(val output: Output) : TransformResult<Output, Nothing>()
data class TransformFailure<Failure>(val failure: Failure) : TransformResult<Nothing, Failure>()

data class Pipeline<Input, Output, Failure>(
    val source: ReceiveChannel<Input>,
    val transform: suspend (Input) -> TransformResult<Output, Failure>,
    val processOutput: suspend (Output) -> Unit,
    val processFailure: suspend (Input, Failure) -> Unit
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
