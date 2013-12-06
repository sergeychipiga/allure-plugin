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

    public static final String ALLURE_URL_PATH = "allure";

    public static final String ALLURE_REPORT_PATH = "allure-reports";

    public static final String ALLURE_REPORT_DATA_PATH = "data";

    public static File getBuildReportFolder(AbstractBuild<?, ?> build) {
        return build != null ? new File(build.getRootDir(), ALLURE_REPORT_PATH) : null;
    }

    public static ClassLoader getClassLoader() {
        return Hudson.getInstance().getPluginManager().getPlugin(AllureReportPlugin.class).classLoader;
    }

    public static InputStream getResource(String resource) {
        return getClassLoader().getResourceAsStream(resource);
    }

    public static String getIconFilename() {
        PluginWrapper wrapper = Hudson.getInstance().getPluginManager().getPlugin(AllureReportPlugin.class);
        return String.format("/plugin/%s/img/icon.png", wrapper.getShortName());
    }
}
