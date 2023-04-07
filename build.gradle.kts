plugins {
    id("java-library")
    id("maven-publish")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "de.pianoman911"
version = "1.0.5"

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.thejocraft.net/releases/")
}

dependencies {
    compileOnlyApi("io.papermc.paper:paper-api:1.19.4-R0.1-SNAPSHOT")
    compileOnlyApi("de.pianoman911:mapengine-api:1.4.3")
    api("org.bstats:bstats-bukkit:3.0.2")

    compileOnlyApi("org.bytedeco:javacpp:1.5.8")
    compileOnlyApi("org.bytedeco:ffmpeg:1.5.8")
    compileOnlyApi("org.bytedeco:ffmpeg:5.1.2-1.5.8")
    compileOnlyApi("org.bytedeco:javacv:1.5.8") { isTransitive = false }

}

tasks {
    shadowJar {
        destinationDirectory.set(rootProject.buildDir.resolve("libs"))
        archiveBaseName.set(rootProject.name)

        relocate("org.bstats", "de.pianoman911.mapengine.media.bstats")
    }

    assemble {
        dependsOn(shadowJar)
    }

    processResources {
        filesMatching("paper-plugin.yml") {
            expand(
                "version" to project.version,
            )
        }
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