# TEAM Engine  Statistics
This repository provides code to get statistics reports about the tests being exercised in a TEAM Engine installation.

## Preparation

1. Go the folder in local folder. For example ```cd github/teamengine-statistics```
2. Update to the latest ```git pull```
1. Compile the code: ``` mvn clean install ```

## Running
1. locate where the users folder is. For example ~/users_prod
2. Run: ```java -cp target/stats-1.0-SNAPSHOT-with-deps.jar org.opengis.te.stats.TEReport ~/users_prod```

## The following classes can be invoked

1. AdminLogCreator.java ==> This creates the statistic report and if some session has problem with the log.xml and session.xml file then reported in the "/result-output/AdmiLog.log" file.

2. StatisticsCreator.java ==> It will update the session file if the date attribute is not present in the users session with the file creation time. It will resolve the null date attribute issue.

3.TEReport.java ==> This will provide the report with the comma separated value: 
userName|session|date|year|month|testName|overallResult


Currently reports look like the following:

```
Test Name: Catalogue Service - Web (CSW)_3.0.0
Last Month:21 | Last 3 Months:134 | Last Year:450 | All Times:1045
```



We expect to genereate reports like the following:

![Distribution of test in specific time](diff-test-exec.png)

![Distribution of a test per time](exec-test-over-time.png)



## Running the reporter

- First compile the AdminLogCreator.java file as: javac AdminLogCreator.java
- Then run javac AdminLogCreator.class file as: java AdminLogCreator ${path-of-user-folder}
