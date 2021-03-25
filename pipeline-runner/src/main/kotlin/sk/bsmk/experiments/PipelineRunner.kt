package sk.bsmk.experiments

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging

fun main() = runBlocking {

    val log = KotlinLogging.logger { }

    val pipeline = loggingPipeline
    log.info { "I will run ${pipeline.name} pipeline." }

    val input: Flow<String> = flow {
        for (i in 1..3) {
            emit("Message $i")
        }
    }

    input
        .collect { pipeline.transform(it) }

}

val loggingPipeline = object : Pipeline<String, Unit, Unit> {

    private val log = KotlinLogging.logger { }

    override val name: String = "Logging"

    override suspend fun transform(input: String) {
        log.info { "I am logging $input" }
    }

}

interface Pipeline<Input, Output, Error> {
    val name: String

    suspend fun transform(input: Input): Output

}