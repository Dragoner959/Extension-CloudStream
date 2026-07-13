@echo off
setlocal
set DIR=%~dp0
call "%DIR%gradle-8.7\bin\gradle.bat" %*
