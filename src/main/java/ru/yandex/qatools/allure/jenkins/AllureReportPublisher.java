package ru.yandex.qatools.allure.jenkins;

import com.google.common.base.Strings;
import hudson.FilePath;
import hudson.Launcher;
import hudson.matrix.MatrixAggregatable;
import hudson.matrix.MatrixAggregator;
import hudson.matrix.MatrixRun;
import hudson.matrix.MatrixBuild;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Recorder;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;

import org.kohsuke.stapler.DataBoundConstructor;
import ru.yandex.qatools.allure.jenkins.config.ReportBuildPolicy;

/**
 * User: eroshenkoam
 * Date: 10/8/13, 6:20 PM
 * <p/>
 * {@link AllureReportPublisherDescriptor}
 */
@SuppressWarnings("unchecked")
public class AllureReportPublisher extends Recorder implements Serializable, MatrixAggregatable {

    private static final long serialVersionUID = 1L;

    private final String resultsMask;

    private final String reportVersion;

    private final String reportVersionPolicy;

    private final ReportBuildPolicy reportBuildPolicy;

    @DataBoundConstructor
    public AllureReportPublisher(String resultsMask, String reportVersion,
                                 String reportVersionPolicy, String reportBuildPolicy) {
        this.reportBuildPolicy = ReportBuildPolicy.valueOf(reportBuildPolicy);
        this.reportVersionPolicy = reportVersionPolicy;
        this.reportVersion = reportVersion;
        this.resultsMask = resultsMask;
    }

    @SuppressWarnings("unused")
    public String getResultsMask() {
        return resultsMask;
    }

    @SuppressWarnings("unused")
    public String getReportVersion() {
        return reportVersion;
    }

    @SuppressWarnings("unused")
    public String getReportVersionPolicy() {
        return reportVersionPolicy;
    }

    @SuppressWarnings("unused")
    public String getReportBuildPolicy() {
        return reportBuildPolicy.name();
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
            throws InterruptedException, IOException {

        final PrintStream logger = listener.getLogger();

        logger.println("Allure: started");

        if (!reportBuildPolicy.isNeedToBuildReport(build)) {
            logger.println(String.format("Allure: project build rejected by policy [%s]",
                    reportBuildPolicy.getTitle()));
            return true;
        }

        logger.println(MessageFormat.format("Allure: analyse tests results path <{0}>", this.resultsMask));

        String allureReportVersion = Strings.isNullOrEmpty(this.reportVersion) ?
                getDescriptor().defaultReportVersion() : this.reportVersion;

        FilePath generatedAllureReportData = generateAllureReportData(build, this.resultsMask, allureReportVersion);

        FilePath allureReport = new FilePath(build.getRootDir()).child(AllureReportPlugin.REPORT_PATH);
        generatedAllureReportData.copyRecursiveTo(allureReport);
        logger.println(MessageFormat.format("Allure: copy allure report face to <{0}>", allureReport));

        build.getActions().add(new AllureBuildAction(build));
        generatedAllureReportData.deleteContents();
        generatedAllureReportData.deleteRecursive();

        logger.println("Allure: complete");
        return true;
    }

    public MatrixAggregator createAggregator(MatrixBuild build, Launcher launcher, BuildListener listener) {
        return new MatrixAggregator(build, launcher, listener) {

            public static final String ALLURE_MATRIX_TEMP_DIR = "allure_matrix_temp";

            @Override
            public boolean endBuild() throws InterruptedException, IOException {
                FilePath dst = new FilePath(build.getWorkspace(), AllureReportPublisher.this.resultsMask);
                if (dst.exists() && !dst.getRemote().equals(build.getWorkspace().getRemote())) {
                    dst.deleteRecursive();
                }

                for (MatrixRun run : build.getExactRuns()) {
                    FilePath src = new FilePath(run.getWorkspace(), AllureReportPublisher.this.resultsMask);
                    if (dst.isRemote()) {
                        FilePath tempMasterDir = new FilePath(build.getRootDir()).child(ALLURE_MATRIX_TEMP_DIR);
                        try {
                            src.copyRecursiveTo(tempMasterDir);
                            tempMasterDir.copyRecursiveTo(dst);
                        } finally {
                            if (tempMasterDir.exists()) {
                                tempMasterDir.deleteRecursive();
                            }
                        }
                    } else {
                        src.copyRecursiveTo(dst);
                    }
                }
                return perform(build, launcher, listener);
            }
        };
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

    private FilePath generateAllureReportData(AbstractBuild<?, ?> build, String resultsMask, String reportVersion)
            throws IOException, InterruptedException {

        AllureReportCollector collector = new AllureReportCollector(resultsMask, reportVersion);
        String generatedAllureReportDataPath = build.getWorkspace().act(collector);
        return new FilePath(build.getWorkspace(), generatedAllureReportDataPath);
    }

}
