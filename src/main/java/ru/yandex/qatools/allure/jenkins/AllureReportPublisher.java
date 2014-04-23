package ru.yandex.qatools.allure.jenkins;

import hudson.FilePath;
import hudson.Launcher;
import hudson.matrix.MatrixAggregatable;
import hudson.matrix.MatrixAggregator;
import hudson.matrix.MatrixRun;
import hudson.matrix.MatrixBuild;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.Result;
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

import org.apache.commons.io.IOUtils;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * User: eroshenkoam
 * Date: 10/8/13, 6:20 PM
 * <p/>
 * {@link AllureReportPublisherDescriptor}
 */
@SuppressWarnings("unchecked")
public class AllureReportPublisher extends Recorder implements Serializable, MatrixAggregatable {

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
    
    public MatrixAggregator createAggregator(MatrixBuild build, Launcher launcher, BuildListener listener) {
        return new MatrixAggregator(build, launcher, listener) {
        	
        	public static final String ALLURE_MATRIX_TEMP_DIR = "allure_matrix_temp";
        	
        	@Override
        	public boolean endBuild() throws InterruptedException, IOException {
            	FilePath dst = new FilePath(build.getWorkspace(), AllureReportPublisher.this.resultsPath);
            	if(dst.exists() && dst.getRemote() != build.getWorkspace().getRemote()){
            		dst.deleteRecursive();
            	}
            	
            	for(MatrixRun run : build.getExactRuns()) {
            		FilePath src = new FilePath(run.getWorkspace(), AllureReportPublisher.this.resultsPath);
            		if(dst.isRemote()){
	            		FilePath tempMasterDir = new FilePath(build.getRootDir()).child(ALLURE_MATRIX_TEMP_DIR);
	            		try {
		            		src.copyRecursiveTo(tempMasterDir);
		            		tempMasterDir.copyRecursiveTo(dst);
	            		}
	            		finally {
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
