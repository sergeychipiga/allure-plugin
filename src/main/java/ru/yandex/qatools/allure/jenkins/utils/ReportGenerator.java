package ru.yandex.qatools.allure.jenkins.utils;

import hudson.FilePath;
import hudson.remoting.VirtualChannel;
import ru.yandex.qatools.allure.report.AllureReportBuilder;

import java.io.File;
import java.io.IOException;

/**
 * eroshenkoam
 * 7/16/14
 */
public class ReportGenerator implements FilePath.FileCallable<FilePath> {

    public static final String RESULTS_PATH = "results";

    public static final String REPORT_PATH = "report";

    private String reportVersion;

    public ReportGenerator(String reportVersion) {
        this.reportVersion = reportVersion;
    }

    @Override
    public FilePath invoke(File f, VirtualChannel channel) throws IOException, InterruptedException {

        File resultsDirectory = new File(f, RESULTS_PATH);
        File reportDirectory = new File(f, REPORT_PATH);

        try {
            AllureReportBuilder allureReportBuilder = new AllureReportBuilder(reportVersion, reportDirectory);
            allureReportBuilder.processResults(resultsDirectory);
            allureReportBuilder.unpackFace();
        } catch (Exception e) {
            throw new IOException(e);
        }

        return new FilePath(reportDirectory);
    }
}
