plugins {
    java
    application
}

val mainClassName = "io.github.syst3ms.skriptparser.Main"

repositories {
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    mavenCentral()
    jcenter()
}

dependencies {
    compile("org.jetbrains:annotations:15.0")
    implementation(group = "commons-cli", name = "commons-cli", version = "1.4")
    testImplementation("junit:junit:4.12")
    compile("org.spigotmc:spigot-api:1.15.2-R0.1-SNAPSHOT")
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
            .map { if (it.isDirectory) it else zipTree(it) })
    val sourcesMain = sourceSets.main.get()
    from(sourcesMain.output)
}
