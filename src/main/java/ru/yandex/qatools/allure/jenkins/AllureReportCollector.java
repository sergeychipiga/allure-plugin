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
 */
public class AllureReportCollector implements FileCallable<String> {

    private static final long serialVersionUID = 1L;

    private final String reportPath;

    private final String[] resultsMask;

    /**
     * @param reportPath relative report path
     * @param resultsMask ant-like file mask of what to include
     */
    public AllureReportCollector(String resultsMask, String reportPath) {
        this.resultsMask = resultsMask.split(";");
        this.reportPath = reportPath;
    }


    /**
     * @return the relative to <b>f</b> path of generated report directory
     */
    @Override
    public String invoke(final File f, VirtualChannel channel) throws IOException, InterruptedException {
        File[] allureResultDirectoryList = FileUtils.findFilesByMask(f, resultsMask);
        File allureOutputDirectory = new File(f, reportPath);

        if (allureResultDirectoryList.length == 0) {
            throw new AllureReportException(String.format("Can't access allure input folders by <%s>", resultsMask));
        }

        if (!(allureOutputDirectory.exists() || allureOutputDirectory.mkdirs())) {
            throw new AllureReportException(String.format("Can't create allure output directory <%s>",
                    allureOutputDirectory.getAbsolutePath()));
        }

        AllureReportGenerator allureReportGenerator = new AllureReportGenerator(allureResultDirectoryList);
        allureReportGenerator.generate(allureOutputDirectory);

        return reportPath;
    }
}
