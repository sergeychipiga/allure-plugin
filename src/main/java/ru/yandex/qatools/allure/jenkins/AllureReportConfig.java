package ru.yandex.qatools.allure.jenkins;

import org.kohsuke.stapler.DataBoundConstructor;
import ru.yandex.qatools.allure.jenkins.config.ReportBuildPolicy;
import ru.yandex.qatools.allure.jenkins.config.ReportVersionPolicy;

/**
 * eroshenkoam
 * 30/07/14
 */
public class AllureReportConfig {

    private final String resultsPattern;

    private final String reportVersionCustom;

    private final ReportBuildPolicy reportBuildPolicy;

    private final ReportVersionPolicy reportVersionPolicy;

    @DataBoundConstructor
    public AllureReportConfig(String resultsPattern, String reportVersionCustom,
                              ReportVersionPolicy reportVersionPolicy, ReportBuildPolicy reportBuildPolicy) {

        this.reportVersionPolicy = reportVersionPolicy;
        this.reportVersionCustom = reportVersionCustom;
        this.reportBuildPolicy = reportBuildPolicy;
        this.resultsPattern = resultsPattern;
    }

    public String getResultsPattern() {
        return resultsPattern;
    }

    public String getReportVersionCustom() {
        return reportVersionCustom;
    }

    public ReportVersionPolicy getReportVersionPolicy() {
        return reportVersionPolicy;
    }

    public ReportBuildPolicy getReportBuildPolicy() {
        return reportBuildPolicy;
    }

    public static AllureReportConfig newInstance(String resultsMask, boolean alwaysGenerate) {
        return new AllureReportConfig(resultsMask, null, ReportVersionPolicy.DEFAULT,
                alwaysGenerate ? ReportBuildPolicy.ALWAYS : ReportBuildPolicy.UNSTABLE);
    }
}
