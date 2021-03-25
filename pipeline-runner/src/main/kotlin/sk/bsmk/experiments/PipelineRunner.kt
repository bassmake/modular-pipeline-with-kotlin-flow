package sk.bsmk.experiments

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@ExperimentalTime
fun main() = runBlocking {

    val log = KotlinLogging.logger { }

    val logger = Logger<Int>()
    log.info { "I will run some pipeline." }

    val source = tickingSource()

    source.flow
        .collect { logger.collect(it) }

}

@ExperimentalTime
fun tickingSource(delay: Duration = Duration.ZERO, period: Duration = Duration.ZERO, count: Int = 10): Source<Int> {
    return object : Source<Int> {
        override val name: String = "Ticking Source"
        override val flow: Flow<Int> = flow {
            delay(delay)
            for (i in 1..count) {
                emit(i)
                delay(period)
            }
        }
    }
}

class Logger<T> : Transformation<T, T, Unit>, Sink<T, Nothing> {
    private val log = KotlinLogging.logger { }

    override val name: String = "Logging"

    override suspend fun transform(input: T): TransformationResult<T, Unit> {
        log.info { "I am logging $input" }
        return TransformationSuccess(input)
    }

    override suspend fun collect(input: T) {
        log.info { "I am logging $input" }
    }
}
