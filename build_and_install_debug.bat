@echo on
setlocal enableextensions enabledelayedexpansion
cd /d %~dp0

echo =============================================
echo sGHOST - Build and Install (Debug)
echo =============================================

REM Ensure gradle wrapper JAR exists
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

echo [INFO] Java Version:
java -version

echo [INFO] Gradle Build: assembleDebug
call .\gradlew.bat --no-daemon assembleDebug || goto :build_fail

echo [INFO] APK Pfad: app\build\outputs\apk\debug\app-debug.apk
if not exist app\build\outputs\apk\debug\app-debug.apk (
  echo [ERROR] APK nicht gefunden. Build fehlgeschlagen?
  goto :end
)

echo [INFO] Installiere per ADB...
where adb >nul 2>nul
if %errorlevel% neq 0 (
  echo [WARN] ADB nicht gefunden. Ueberspringe Installation.
  goto :end
)

adb devices
adb install -r app\build\outputs\apk\debug\app-debug.apk || goto :adb_fail
echo [OK] Installation abgeschlossen.
goto :end

:dl_fail
echo [ERROR] Download der gradle-wrapper.jar fehlgeschlagen.
goto :end

:build_fail
echo [ERROR] Gradle-Build fehlgeschlagen.
goto :end

:adb_fail
echo [ERROR] ADB-Installation fehlgeschlagen. Tipp: 
echo   adb kill-server ^&^& adb start-server ^&^& adb devices
echo   oder Emulator neu starten.
goto :end

:end
echo.
echo ====== Vorgang beendet. Fenster schliesst mit Taste. ======
pause
endlocal
