# Allure Jenkins Plugin
This plugin allows to automatically generate [Allure report](http://allure.qatools.ru) and attach it to build during Jenkins job run.

![image](https://raw.github.com/allure-framework/allure-core/master/allure-dashboard.png)

## Installation
 * Open "Manage Jenkins" > "Manage Plugins" > "Available"
 * Find "Allure Report" plugin
 * Install plugin

## Configuration
 * Open job configuration page
 * Add "Allure Report Generation" post build action

![configuration](https://raw.githubusercontent.com/allure-framework/allure-jenkins-plugin/master/img/allure-configuration.png)

## Usage
When build is finished a link to Allure report will appear on the build page:

![configuration](https://raw.githubusercontent.com/allure-framework/allure-jenkins-plugin/master/img/allure-sidebar.png)
![configuration](https://raw.githubusercontent.com/allure-framework/allure-jenkins-plugin/master/img/allure-report.png)

## Default Settings
 * Open Jenkins global configuration
 * Find "Allure Settings" configuration block
 
![configuration](https://raw.githubusercontent.com/allure-framework/allure-jenkins-plugin/master/img/allure-settings.png)


## Develop
To build the plugin you need to have any JDK 1.7+ and [Apache Maven](http://maven.apache.org/).
 * Setup maven settings for Jenkins plugin development: https://wiki.jenkins-ci.org/display/JENKINS/Plugin+tutorial
 * Execute the following command: `$ mvn clean package`

## Contact us
Mailing list: [allure@yandex-team.ru](mailto:allure@yandex-team.ru)
