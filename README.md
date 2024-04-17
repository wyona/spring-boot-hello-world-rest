README
======

Minimal Spring Boot based RESTful 'Hello World' example, including Swagger (using Springfox) and Docker
(Also see https://spring.io/guides/gs/rest-service/)

Requirements
------------

* JDK: 1.11 (e.g. on Mac OS X: export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-11.0.11.jdk/Contents/Home)
* Maven version: 3.3.3 (Please double check Maven .m2/settings.xml)
* Either Tomcat: 8 (please see build.sh)
* or Docker (please see below)
* or from command line

Configuration
-------------

* Configure mail server inside src/main/resources/application.properties, for example
  * app.mail.host=mail.wyona.com
  * app.mail.port=587
  * app.mail.username=USERNAME
  * app.mail.password=PASSWORD

Build and deploy inside Tomcat
------------------------------

* sh build.sh
* http://127.0.0.1:8080/hello-world-webapp-1.0.0-SNAPSHOT/

Build and run from command line
-------------------------------

* mvn clean install
* java -jar target/hello-world-webapp-1.0.0-SNAPSHOT.war
* http://localhost:8383/ (see server.port inside src/main/resources/application.properties)

Docker
------

* Build webapp as jar file (see pom.xml)
* Start Docker
* Build image: docker build -t spring-boot-hello-world .
* Show images: docker images
** Remove image: docker rmi -f IMAGE ID
* Run image: docker run -p 8383:8383 spring-boot-hello-world
* Show docker processes: docker ps
* Stop specific docker process: docker stop CONTAINER ID
* Request in browser: http://127.0.0.1:8383/swagger-ui.html

Specification and Testing
-------------------------

https://www.yulup.com/en/projects/fe937e1d-3bb4-4012-a963-04848bd955ba/index.html

IntelliJ IDEA
-------------
* Start IntelliJ
* File -> New -> Project from Version Control -> Git
** https://github.com/wyona/spring-boot-hello-world-rest
*** https://github.com/wyona/spring-boot-hello-world-rest.git
* Set JDK: File -> Project Structure -> Project SDK: 11
* Reimport All Maven Projects
* Run clean/install
* Startup Server
* http://127.0.0.1:8383
