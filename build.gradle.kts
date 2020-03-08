plugins {
    java
    application
}

val mainClassName = "io.github.syst3ms.skriptparser.Main"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation("com.google.guava:guava:27.0.1-jre")
    compile("org.jetbrains:annotations:15.0")
    compile(group = "com.google.code.findbugs", name = "jsr305", version = "3.0.2")
    implementation(group = "commons-cli", name = "commons-cli", version = "1.4")

    testImplementation("junit:junit:4.12")
}

tasks.create<Jar>("fatJar") {
    archiveClassifier.set("all")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest {
        attributes["Main-Class"] = mainClassName
        attributes["Implementation-Title"] = "skript-parser"
        attributes["Implementation-Version"] = "alpha"
    }

    from(configurations.runtimeClasspath.get()
            .onEach { println("add from dependencies: ${it.name}") }
            .map { if (it.isDirectory) it else zipTree(it) })
    val sourcesMain = sourceSets.main.get()
    sourcesMain.allSource.forEach { println("add from sources: ${it.name}") }
    from(sourcesMain.output)
}
