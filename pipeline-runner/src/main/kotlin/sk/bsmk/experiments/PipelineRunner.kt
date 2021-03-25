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

    val logSuccess = Peek<TransformSuccess<Any, Any, Any>> {
        log.info { "Successful output: ${it.output}" }
    }
    val logError = Peek<TransformFailure<Any, Any, Any>> {
        log.error { "Something went wrong with ${it.input}: ${it.failure}" }
    }
    log.info { "I will run some pipeline." }

    val source = tickingSource()

    val transformed = source.flow.map { messageAppender.transform(it) }
    val successes = transformed.mapNotNull {
        when (it) {
            is TransformSuccess -> it
            is TransformFailure -> null
        }
    }
    val failures = transformed.mapNotNull {
        when (it) {
            is TransformSuccess -> null
            is TransformFailure -> it
        }
    }

    successes.collect { logSuccess.collect(it) }
    failures.collect { logError.collect(it) }

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

    override suspend fun collect(input: T): CollectResult<Nothing> {
        peek(input)
        return CollectSuccess()
    }
}
