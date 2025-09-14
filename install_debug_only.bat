@echo on
setlocal
cd /d %~dp0
if not exist app\build\outputs\apk\debug\app-debug.apk (
  echo [ERROR] APK fehlt. Bitte zuerst build_debug_only.bat ausfuehren.
  goto :end
)
where adb >nul 2>nul
if %errorlevel% neq 0 (
  echo [ERROR] ADB nicht gefunden. Bitte Platform-Tools installieren.
  goto :end
)
adb devices
adb install -r app\build\outputs\apk\debug\app-debug.apk
:end
pause
endlocal
