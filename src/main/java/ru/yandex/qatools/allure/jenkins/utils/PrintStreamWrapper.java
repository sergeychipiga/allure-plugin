package ru.yandex.qatools.allure.jenkins.utils;

import ru.yandex.qatools.allure.jenkins.AllureReportPlugin;

import java.io.PrintStream;

/**
 * eroshenkoam
 * 7/16/14
 */
public class PrintStreamWrapper {

    public static final String PREFIX = AllureReportPlugin.DESCRIPTION;

    private PrintStream printStream;

    public PrintStreamWrapper(PrintStream printStream) {
        this.printStream = printStream;
    }

    public void println(String message, Object... objects) {
        this.printStream.println(PREFIX.concat(": ").concat(String.format(message, objects)));
    }

    public PrintStream getPrintStream() {
        return printStream;
    }
}
