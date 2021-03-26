plugins {
    id("sk.bsmk.experiments.kotlin-application-conventions")
}

dependencies {
    implementation("org.apache.logging.log4j:log4j-slf4j-impl")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("io.github.microutils:kotlin-logging-jvm")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("io.ktor:ktor-client")
    implementation("io.ktor:ktor-client-cio")

    implementation(project(":pipeline-api"))
}

application {
    mainClass.set("sk.bsmk.experiments.PipelineRunnerKt")
}
