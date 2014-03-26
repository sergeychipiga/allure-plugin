## Allure Jenkins Plugin

Плагин, анализирующий allure-отчеты в Jenkins. 

Для использования нужно добавить в джобу Post-build action `Publish Allure Tests Report`, в котором указать директорию с xml-данными для отчетов.

После запуска джобы в билде появится ссылка `Allure Report`, указывающая на отчет для этого билда, а в джобе -- ссылка `Latest Allure Test Report`, указывающая на отчет последнего запуска.

Для генерации собственно xml-отчетов в джобе можно использовать адаптеры для тестов -- например, [allure-python](https://github.yandex-team.ru/allure/allure-python) или [allure-java](https://github.yandex-team.ru/allure/allure-java).


How to make & install

Требуется:
Maven
Java 1.7*

1. git clone allure-jenkins-plugin
2. cd ./allure-jenkins-plugin && mvn package
3. cp allure-jenkins-plugin/target/allure-jenkins-plugin.hpi $JENKINS_HOME/plugins/
4. touch $JENKINS_HOME/plugins/allure-jenkins-plugin.hpi.pinned
5. restart jenkins service

* также на хосте с Jenkins.
