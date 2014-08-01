# Allure Jenkins Plugin
This plugin allows to automatically generate Allure report and attach it to build during Jenkins job run.

![image](https://raw.github.com/allure-framework/allure-core/master/allure-dashboard.png)

## Configuration
 * Open a job configuration page
 * Add the "Allure Report Generation" post build action

![configuration](https://raw.githubusercontent.com/allure-framework/allure-jenkins-plugin/master/img/allure-configuration.png)

## Usage
When finished a link to Allure report will appear on the build page:

![configuration](https://raw.githubusercontent.com/allure-framework/allure-jenkins-plugin/master/img/allure-sidebar.png)
![configuration](https://raw.githubusercontent.com/allure-framework/allure-jenkins-plugin/master/img/allure-report.png)

## Default Settings
 * Open Jenkins global configuration
 * Find "Allure Settings" configuration block
 
![configuration](https://raw.githubusercontent.com/allure-framework/allure-jenkins-plugin/master/img/allure-settings.png)


## Develop
To build the plugin you need to have any JDK 1.7+ and [Apache Maven](http://maven.apache.org/).
 * Setup maven settings for jenkins plugin development: https://wiki.jenkins-ci.org/display/JENKINS/Plugin+tutorial
 * Execute the following command: `$ mvn clean package`

## Contact us
Mailing list: [allure@yandex-team.ru](mailto:allure@yandex-team.ru)
