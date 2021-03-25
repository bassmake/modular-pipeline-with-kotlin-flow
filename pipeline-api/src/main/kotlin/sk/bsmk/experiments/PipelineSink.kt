package sk.bsmk.experiments

interface PipelineSink<Input, Error> {
    val name: String

    suspend fun collect(input: Input)
}
