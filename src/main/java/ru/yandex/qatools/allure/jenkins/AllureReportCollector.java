package ru.yandex.qatools.allure.jenkins;

import hudson.FilePath.FileCallable;
import hudson.remoting.VirtualChannel;

import java.io.File;
import java.io.IOException;

import ru.yandex.qatools.allure.data.AllureReportGenerator;


/**
 * {@link FileCallable} that performs actual allure report processing on the remote slave
 *
 * @author pupssman
 *
 */
public class AllureReportCollector implements FileCallable<String> {

    public static final String ALLURE_REPORT_GENERATION_FOLDER = "target/site/allure-report";

    private static final long serialVersionUID = 1L;

    private final String reportFiles;

    /**
     * @param reportFiles ant-like file mask of what to include
     */
    public AllureReportCollector(String reportFiles) {
        this.reportFiles = reportFiles;
    }

    /**
     * @return the relative to <b>f</b> path of generated report directory
     */
    @Override
    public String invoke(final File f, VirtualChannel channel) throws IOException, InterruptedException {

        File allureOutputDirectory = new File(f, ALLURE_REPORT_GENERATION_FOLDER);
        File allureInputDirectory = new File(f, reportFiles);

        if (!allureInputDirectory.exists() || !allureInputDirectory.isDirectory() || !allureInputDirectory.canRead()) {
            throw new AllureReportException(String.format("Can't access allure input directory <%s>",
                    allureInputDirectory.getAbsolutePath()));
        }

        if (!(allureOutputDirectory.exists() || allureOutputDirectory.mkdirs())) {
            throw new AllureReportException(String.format("Can't create allure output directory <%s>",
                    allureOutputDirectory.getAbsolutePath()));
        }

        AllureReportGenerator allureReportGenerator = new AllureReportGenerator(allureInputDirectory);
        allureReportGenerator.generate(allureOutputDirectory);

        return ALLURE_REPORT_GENERATION_FOLDER;
    }
}
