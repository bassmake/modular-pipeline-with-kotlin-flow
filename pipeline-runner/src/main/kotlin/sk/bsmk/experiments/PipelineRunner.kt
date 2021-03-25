package sk.bsmk.experiments

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging

import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@ExperimentalTime
fun main() = runBlocking {

    val log = KotlinLogging.logger { }

    val logSuccess: PipelineSink<String, Nothing> = Peek {
        log.info { "Successful output: $it" }
    }
    val logFailure = Peek<String> {
        log.error { "Something went wrong: $it" }
    }

    val pipeline = Pipeline(
        source = tickingSource(),
        transformation = messageAppender,
        sink = logSuccess,
        failureSink = logFailure
    )

    pipeline.run()

}

@ExperimentalTime
fun tickingSource(delay: Duration = Duration.ZERO, period: Duration = Duration.ZERO, count: Int = 10) =
    object : PipelineSource<Int> {
        override val name: String = "Ticking Source"
        override val flow: Flow<Int> = flow {
            delay(delay)
            for (i in 1..count) {
                emit(i)
                delay(period)
            }
        }
    }

val messageAppender = object : PipelineTransformation<Int, String, String> {
    override val name: String = "Message Appender"

    override suspend fun transform(input: Int): TransformResult<Int, String, String> {
        return if (input % 2 == 0) {
            TransformSuccess("Message $input")
        } else {
            TransformFailure(input, "Number is odd")
        }
    }
}

class Peek<T>(private val peek: (T) -> Unit) : PipelineTransformation<T, T, Unit>, PipelineSink<T, Nothing> {
    override val name: String = "Peek"

    override suspend fun transform(input: T): TransformResult<T, T, Unit> {
        peek(input)
        return TransformSuccess(input)
    }

    override suspend fun drain(input: T): DrainResult<T, Nothing> {
        peek(input)
        return DrainSuccess
    }
}
