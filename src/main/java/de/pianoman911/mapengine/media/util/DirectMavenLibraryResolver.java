package de.pianoman911.mapengine.media.util;

import io.papermc.paper.plugin.loader.library.ClassPathLibrary;
import io.papermc.paper.plugin.loader.library.LibraryLoadingException;
import io.papermc.paper.plugin.loader.library.LibraryStore;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.graph.DefaultDependencyNode;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.repository.RepositoryPolicy;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResolutionException;
import org.eclipse.aether.resolution.DependencyResult;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transfer.AbstractTransferListener;
import org.eclipse.aether.transfer.TransferEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DirectMavenLibraryResolver implements ClassPathLibrary {

    private static final Logger LOGGER = LoggerFactory.getLogger("MavenResolver");

    private final RepositorySystem repository;
    private final DefaultRepositorySystemSession session;
    private final List<RemoteRepository> repositories = new ArrayList<>();
    private final List<Dependency> dependencies = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public DirectMavenLibraryResolver() {
        DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();

        try {
            locator.addService(RepositoryConnectorFactory.class, (Class<? extends RepositoryConnectorFactory>)
                    Class.forName("org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory"));
            locator.addService(TransporterFactory.class, (Class<? extends TransporterFactory>)
                    Class.forName("org.eclipse.aether.transport.http.HttpTransporterFactory"));
        } catch (ClassNotFoundException exception) {
            throw new RuntimeException(exception);
        }

        this.repository = locator.getService(RepositorySystem.class);
        this.session = MavenRepositorySystemUtils.newSession();

        this.session.setChecksumPolicy(RepositoryPolicy.CHECKSUM_POLICY_FAIL);
        this.session.setLocalRepositoryManager(this.repository.newLocalRepositoryManager(this.session, new LocalRepository("libraries")));
        this.session.setTransferListener(new AbstractTransferListener() {
            @Override
            public void transferInitiated(@NotNull TransferEvent event) {
                LOGGER.info("Downloading {}{}...", event.getResource().getRepositoryUrl(), event.getResource().getResourceName());
            }
        });
        this.session.setReadOnly();
    }

    @Override
    public void register(@NotNull LibraryStore store) throws LibraryLoadingException {
        List<RemoteRepository> repos = this.repository.newResolutionRepositories(this.session, this.repositories);

        try {
            for (Dependency dependency : this.dependencies) {
                DefaultDependencyNode node = new DefaultDependencyNode(dependency);
                node.setRepositories(repos);

                DependencyResult result = this.repository.resolveDependencies(
                        this.session, new DependencyRequest(node, null));

                for (ArtifactResult artifact : result.getArtifactResults()) {
                    File file = artifact.getArtifact().getFile();
                    store.addLibrary(file.toPath());
                }
            }
        } catch (DependencyResolutionException exception) {
            throw new LibraryLoadingException("Error resolving maven libraries", exception);
        }
    }

    public void addDependency(Dependency dependency) {
        this.dependencies.add(dependency);
    }

    public void addRepository(RemoteRepository remoteRepository) {
        this.repositories.add(remoteRepository);
    }

    public List<Dependency> getDependencies() {
        return this.dependencies;
    }

    public List<RemoteRepository> getRepositories() {
        return this.repositories;
    }
}
