@echo off
setlocal
set DIR=%~dp0
set JAVA_EXE=java
"%JAVA_EXE%" -jar "%DIR%gradle\wrapper\gradle-wrapper.jar" %*
