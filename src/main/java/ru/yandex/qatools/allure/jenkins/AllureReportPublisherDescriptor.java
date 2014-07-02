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

    public static final String DEAFAULT_RESULTS_MASK = "**/allure-results";

    public static final String DEFAULT_REPORT_VERSION = "1.3.9";

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
        return DEAFAULT_RESULTS_MASK;
    }

    @SuppressWarnings("unused")
    public String defaultReportVersion() {
        return DEFAULT_REPORT_VERSION;
    }
}
