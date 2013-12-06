package ru.yandex.qatools.allure.jenkins;

import org.kohsuke.stapler.StaplerProxy;

import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.ProminentProjectAction;


/**
 * {@link Action} that shows link to the allure report of last successful build on the project page
 *
 * @author pupssman
 */
public class AllureProjectAction implements ProminentProjectAction, StaplerProxy {
    private final AbstractProject<?, ?> project;

    public AllureProjectAction(AbstractProject<?, ?> project) {
        this.project = project;
    }

    @Override
    public String getIconFileName() {
        return this.getTarget() != null ? AllureReportPlugin.getIconFilename() : null;
    }

    @Override
    public String getDisplayName() {
        return "Latest Allure Test Report";
    }

    @Override
    public String getUrlName() {
        return AllureReportPlugin.ALLURE_URL_PATH;
    }

    @Override
    public Object getTarget() {
        AbstractBuild<?, ?> build = project.getLastSuccessfulBuild();
        return build != null ? build.getAction(AllureBuildAction.class) : null;
    }
}
