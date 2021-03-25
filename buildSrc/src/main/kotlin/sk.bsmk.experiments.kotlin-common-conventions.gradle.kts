plugins {
    id("org.jetbrains.kotlin.jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    constraints {
        implementation("org.apache.commons:commons-text:1.9")
        testImplementation("io.kotest:kotest-runner-junit5:4.4.3")
    }

    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.3")
    testImplementation("io.kotest:kotest-runner-junit5")

}

tasks.test {
    useJUnitPlatform()
}
