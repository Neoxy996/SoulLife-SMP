@echo off
set GRADLE_USER_HOME=%USERPROFILE%\.gradle
"%JAVA_HOME%\bin\java" -jar "%GRADLE_USER_HOME%\wrapper\dists\gradle-8.4-bin\*\gradle-8.4\lib\gradle-launcher-8.4.jar" %*
