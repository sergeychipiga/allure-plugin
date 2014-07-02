package ru.yandex.qatools.allure.jenkins;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import ru.yandex.qatools.allure.jenkins.config.ReportBuildPolicy;

/**
 * User: eroshenkoam
 * Date: 10/9/13, 7:49 PM
 */
@Extension
public class AllureReportPublisherDescriptor extends BuildStepDescriptor<Publisher> {

    public AllureReportPublisherDescriptor() {
        super(AllureReportPublisher.class);
    }

    @Override
    public String getDisplayName() {
        return "Allure report processing";
    }

    @Override
    @SuppressWarnings("rawtypes")
    public boolean isApplicable(Class<? extends AbstractProject> jobType) {
        return true;
    }

    @SuppressWarnings("unused")
    public ReportBuildPolicy[] getReportBuildPolicies() {
        return ReportBuildPolicy.values();
    }


    @SuppressWarnings("unused")
    public String defaultResultsMask() {
        return AllureReportPlugin.DEFAULT_RESULTS_MASK;
    }

    @SuppressWarnings("unused")
    public String defaultReportVersion() {
        return AllureReportPlugin.DEFAULT_REPORT_VERSION;
    }
}
