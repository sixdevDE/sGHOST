@echo on
setlocal enableextensions enabledelayedexpansion
cd /d %~dp0

call .\sdk_setup.bat || goto :end

call .\build_portable_gradle.bat || goto :end

if not exist app\build\outputs\apk\debug\app-debug.apk (
  echo [ERROR] APK nicht gefunden.
  goto :end
)

where adb >nul 2>nul
if %errorlevel% neq 0 (
  echo [WARN] ADB nicht gefunden. Ueberspringe Installation.
  goto :end
)

adb devices
adb install -r app\build\outputs\apk\debug\app-debug.apk || echo [WARN] ADB-Installation fehlgeschlagen.
:end
pause
endlocal
