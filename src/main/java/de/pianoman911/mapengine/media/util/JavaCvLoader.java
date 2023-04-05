package de.pianoman911.mapengine.media.util;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

public final class JavaCvLoader {

    private static final String JAVACV_VERSION = "1.5.8";
    private static final String FFMPEG_VERSION = "5.1.2";

    private static final String PAPER_REPO_URL = "https://repo.papermc.io/repository/maven-public/";
    private static final String GROUP_ID = "org.bytedeco";

    private static final Logger LOGGER = LoggerFactory.getLogger("MediaExtension");

    private JavaCvLoader() {
    }

    public static void addJavaCvPlatform(DirectMavenLibraryResolver resolver) {
        String os = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        os = os.contains("win") ? "windows" : os.contains("linux") ? "linux" : os.contains("mac") ? "macosx" : null;

        // https://github.com/bytedeco/javacpp-presets#the-cppbuildsh-scripts
        String arch = System.getProperty("os.arch").toLowerCase(Locale.ROOT);
        arch = arch.contains("amd") || arch.contains("x86") ? (arch.contains("64") ? "x86_64" : "x86")
                : arch.contains("aarch") || arch.contains("arm") ? (arch.contains("64") ? "arm64" : "arm") : null;

        if (os == null || arch == null) {
            throw new UnsupportedOperationException("Unsupported os/arch: "
                    + System.getProperty("os.name") + "/" + System.getProperty("os.arch"));
        }
        String nativeId = os + "-" + arch;

        DefaultArtifact javacppNatives = new DefaultArtifact(GROUP_ID, "javacpp", nativeId, "jar", JAVACV_VERSION);
        DefaultArtifact javacpp = new DefaultArtifact(GROUP_ID, "javacpp", "jar", JAVACV_VERSION);
        DefaultArtifact ffmpegNatives = new DefaultArtifact(GROUP_ID, "ffmpeg", nativeId, "jar", FFMPEG_VERSION + "-" + JAVACV_VERSION);
        DefaultArtifact ffmpeg = new DefaultArtifact(GROUP_ID, "ffmpeg", "jar", FFMPEG_VERSION + "-" + JAVACV_VERSION);
        DefaultArtifact javacv = new DefaultArtifact(GROUP_ID, "javacv", "jar", JAVACV_VERSION);

        resolver.addRepository(new RemoteRepository.Builder("paper", "default", PAPER_REPO_URL).build());
        resolver.addDependency(new Dependency(javacppNatives, null));
        resolver.addDependency(new Dependency(javacpp, null));
        resolver.addDependency(new Dependency(ffmpegNatives, null));
        resolver.addDependency(new Dependency(ffmpeg, null));
        resolver.addDependency(new Dependency(javacv, null));

        LOGGER.info("-----------------[ Dependency Loader ]-----------------");
        LOGGER.info("");
        LOGGER.info("Loading " + resolver.getDependencies().size() + " dependencies:");
        for (Dependency dependency : resolver.getDependencies()) {
            Artifact artifact = dependency.getArtifact();
            LOGGER.info("  - " + artifact.getGroupId() + ":" + artifact.getArtifactId() + ":" + artifact.getVersion()
                    + (StringUtils.isBlank(artifact.getClassifier()) ? "" : ":" + artifact.getClassifier()));
        }
        LOGGER.info("");
        LOGGER.info("Using " + resolver.getRepositories().size() + " repositories:");
        for (RemoteRepository repository : resolver.getRepositories()) {
            LOGGER.info("  - " + repository.getId() + ": " + repository.getUrl());
        }
        LOGGER.info("");
        LOGGER.info("-------------------------------------------------------");
    }
}
