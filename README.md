# Allure Jenkins Plugin

This plugin allows to automatically generate Allure report and attach it to build during Jenkins job run.

## Usage
This plugin adds a new post-build action **Publish Allure Tests Report** which generates Allure report. The only setting for the step is the name of directory with XML files storing input data for Allure. In order to generate those XML files you need to attach Allure adapter to your favorite testing framework. See [wiki](https://github.com/allure-framework/allure-core/wiki) for more details on how to do this. When the build is finished a link to report (**Allure Report**) will appear on its page whereas build page will point to the latest generated Allure report (**Latest Allure Test Report**).

## Building

To build the plugin you need to have any JDK 1.7+ and [Apache Maven](http://maven.apache.org/). When installed simply execute the following command:
```
$ mvn clean package
```
If you're building Jenkins plugins for the first time you also need to add entries to **settings.xml** file as described on the [page](https://wiki.jenkins-ci.org/display/JENKINS/Plugin+tutorial#Plugintutorial-SettingUpEnvironment).

## Installing
After building the plugin you can install the plugin by direct upload of the file **target/allure-jenkins-plugin.hpi** (see [instructions](https://wiki.jenkins-ci.org/display/JENKINS/Plugins#Plugins-Usingtheinterface)).
