rootProject.name = "modular-pipeline-with-kotlin-flow"
include(
    "app",
    "pipelines",
    "pipelines:ticker-pipeline-api", "pipelines:ticker-pipeline",
    "pipelines:fetcher-pipeline-api", "pipelines:fetcher-pipeline"
)
