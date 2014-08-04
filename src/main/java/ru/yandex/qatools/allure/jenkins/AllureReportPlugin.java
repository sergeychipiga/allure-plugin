package ru.yandex.qatools.allure.jenkins;

import java.io.File;

import hudson.Plugin;
import hudson.PluginWrapper;
import hudson.model.AbstractBuild;
import jenkins.model.Jenkins;

/**
 * User: eroshenkoam
 * Date: 10/9/13, 8:29 PM
 */
public class AllureReportPlugin extends Plugin {

    public static final String TITLE = "Allure Report";

    public static final String DESCRIPTION = "Allure Report Generation";

    public static final String URL_PATH = "allure";

    public static final String REPORT_PATH = "allure-reports";

    public static final String DEFAULT_RESULTS_PATTERN = "**/allure-results";

    public static final String DEFAULT_REPORT_VERSION = "1.3.9";

    public static File getReportBuildDirectory(AbstractBuild<?, ?> build) {
        return build != null ? new File(build.getRootDir(), REPORT_PATH) : null;
    }

    public static String getTitle() {
        return TITLE;
    }

    public static String getIconFilename() {
        PluginWrapper wrapper = Jenkins.getInstance().getPluginManager().getPlugin(AllureReportPlugin.class);
        return String.format("/plugin/%s/img/icon.png", wrapper.getShortName());
    }


}
