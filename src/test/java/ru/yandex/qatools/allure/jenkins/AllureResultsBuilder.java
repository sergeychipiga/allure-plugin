package ru.yandex.qatools.allure.jenkins;

import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import org.jvnet.hudson.test.TestBuilder;

import java.io.File;
import java.io.IOException;

/**
 * User: eroshenkoam
 * Date: 11/6/13, 8:04 PM
 */
public class AllureResultsBuilder extends TestBuilder {

    public final static String RESULTS_PATH = "allure-results";

    private final boolean markBuildFailed;
    private final File resourceResultsFolder;

    public AllureResultsBuilder(File resourceResultsFolder) {
        this(resourceResultsFolder, true);
    }

    public AllureResultsBuilder(File resourceResultsFolder, boolean markBuildFailed) {
        this.resourceResultsFolder = resourceResultsFolder;
        this.markBuildFailed = markBuildFailed;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> abstractBuild, Launcher launcher, BuildListener buildListener)
            throws InterruptedException, IOException {
        new FilePath(resourceResultsFolder).copyRecursiveTo(makeWorkspaceResultsPath(abstractBuild));
        return !markBuildFailed;
    }

    public FilePath makeWorkspaceResultsPath(AbstractBuild<?, ?> build) {
        return makeWorkspaceResultsPath(build.getWorkspace());
    }

    public FilePath makeWorkspaceResultsPath(FilePath workspace) {
        return workspace.child(RESULTS_PATH);
    }

}
