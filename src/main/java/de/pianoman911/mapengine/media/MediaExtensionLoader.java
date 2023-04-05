package de.pianoman911.mapengine.media;

import de.pianoman911.mapengine.media.util.DirectMavenLibraryResolver;
import de.pianoman911.mapengine.media.util.JavaCvLoader;
import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import org.jetbrains.annotations.NotNull;

public class MediaExtensionLoader implements PluginLoader {

    @Override
    public void classloader(@NotNull PluginClasspathBuilder classpathBuilder) {
        DirectMavenLibraryResolver resolver = new DirectMavenLibraryResolver();
        JavaCvLoader.addJavaCvPlatform(resolver);
        classpathBuilder.addLibrary(resolver);
    }
}
