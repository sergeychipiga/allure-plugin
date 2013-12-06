package ru.yandex.qatools.allure.jenkins;

import java.io.IOException;

/**
 * User: eroshenkoam
 * Date: 10/8/13, 6:19 PM
 */
public class AllureReportException extends IOException {

    private static final long serialVersionUID = 1L;

    public AllureReportException(String message) {
        super(message);
    }

}
