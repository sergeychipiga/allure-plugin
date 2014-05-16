package ru.yandex.qatools.allure.jenkins;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;

/**
 * User: eroshenkoam
 * Date: 10/9/13, 7:49 PM
 */
@Extension
public class AllureReportPublisherDescriptor extends BuildStepDescriptor<Publisher> {

    public static final String DEFAULT_REPORT_PATH = "allure-report";

    public AllureReportPublisherDescriptor() {
        super(AllureReportPublisher.class);
    }

    @Override
    public String getDisplayName() {
        return "Publish Allure Tests Report";
    }

    @Override
    @SuppressWarnings("rawtypes")
    public boolean isApplicable(Class<? extends AbstractProject> jobType) {
        return true;
    }

    @SuppressWarnings("unused")
    public String defaultReportPath() {
        return DEFAULT_REPORT_PATH;
    }
}
