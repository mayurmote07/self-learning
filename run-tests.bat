@echo off
REM Run Maven Tests for Kafka Learning Project
REM This script properly handles paths with spaces

setlocal enabledelayedexpansion

set MAVEN_CMD="C:\Program Files\JetBrains\IntelliJ IDEA 2026.1\plugins\maven\lib\maven3\bin\mvn.cmd"

echo.
echo ============================================
echo   Kafka Learning Project - Test Suite
echo ============================================
echo.

cd /d "%~dp0"

REM Run all tests
echo Running all unit tests...
echo.

%MAVEN_CMD% clean test -DskipITs

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ============================================
    echo   ✅ All tests passed!
    echo ============================================
    echo.
) else (
    echo.
    echo ============================================
    echo   ❌ Some tests failed!
    echo ============================================
    echo.
)

endlocal

