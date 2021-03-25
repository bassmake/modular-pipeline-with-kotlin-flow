package sk.bsmk.experiments

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

        transformFailures.map { failureSink.collect(it.failure) }
            .mapNotNull {
                when (it) {
                    is CollectSuccess -> null
                    is CollectFailure -> it
                }
            }
            .collect { log.error { "Unable to store failure: $it" } }

        transformSuccesses
            .map { sink.collect(it.output) }
            .mapNotNull {
                when (it) {
                    is CollectSuccess -> null
                    is CollectFailure -> it
                }
            }
            .collect { log.error { "Unable to store success: $it" } }
    }
}
