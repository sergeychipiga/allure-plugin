package ru.yandex.qatools.allure.jenkins;

import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Recorder;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.io.IOUtils;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * User: eroshenkoam
 * Date: 10/8/13, 6:20 PM
 * <p/>
 * {@link AllureReportPublisherDescriptor}
 */
@SuppressWarnings("unchecked")
public class AllureReportPublisher extends Recorder implements Serializable {

    private static final long serialVersionUID = 1L;

    private final boolean alwaysGenerate;

    private final String resultsPath;

    @DataBoundConstructor
    public AllureReportPublisher(String resultsPath, boolean alwaysGenerate) {
        this.alwaysGenerate = alwaysGenerate;
        this.resultsPath = resultsPath;
    }

    public String getResultsPath() {
        return resultsPath;
    }

    public boolean getAlwaysGenerate() {
        return alwaysGenerate;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
            throws InterruptedException, IOException {

        final PrintStream logger = listener.getLogger();

        logger.println("Allure: started");

        if (!isNeedToBuildReport(build)) {
            logger.println("Allure: not analyzing allure report for a passed builds.");
            return true;
        }

        FilePath allureResults = build.getWorkspace().child(this.resultsPath);

        if (!allureResults.exists()) {
            logger.println(MessageFormat.format("Allure: tests results directory <{0}> doesn't exists.",
                    allureResults));
            build.setResult(Result.FAILURE);
            return true;
        }

        logger.println(MessageFormat.format("Allure: analyse tests results path <{0}>", this.resultsPath));
        FilePath generatedAllureReportData = generateAllureReportData(build, this.resultsPath);

        FilePath allureReport = new FilePath(build.getRootDir()).child(AllureReportPlugin.ALLURE_REPORT_PATH);
        generatedAllureReportData.copyRecursiveTo(allureReport);

        logger.println(MessageFormat.format("Allure: copy allure report face to <{0}>", allureReport));
        copyAllureReportFaceTo(allureReport);

        build.getActions().add(new AllureBuildAction(build));

        logger.println("Allure: complete");
        return true;
    }

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.STEP;
    }

    @Override
    public Collection<? extends Action> getProjectActions(AbstractProject<?, ?> project) {
        return Arrays.asList(new AllureProjectAction(project));
    }

    public boolean isNeedToBuildReport(AbstractBuild<?, ?> build) {
        return alwaysGenerate || build.getResult().isBetterThan(Result.FAILURE);
    }

    private FilePath generateAllureReportData(AbstractBuild<?, ?> build, String resultsPath)
            throws IOException, InterruptedException {
        AllureReportCollector collector = new AllureReportCollector(resultsPath);
        String generatedAllureReportDataPath = build.getWorkspace().act(collector);
        return new FilePath(build.getWorkspace(), generatedAllureReportDataPath);
    }

    private void copyAllureReportFaceTo(FilePath allureReport) throws IOException, InterruptedException {
        // FIXME: we need to put static contents only once and have the symlinks to save space
        for (Object resource : IOUtils.readLines(AllureReportPlugin.getResource("allure-contents.txt"))) {
            String resourceName = (String) resource;
            // FIXME: removing leading path component due to layout, may need a proper fix
            String destination = resourceName.replaceFirst("^[^\\/]*\\/", "");
            allureReport.child(destination).copyFrom(AllureReportPlugin.getResource(resourceName));
        }
    }

}
