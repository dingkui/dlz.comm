@echo off
setlocal enabledelayedexpansion

echo ========================================
echo JDK Compatibility Test Script
echo ========================================
echo.

echo Current Environment:
echo.
echo Checking Java version...
java -version 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [WARNING] Java not found or error occurred
)
echo.

echo Checking Maven version...
call mvn -v 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Maven not found or error occurred
    echo Please install Maven and add it to PATH
    pause
    exit /b 1
)
echo.

REM Test JDK 8
call :test_jdk 8
call :test_jdk 11
call :test_jdk 17
call :test_jdk 21

echo ========================================
echo Compatibility Test Completed!
echo ========================================
pause
exit /b 0

REM ========================================
REM Function: test_jdk
REM Parameters: %1 = JDK version (8, 11, 17, 21)
REM ========================================
:test_jdk
set JDK_VERSION=%1

echo ========================================
echo Testing JDK %JDK_VERSION% Compatibility
echo ========================================

echo [Running] mvn clean compile -Pjdk%JDK_VERSION%-test ...
call mvn clean compile -Pjdk%JDK_VERSION%-test >nul 2>&1
if !ERRORLEVEL! NEQ 0 (
    echo [FAIL] JDK %JDK_VERSION% compilation failed
    if "%JDK_VERSION%"=="8" goto :eof
    goto :eof_function
) else (
    echo [OK] JDK %JDK_VERSION% compilation succeeded
)

echo [Running] mvn test -Pjdk%JDK_VERSION%-test ...
call mvn test -Pjdk%JDK_VERSION%-test >nul 2>&1
if !ERRORLEVEL! NEQ 0 (
    echo [FAIL] JDK %JDK_VERSION% tests failed
) else (
    echo [OK] JDK %JDK_VERSION% tests succeeded
)
echo.

:eof_function
exit /b 0
