package ru.yandex.qatools.allure.jenkins;

import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import ru.yandex.qatools.allure.model.ModelProperties;

import java.io.File;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * User: eroshenkoam
 * Date: 11/6/13, 5:47 PM
 */
public class ReportPublishingTest {

    private static AllureResultsBuilder allureResultsBuilder;

    @BeforeClass
    public static void initResourceResultsFolder() {
        ModelProperties modelProperties = new ModelProperties();
        File results = new File(ClassLoader.getSystemResource(modelProperties.getResultsPath()).getFile());
        allureResultsBuilder = new AllureResultsBuilder(results);
    }

    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test
    public void first() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject();
        project.getBuildersList().add(allureResultsBuilder);
        AllureReportPublisher publisher = new AllureReportPublisher(AllureResultsBuilder.RESULTS_PATH, true);
        project.getPublishersList().add(publisher);

        FreeStyleBuild lastBuild = project.scheduleBuild2(0).get();
        assertThat(allureResultsBuilder.makeWorkspaceResultsPath(lastBuild).exists(), equalTo(true));

        assertThat(lastBuild.getWorkspace().child(AllureReportCollector.ALLURE_REPORT_GENERATION_FOLDER).exists(),
                equalTo(true));
    }
}
