import net.minecrell.pluginyml.paper.PaperPluginDescription
import java.io.ByteArrayOutputStream
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.Temporal
import java.util.stream.Stream

plugins {
    id("java-library")
    id("maven-publish")
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("net.minecrell.plugin-yml.paper") version "0.6.0"
}

group = "de.pianoman911"
version = "1.1.2"

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.thejocraft.net/releases/")
}

dependencies {
    compileOnlyApi("io.papermc.paper:paper-api:1.19.4-R0.1-SNAPSHOT")
    compileOnlyApi("de.pianoman911:mapengine-api:1.8.0")
    api("org.bstats:bstats-bukkit:3.0.2")

    compileOnlyApi("org.bytedeco:javacpp:1.5.10")
    compileOnlyApi("org.bytedeco:ffmpeg:6.1.1-1.5.10")
    compileOnlyApi("org.bytedeco:javacv:1.5.10") { isTransitive = false }

}

val gitHash = git("rev-parse --short HEAD")
val gitBranch = git("rev-parse --abbrev-ref HEAD")
val gitTag = git("describe --tags --abbrev=0")

fun git(git: String): String {
    val out = ByteArrayOutputStream()
    rootProject.exec {
        commandLine(Stream.concat(Stream.of("git"), git.split(" ").stream()).toList())
        standardOutput = out
    }
    return out.toString().trim()
}

val compileTime: Temporal = ZonedDateTime.now(ZoneOffset.UTC)
val compileDate: String = DateTimeFormatter.ISO_DATE_TIME.format(compileTime)

tasks {
    shadowJar {
        destinationDirectory.set(rootProject.buildDir.resolve("libs"))
        archiveBaseName.set(rootProject.name)

        relocate("org.bstats", "de.pianoman911.mapengine.media.bstats")
    }

    assemble {
        dependsOn(shadowJar)
    }

    jar {
        manifest.attributes(
                "Implementation-Title" to rootProject.name,
                "Implementation-Vendor" to "pianoman911",
                "Implementation-Version" to project.version,
                "License" to "AGPL-3.0",

                "Build-Date" to compileDate,
                "Build-Timestamp" to compileTime.toString(),

                "Git-Commit" to gitHash,
                "Git-Branch" to gitBranch,
                "Git-Tag" to gitTag,
        )
    }
}

publishing {
    publications.create<MavenPublication>("maven${project.name}") {
        artifactId = project.name.lowercase()
        from(components["java"])
    }
    repositories.maven("https://repo.thejocraft.net/releases/") {
        name = "tjcserver"
        authentication { create<BasicAuthentication>("basic") }
        credentials(PasswordCredentials::class)
    }
}

paper {
    main = "$group.mapengine.media.MapEngineMedia"
    loader = "$group.mapengine.media.MediaExtensionLoader"
    apiVersion = "1.19"
    authors = listOf("pianoman911")

    name = "MapMediaExt"
    description = "$gitHash/$gitBranch ($gitTag), $compileDate"

    serverDependencies {
        register("MapEngine") {
            load = PaperPluginDescription.RelativeLoadOrder.AFTER
        }
    }
}