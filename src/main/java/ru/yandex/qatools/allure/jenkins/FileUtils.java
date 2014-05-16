package ru.yandex.qatools.allure.jenkins;

import org.apache.tools.ant.DirectoryScanner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * eroshenkoam
 * 4/25/14
 */
public class FileUtils {

    private FileUtils() {

    }

    public static File[] findFilesByMask(File file, String[] mask) {
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(file);
        scanner.setIncludes(mask);
        scanner.setCaseSensitive(false);
        scanner.scan();

        String[] paths = scanner.getIncludedDirectories();
        File[] files = new File[paths.length];
        for (int i = 0; i < paths.length; i++) {
            files[i] = (new File(file, paths[i]));
        }
        return files;
    }
}
