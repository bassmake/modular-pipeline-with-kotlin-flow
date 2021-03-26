plugins {
    id("org.jetbrains.kotlin.jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    constraints {
        implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.14.1")
        implementation("io.github.microutils:kotlin-logging-jvm:2.0.6")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.3")
        testImplementation("io.kotest:kotest-runner-junit5:4.4.3")
    }

    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation(platform("io.ktor:ktor-bom:1.5.2"))

    testImplementation("io.kotest:kotest-runner-junit5")

}

tasks.test {
    useJUnitPlatform()
}


