package sk.bsmk.experiments

import kotlinx.coroutines.flow.Flow

interface PipelineSource<Output> {
    val name: String

    val flow: Flow<Output>
}
