plugins {
    id("sk.bsmk.experiments.kotlin-application-conventions")
}

dependencies {
    implementation(project(":pipelines"))
}

application {
    mainClass.set("sk.bsmk.experiments.app.AppKt")
}
