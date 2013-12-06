package ru.yandex.qatools.allure.jenkins;

import hudson.FilePath;
import hudson.model.Action;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.DirectoryBrowserSupport;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 * {@link Action} that server allure report from archive directory on master of a given build.
 *
 * @author pupssman
 */
public class AllureBuildAction implements Action {

    private final AbstractBuild<?, ?> build;

    public AllureBuildAction(AbstractBuild<?, ?> build) {
        this.build = build;
    }

    @Override
    public String getIconFileName() {
        return AllureReportPlugin.getIconFilename();
    }

    @Override
    public String getDisplayName() {
        return "Allure Report";
    }

    @Override
    public String getUrlName() {
        return AllureReportPlugin.ALLURE_URL_PATH;
    }

    @SuppressWarnings("unused")
    public DirectoryBrowserSupport doDynamic(StaplerRequest req, StaplerResponse rsp)
            throws IOException, ServletException, InterruptedException {
        AbstractProject<?, ?> project = build.getProject();
        FilePath systemDirectory = new FilePath(AllureReportPlugin.getBuildReportFolder(build));
        return new DirectoryBrowserSupport(this, systemDirectory, project.getDisplayName(), null, false);
    }

}
