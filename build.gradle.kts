plugins {
    java
    application
}

group = "edu.illinois.abhayp4.brain_project"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation("org.yaml:snakeyaml:2.3")
    implementation("com.fasterxml.jackson.core:jackson-core:2.18.0")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.18.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.18.0")
}

application {
    mainClass.set("edu.illinois.abhayp4.brain_project.main.Main")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()    
    maxHeapSize = "1G"

    testLogging {
        events("passed", "failed", "skipped")
        showStandardStreams = true
    }
}