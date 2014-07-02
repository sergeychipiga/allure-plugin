package ru.yandex.qatools.allure.jenkins;

import java.io.File;
import java.io.InputStream;

import hudson.Plugin;
import hudson.PluginWrapper;
import hudson.model.AbstractBuild;
import hudson.model.Hudson;

/**
 * User: eroshenkoam
 * Date: 10/9/13, 8:29 PM
 */
public class AllureReportPlugin extends Plugin {

    public static final String ALLURE_TITLE = "Allure Report";

    public static final String ALLURE_URL_PATH = "allure";

    public static final String ALLURE_REPORT_PATH = "allure-report";

    public static File getBuildReportFolder(AbstractBuild<?, ?> build) {
        return build != null ? new File(build.getRootDir(), ALLURE_REPORT_PATH) : null;
    }

    public static String getAllureTitle() {
        return ALLURE_TITLE;
    }


    public static String getIconFilename() {
        PluginWrapper wrapper = Hudson.getInstance().getPluginManager().getPlugin(AllureReportPlugin.class);
        return String.format("/plugin/%s/img/icon.png", wrapper.getShortName());
    }
}
