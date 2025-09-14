@echo on
setlocal enableextensions enabledelayedexpansion
cd /d %~dp0

if not exist gradle\wrapper mkdir gradle\wrapper
if not exist gradle\wrapper\gradle-wrapper.jar (
  echo [INFO] Lade gradle-wrapper.jar ...
  where curl >nul 2>nul
  if %errorlevel%==0 (
    curl -L -o gradle\wrapper\gradle-wrapper.jar https://repo1.maven.org/maven2/org/gradle/gradle-wrapper/8.9/gradle-wrapper-8.9.jar || goto :dl_fail
  ) else (
    powershell -NoLogo -NoProfile -Command "Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/org/gradle/gradle-wrapper/8.9/gradle-wrapper-8.9.jar' -OutFile 'gradle/wrapper/gradle-wrapper.jar'" || goto :dl_fail
  )
)

call .\gradlew.bat --no-daemon assembleDebug || goto :build_fail

echo [OK] APK: app\build\outputs\apk\debug\app-debug.apk
goto :end

:dl_fail
echo [ERROR] Download der gradle-wrapper.jar fehlgeschlagen.
goto :end
:build_fail
echo [ERROR] Gradle-Build fehlgeschlagen.
goto :end
:end
pause
endlocal
