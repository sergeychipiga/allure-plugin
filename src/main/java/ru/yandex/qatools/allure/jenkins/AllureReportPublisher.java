package ru.yandex.qatools.allure.jenkins;

import hudson.FilePath;
import hudson.Launcher;
import hudson.matrix.MatrixAggregatable;
import hudson.matrix.MatrixAggregator;
import hudson.matrix.MatrixRun;
import hudson.matrix.MatrixBuild;
import hudson.model.*;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Recorder;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.kohsuke.stapler.DataBoundConstructor;
import ru.yandex.qatools.allure.jenkins.actions.AllureBuildAction;
import ru.yandex.qatools.allure.jenkins.actions.AllureProjectAction;
import ru.yandex.qatools.allure.jenkins.config.AllureReportConfig;
import ru.yandex.qatools.allure.jenkins.utils.PropertiesSaver;
import ru.yandex.qatools.allure.jenkins.utils.ReportGenerator;
import ru.yandex.qatools.allure.jenkins.config.ReportBuildPolicy;
import ru.yandex.qatools.allure.jenkins.config.ReportVersionPolicy;
import ru.yandex.qatools.allure.jenkins.utils.PrintStreamWrapper;

import static ru.yandex.qatools.allure.jenkins.AllureReportPlugin.getReportBuildDirectory;
import static ru.yandex.qatools.allure.jenkins.utils.GlobDirectoryFinder.findDirectoriesByGlob;

/**
 * User: eroshenkoam
 * Date: 10/8/13, 6:20 PM
 * <p/>
 * {@link AllureReportPublisherDescriptor}
 */
@SuppressWarnings("unchecked")
public class AllureReportPublisher extends Recorder implements Serializable, MatrixAggregatable {

    private static final long serialVersionUID = 1L;

    private final AllureReportConfig config;

    @Deprecated
    private String resultsMask;

    @Deprecated
    private boolean alwaysGenerate;

    @DataBoundConstructor
    public AllureReportPublisher(AllureReportConfig config) {
        this.config = config;
    }

    public AllureReportConfig getConfig() {
        return config == null ? AllureReportConfig.newInstance(resultsMask, alwaysGenerate) : config;
    }

    @Deprecated
    public String getResultsMask () {
        return resultsMask;
    }

    @Deprecated
    public boolean getAlwaysGenerate() {
        return alwaysGenerate;
    }

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.STEP;
    }

    @Override
    public Collection<? extends Action> getProjectActions(AbstractProject<?, ?> project) {
        return Arrays.asList(new AllureProjectAction(project));
    }

    public AllureReportPublisherDescriptor getDescriptor() {
        return (AllureReportPublisherDescriptor) super.getDescriptor();
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
            throws InterruptedException, IOException {

        PrintStreamWrapper logger = new PrintStreamWrapper(listener.getLogger());
        FilePath allureFilePath = build.getWorkspace().createTempDir("allure", null);

        logger.println("started");
        ReportBuildPolicy reportBuildPolicy = getConfig().getReportBuildPolicy();
        if (!reportBuildPolicy.isNeedToBuildReport(build)) {
            logger.println("report generation reject by policy [%s]", reportBuildPolicy.getTitle());
            return true;
        }

        String resultsPattern = getConfig().getResultsPattern();
        logger.println("find directories by mask [%s]", resultsPattern);
        List<FilePath> resultsFilePaths = build.getWorkspace().act(findDirectoriesByGlob(resultsPattern));
        logger.println("found allure result directories %s", Arrays.toString(resultsFilePaths.toArray()));


        if (resultsFilePaths.size() == 0) {
            logger.println("can't find allure results directories");
            return false;
        }

        FilePath resultsFilePath = allureFilePath.child(ReportGenerator.RESULTS_PATH);
        logger.println("copy founded directories in directory [%s]", resultsFilePath);
        for (FilePath filePath : resultsFilePaths) {
            filePath.copyRecursiveTo(resultsFilePath);
        }
        resultsFilePath.createTempFile("allure", "-environment.properties").
                act(new PropertiesSaver(build.getBuildVariables(), "Build Properties"));

        generateReport(build, allureFilePath, logger);
        publishReport(build, logger);
        delete(allureFilePath, logger);
        logger.println("completed");

        return true;
    }

    public MatrixAggregator createAggregator(MatrixBuild build, Launcher launcher, BuildListener listener) {

        return new MatrixAggregator(build, launcher, listener) {

            @Override
            public boolean endBuild() throws InterruptedException, IOException {

                PrintStreamWrapper logger = new PrintStreamWrapper(listener.getLogger());

                logger.println("started");
                ReportBuildPolicy reportBuildPolicy = getConfig().getReportBuildPolicy();
                if (!reportBuildPolicy.isNeedToBuildReport(build)) {
                    logger.println("project build reject by policy [%s]", reportBuildPolicy.getTitle());
                    return true;
                }

                FilePath allureFilePath = build.getRootBuild().getWorkspace().createTempDir("allure", null);
                FilePath tmpResultsDirectory = allureFilePath.child(ReportGenerator.RESULTS_PATH);

                logger.println("copy matrix builds results in directory [%s]", tmpResultsDirectory);

                String resultsPattern = getConfig().getResultsPattern();
                for (MatrixRun run : build.getExactRuns()) {
                    List<FilePath> resultsDirectories = run.getWorkspace().act(findDirectoriesByGlob(resultsPattern));
                    for (FilePath resultsDirectory : resultsDirectories) {
                        resultsDirectory.copyRecursiveTo(tmpResultsDirectory);
                    }
                }

                if (tmpResultsDirectory.getUsableDiskSpace() == 0) {
                    logger.println("results directory [%s] is empty", tmpResultsDirectory);
                    return true;
                }

                generateReport(build, allureFilePath, logger);
                publishReport(build, logger);
                delete(allureFilePath, logger);

                logger.println("completed");
                return true;
            }
        };
    }

    private void generateReport(AbstractBuild<?, ?> build, FilePath allureFilePath, PrintStreamWrapper logger)
            throws IOException, InterruptedException {

        logger.println("generate report from directory [%s]", allureFilePath);
        FilePath reportFilePath = new FilePath(getReportBuildDirectory(build));
        String reportVersion = getConfig().getReportVersionPolicy().equals(ReportVersionPolicy.CUSTOM) ?
                getConfig().getReportVersionCustom() : getDescriptor().getReportVersionDefault();
        allureFilePath.act(new ReportGenerator(reportVersion)).copyRecursiveTo(reportFilePath);
    }

    private void publishReport(AbstractBuild<?, ?> build, PrintStreamWrapper logger) {
        logger.println("attach report link to build and project");
        build.getActions().add(new AllureBuildAction(build));
    }

    private void delete(FilePath filePath, PrintStreamWrapper logger) {
        try {
            filePath.deleteContents();
            filePath.deleteRecursive();
        } catch (IOException | InterruptedException e) {
            logger.println("Can't delete directory [%s]", filePath);
            e.printStackTrace(logger.getPrintStream());
        }
    }

}
