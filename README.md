# MapEngine Media Extension

<img src="https://i.imgur.com/7YyUEBQ.png" alt="logo" width="200">

[![GitHub tag (latest by date)](https://img.shields.io/github/v/tag/TheJoCraftNET/MapEngine-MediaExtension?style=flat-square)](#)
[![AGPLv3 License](https://img.shields.io/badge/License-AGPL%20v3-yellow.svg?style=flat-square)](https://opensource.org/license/agpl-v3/)
[![Status Beta](https://img.shields.io/badge/Status-Beta-orange?style=flat-square)](#)

## Description

MapEngine Media Extension is an extension library for [MapEngine](https://github.com/TheJoCraftNET/MapEngine). 
It provides an additional API for playing video and streaming live content using MapEngine.

The Extension uses [bytedeco/javacv's](https://github.com/bytedeco/javacv) FFmpeg implementation for decode media.
JavaCV, JavaCPP and FFmpeg will be downloaded on server startup and loaded into the classpath.
This plugin downloads only the currently needed libraries for the current operating system and architecture.

## Features

- Runtime dependency downloader
- FFmpeg based media decoding

<details>
<summary><strong>Downloading native libraries</strong></summary>

This is an example of native libraries being downloaded on server startup.

![RuntimeDependencyLoading](https://i.imgur.com/GMWH9NW.gif)

</details>

<details>
<summary><strong>Live streaming via RTMP</strong></summary>

This is an example of a live stream on a 7x4 map in Minecraft.
The stream source is 1920x1080@20fps streamed with OBS.

[![Watch it here](https://i.imgur.com/h1e9ROE.png)](https://youtu.be/5tg_DX84eLw)

</details>

## Usage

`MapMediaExt` has to be added as a dependency to the `plugin.yml` regardless of the build system used.

<details>
<summary><strong>Maven</strong></summary>

```xml
<repositories>
    <repository>
        <id>tjcserver</id>
        <url>https://repo.thejocraft.net/releases/</url>
    </repository>
</repositories>
```

```xml
<dependencies>
    <dependency>
        <groupId>de.pianoman911</groupId>
        <artifactId>mapengine-mediaext</artifactId>
        <version>1.1.4</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```
</details>


<details>
<summary><strong>Gradle (groovy)</strong></summary>

```groovy
repositories {
    maven {
        url = 'https://repo.thejocraft.net/releases/'
        name = 'tjcserver'
    }
}

dependencies {
    compileOnly 'de.pianoman911:mapengine-mediaext:1.1.4'
}
```

</details>

<details>
<summary><strong>Gradle (kotlin)</strong></summary>

```kotlin
repositories {
    maven("https://repo.thejocraft.net/releases/") {
        name = "tjcserver"
    }
}

dependencies {
    compileOnly("de.pianoman911:mapengine-mediaext:1.1.4")
}
```

</details>

### Example

```java
public class Bar {

    public void foo(IMapDisplay display, URI streamUri) {
        // create a drawing space for the display
        IDrawingSpace space = plugin.mapEngine().pipeline().drawingSpace(display);
        
        // add all online players as receivers
        space.ctx().receivers().addAll(Bukkit.getOnlinePlayers());

        // create a new frame source with a 10 frame buffer and rescaling enabled
        FfmpegFrameSource source = new FfmpegFrameSource(streamUri, 10, space, true);
        
        // start the decoding process
        source.start(); 
    }
}
```

More detailed examples can be found in the [TheJoCraftNET/MapEngineExamples](https://github.com/TheJoCraftNET/MapEngineExamples) repository.

## Building

1. Clone the project (`git clone https://github.com/TheJoCraftNET/MapEngine-MediaExtension.git`)
2. Go to the cloned directory (`cd MapEngine-MediaExtension`)
3. Build the Jar (`./gradlew build` on Linux/MacOS, `gradlew build` on Windows)

The plugin jar can be found in the `build` → `libs` directory.
