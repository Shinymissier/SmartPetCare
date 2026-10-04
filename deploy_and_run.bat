@echo off
echo =========================================================================
echo Smart Pet Care and Vaccination Tracker - Build and Run
echo =========================================================================
set DIRNAME=%~dp0
cd /d "%DIRNAME%"

echo [1/3] Packaging Web Application WAR...
call mvnw.cmd package
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Maven build failed!
    pause
    exit /b %ERRORLEVEL%
)

echo [2/3] Deploying petcare.war to Apache Tomcat...
if exist "C:\apache-tomcat-9.0.120\apache-tomcat-9.0.120\webapps" (
    copy /y "target\petcare.war" "C:\apache-tomcat-9.0.120\apache-tomcat-9.0.120\webapps\petcare.war"
    echo Deployed to C:\apache-tomcat-9.0.120\apache-tomcat-9.0.120\webapps\petcare.war
) else (
    echo Note: Tomcat webapps directory not found at standard path. petcare.war is ready in target\petcare.war
)

echo [3/3] Ready!
echo Web Application URL: http://localhost:8080/petcare
echo Demo Credentials: demo@petcare.com / admin123
echo.
if exist "C:\apache-tomcat-9.0.120\apache-tomcat-9.0.120\bin\startup.bat" (
    echo Starting Apache Tomcat...
    call "C:\apache-tomcat-9.0.120\apache-tomcat-9.0.120\bin\startup.bat"
)
pause
