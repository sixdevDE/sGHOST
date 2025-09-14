@echo on
setlocal enableextensions enabledelayedexpansion
cd /d %~dp0

set GRADLE_DIR=.gradle-portable
set ZIP_PATH=%GRADLE_DIR%\gradle-8.9-bin.zip

if not exist %GRADLE_DIR% mkdir %GRADLE_DIR%

if not exist "%ZIP_PATH%" (
  echo [INFO] Lade Gradle 8.9...
  where curl >nul 2>nul
  if %errorlevel%==0 (
    curl -L -o "%ZIP_PATH%" https://services.gradle.org/distributions/gradle-8.9-bin.zip || goto :dl_fail
  ) else (
    powershell -NoLogo -NoProfile -Command "Invoke-WebRequest -Uri 'https://services.gradle.org/distributions/gradle-8.9-bin.zip' -OutFile '%ZIP_PATH%'" || goto :dl_fail
  )
)

if not exist "%GRADLE_DIR%\gradle-8.9" (
  echo [INFO] Entpacke Gradle...
  powershell -NoLogo -NoProfile -Command "Expand-Archive -Force -LiteralPath '%ZIP_PATH%' -DestinationPath '%GRADLE_DIR%'"  || goto :unzip_fail
)

echo [INFO] Starte Build...
set PATH=%CD%\%GRADLE_DIR%\gradle-8.9\bin;%PATH%
gradle --version || goto :gradle_fail
gradle --no-daemon assembleDebug || goto :build_fail

echo [OK] APK: app\build\outputs\apk\debug\app-debug.apk
goto :end

:dl_fail
echo [ERROR] Download von Gradle fehlgeschlagen.
goto :end
:unzip_fail
echo [ERROR] Entpacken von Gradle fehlgeschlagen.
goto :end
:gradle_fail
echo [ERROR] Portable Gradle konnte nicht gestartet werden.
goto :end
:build_fail
echo [ERROR] Build fehlgeschlagen.
goto :end
:end
pause
endlocal
