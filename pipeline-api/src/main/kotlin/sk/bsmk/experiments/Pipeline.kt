package sk.bsmk.experiments

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import mu.KotlinLogging

data class Pipeline<Input, Output, TransformFailure>(
    val source: PipelineSource<Input>,
    val transformation: PipelineTransformation<Input, Output, TransformFailure>,
    val sink: PipelineSink<Output, Any>,
    val failureSink: PipelineSink<TransformFailure, Any>
) {

    private val log = KotlinLogging.logger { }

    suspend fun run() {

        val transformed = source.flow.map { transformation.transform(it) }

        val transformSuccesses = transformed.mapNotNull {
            when (it) {
                is TransformSuccess -> it
                is sk.bsmk.experiments.TransformFailure -> null
            }
        }
        val transformFailures = transformed.mapNotNull {
            when (it) {
                is TransformSuccess -> null
                is sk.bsmk.experiments.TransformFailure -> it
            }
        }

        collect(transformFailures.map { failureSink.drain(it.failure) })
        collect(transformSuccesses.map { sink.drain(it.output) })

    }

    private suspend fun <T> collect(results: Flow<DrainResult<T, Any>>) {
        results
            .mapNotNull {
                when (it) {
                    is DrainSuccess -> null
                    is DrainFailure -> it
                }
            }
            .collect { log.error { "Unable to store: ${it.input}: ${it.failure}" } }
    }
}
