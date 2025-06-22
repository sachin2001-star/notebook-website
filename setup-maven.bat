@echo off
echo Downloading Maven...
powershell -Command "& {[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri 'https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.zip' -OutFile 'maven.zip'}"

echo Extracting Maven...
powershell -Command "Expand-Archive -Path 'maven.zip' -DestinationPath '.' -Force"

echo Setting up Maven...
set MAVEN_HOME=%CD%\apache-maven-3.9.6
set PATH=%MAVEN_HOME%\bin;%PATH%

echo Testing Maven...
mvn --version

echo Starting Spring Boot application...
mvn spring-boot:run

pause