package ru.yandex.qatools.allure.jenkins;

import hudson.FilePath.FileCallable;
import hudson.remoting.VirtualChannel;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.repository.internal.DefaultArtifactDescriptorReader;
import org.apache.maven.repository.internal.DefaultVersionRangeResolver;
import org.apache.maven.repository.internal.DefaultVersionResolver;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.connector.file.FileRepositoryConnectorFactory;
import org.eclipse.aether.connector.wagon.WagonProvider;
import org.eclipse.aether.connector.wagon.WagonRepositoryConnectorFactory;
import org.eclipse.aether.impl.*;
import org.eclipse.aether.internal.impl.*;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.LocalRepositoryManager;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.log.LoggerFactory;
import org.eclipse.aether.spi.log.NullLoggerFactory;
import ru.yandex.qatools.allure.jenkins.utils.FileUtils;
import ru.yandex.qatools.allure.report.AllureReportBuilder;
import ru.yandex.qatools.allure.report.utils.AetherObjectFactory;
import ru.yandex.qatools.allure.report.utils.DependencyResolver;
import ru.yandex.qatools.allure.report.utils.ManualWagonProvider;

import static ru.yandex.qatools.allure.report.utils.AetherObjectFactory.newDependencyResolver;
import static ru.yandex.qatools.allure.report.utils.AetherObjectFactory.newRemoteRepository;


/**
 * {@link FileCallable} that performs actual allure report processing on the remote slave
 *
 * @author pupssman
 */
public class AllureReportCollector implements FileCallable<String> {

    private static final long serialVersionUID = 1L;

    private final String reportPath = "allure";

    private final String reportVersion;

    private final String[] resultsMask;

    /**
     * @param reportVersion allur ereport version
     * @param resultsMask   ant-like file mask of what to include
     */
    public AllureReportCollector(String resultsMask, String reportVersion) {
        this.resultsMask = resultsMask.split(";");
        this.reportVersion = reportVersion;
    }


    /**
     * @return the relative to <b>f</b> path of generated report directory
     */
    @Override
    public String invoke(final File f, VirtualChannel channel) throws IOException, InterruptedException {
        File[] resultsDirectories = FileUtils.findFilesByMask(f, resultsMask);
        File reportDirectory = new File(f, reportPath);
        File mavenLocalDirectory = new File(f, "repository");

        if (resultsDirectories.length == 0) {
            throw new AllureReportException(String.format("Can't access allure input folders by <%s>", resultsMask));
        }

        if (!(reportDirectory.exists() || reportDirectory.mkdirs())) {
            throw new AllureReportException(String.format("Can't create allure output directory <%s>",
                    reportDirectory.getAbsolutePath()));
        }

        try {
            DependencyResolver dependencyResolver = newDependencyResolver(mavenLocalDirectory,
                    AetherObjectFactory.MAVEN_CENTRAL_URL);
            AllureReportBuilder allureReportBuilder = new AllureReportBuilder(reportVersion, reportDirectory, dependencyResolver);
            allureReportBuilder.processResults(resultsDirectories);
            allureReportBuilder.unpackFace();
        } catch (Exception e) {
            throw new IOException(e);
        }
        return reportPath;
    }
}
