#!/bin/bash

TOMCAT_HOME=/Users/michaelwechner/local/apache-tomcat-8.0.26

if [ ! -d $TOMCAT_HOME ];then
    echo "ERROR: No Tomcat installed at $TOMCAT_HOME"
    exit 0
fi

echo "INFO: Build 'Hello World' webapp ..."
mvn clean install -Dmaven.test.skip=true
#mvn clean install
#mvn -X clean install

echo "INFO: Deploy webapp ..."
rm -rf $TOMCAT_HOME/webapps/hello-world-webapp-*
rm -rf $TOMCAT_HOME/work/Catalina/localhost/hello-world-webapp-*
cp target/hello-world-webapp-1.0.0-SNAPSHOT.war $TOMCAT_HOME/webapps/.

echo "INFO: Clean log files ..."
rm -f $TOMCAT_HOME/logs/*

echo "INFO: Startup Tomcat '$TOMCAT_HOME' and access the 'Hello World' webapp at 'http://127.0.0.1:8080/hello-world-webapp-1.0.0-SNAPSHOT/' ...."
