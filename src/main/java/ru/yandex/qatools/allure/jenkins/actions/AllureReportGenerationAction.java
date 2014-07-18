package ru.yandex.qatools.allure.jenkins.actions;

import hudson.FilePath;
import hudson.remoting.VirtualChannel;
import ru.yandex.qatools.allure.report.AllureReportBuilder;
import ru.yandex.qatools.allure.report.utils.AetherObjectFactory;
import ru.yandex.qatools.allure.report.utils.DependencyResolver;

import java.io.File;
import java.io.IOException;

import static ru.yandex.qatools.allure.report.utils.AetherObjectFactory.newDependencyResolver;

/**
 * eroshenkoam
 * 7/16/14
 */
public class AllureReportGenerationAction implements FilePath.FileCallable<FilePath> {

    public static final String REPOSITORIES_PATH = "repositories";

    public static final String RESULTS_PATH = "results";

    public static final String REPORT_PATH = "report";

    private String reportVersion;

    public AllureReportGenerationAction(String reportVersion) {
        this.reportVersion = reportVersion;
    }

    @Override
    public FilePath invoke(File f, VirtualChannel channel) throws IOException, InterruptedException {

        File repositoriesDirectory = new File(f, REPOSITORIES_PATH);
        File resultsDirectory = new File(f, RESULTS_PATH);
        File reportDirectory = new File(f, REPORT_PATH);

        try {
            DependencyResolver dependencyResolver = newDependencyResolver(repositoriesDirectory,
                    AetherObjectFactory.MAVEN_CENTRAL_URL, "https://oss.sonatype.org/content/repositories/releases/");
            AllureReportBuilder allureReportBuilder = new AllureReportBuilder(reportVersion, reportDirectory,
                    dependencyResolver);
            allureReportBuilder.processResults(resultsDirectory);
            allureReportBuilder.unpackFace();
        } catch (Exception e) {
            throw new IOException(e);
        }

        return new FilePath(reportDirectory);
    }
}
