@echo off
setlocal enabledelayedexpansion

echo ===================================================
echo   HR SPHERE SAAS - AUTOMATIC STARTER
echo ===================================================

:: Check if JAVA_HOME is already set
if defined JAVA_HOME (
    if exist "%JAVA_HOME%\bin\java.exe" (
        echo Using existing JAVA_HOME: %JAVA_HOME%
        goto RUN_APP
    )
)

echo JAVA_HOME not detected. Searching for installed JDK...

:: Search common JDK paths
set "FOUND_JDK="

for /d %%D in ("C:\Program Files\Eclipse Adoptium\jdk*") do (
    if exist "%%D\bin\java.exe" set "FOUND_JDK=%%D"
)
if not defined FOUND_JDK (
    for /d %%D in ("C:\Program Files\Java\jdk*") do (
        if exist "%%D\bin\java.exe" set "FOUND_JDK=%%D"
    )
)
if not defined FOUND_JDK (
    for /d %%D in ("C:\Program Files\Amazon Corretto\jdk*") do (
        if exist "%%D\bin\java.exe" set "FOUND_JDK=%%D"
    )
)

if defined FOUND_JDK (
    echo Found JDK at: !FOUND_JDK!
    set "JAVA_HOME=!FOUND_JDK!"
    set "PATH=!JAVA_HOME!\bin;%PATH%"
    goto RUN_APP
)

echo.
echo [ERROR] JDK not found in standard paths!
echo Please install JDK 17 or JDK 21 and set JAVA_HOME.
pause
exit /b 1

:RUN_APP
echo.
echo Starting HR Sphere Application...
mvn spring-boot:run

pause
