package ru.yandex.qatools.allure.jenkins;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.tools.ant.DirectoryScanner;
import org.junit.Test;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;

/**
 * eroshenkoam
 * 4/25/14
 */
public class SimpleTest {

    @Test
    public void testOutput() {
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setIncludes(new String[]{"**/target"});
        scanner.setBasedir("/Users/eroshenkoam/Developer/allure-framework/allure-jenkins-plugin/work/jobs/allure-report-generation/workspace");
        scanner.setCaseSensitive(false);
        scanner.scan();
        for (String path : scanner.getIncludedDirectories()) {
            System.out.println(path);
        }

    }
}
