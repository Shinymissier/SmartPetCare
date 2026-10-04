@echo off
rem Maven Wrapper launcher script for Smart Campus Management System
set DIRNAME=%~dp0
if not defined MAVEN_HOME (
    if exist "%USERPROFILE%\.m2\apache-maven-3.9.6\bin\mvn.cmd" (
        set "MAVEN_HOME=%USERPROFILE%\.m2\apache-maven-3.9.6"
    )
)
if defined MAVEN_HOME (
    "%MAVEN_HOME%\bin\mvn.cmd" %*
) else (
    mvn %*
)
