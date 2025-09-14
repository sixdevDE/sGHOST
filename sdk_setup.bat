@echo on
setlocal enableextensions enabledelayedexpansion
cd /d %~dp0

set SDK_DIR=.android-sdk
set TOOLS_ZIP=%SDK_DIR%\cmdline-tools.zip
set TOOLS_DIR=%SDK_DIR%\cmdline-tools
set TOOLS_BIN=%TOOLS_DIR%\latest\bin

if not exist "%SDK_DIR%" mkdir "%SDK_DIR%"

if not exist "%TOOLS_ZIP%" (
  echo [INFO] Lade Android cmdline-tools...
  where curl >nul 2>nul
  if %errorlevel%==0 (
    curl -L -o "%TOOLS_ZIP%" https://dl.google.com/android/repository/commandlinetools-win-11076708_latest.zip || goto :dl_fail
  ) else (
    powershell -NoLogo -NoProfile -Command "Invoke-WebRequest -Uri 'https://dl.google.com/android/repository/commandlinetools-win-11076708_latest.zip' -OutFile '%TOOLS_ZIP%'" || goto :dl_fail
  )
)

if not exist "%TOOLS_DIR%\latest" (
  echo [INFO] Entpacke cmdline-tools...
  powershell -NoLogo -NoProfile -Command "Expand-Archive -Force -LiteralPath '%TOOLS_ZIP%' -DestinationPath '%SDK_DIR%'" || goto :unzip_fail
  rem verschiebe in Struktur: cmdline-tools/latest/*
  if not exist "%TOOLS_DIR%\latest" mkdir "%TOOLS_DIR%\latest"
  xcopy /E /I /Y "%SDK_DIR%\cmdline-tools" "%TOOLS_DIR%\latest" >nul
)

setx ANDROID_HOME "%CD%\%SDK_DIR%"
setx ANDROID_SDK_ROOT "%CD%\%SDK_DIR%"
set "PATH=%CD%\%SDK_DIR%\platform-tools;%CD%\%SDK_DIR%\build-tools\34.0.0;%CD%\%TOOLS_BIN%;%PATH%"

echo [INFO] Akzeptiere Lizenzen und installiere Pakete...
echo y| "%TOOLS_BIN%\sdkmanager.bat" --sdk_root="%CD%\%SDK_DIR%" --licenses
"%TOOLS_BIN%\sdkmanager.bat" --sdk_root="%CD%\%SDK_DIR%" ^
  "platform-tools" ^
  "platforms;android-34" ^
  "build-tools;34.0.0" || goto :sdk_fail

echo sdk.dir=%CD%\\%SDK_DIR%> local.properties
echo [OK] SDK eingerichtet.
goto :end

:dl_fail
echo [ERROR] Download der Android cmdline-tools fehlgeschlagen.
goto :end
:unzip_fail
echo [ERROR] Entpacken der cmdline-tools fehlgeschlagen.
goto :end
:sdk_fail
echo [ERROR] SDK-Installation fehlgeschlagen.
goto :end
:end
pause
endlocal
