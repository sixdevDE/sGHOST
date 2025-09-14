Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"
Set-Location $PSScriptRoot

# Ensure wrapper JAR
$jarPath = "gradle\wrapper\gradle-wrapper.jar"
if (-not (Test-Path $jarPath)) {
  Write-Host "Lade gradle-wrapper.jar ..."
  Invoke-WebRequest -Uri "https://repo1.maven.org/maven2/org/gradle/gradle-wrapper/8.9/gradle-wrapper-8.9.jar" -OutFile $jarPath
}

cmd /c ".\gradlew.bat --no-daemon assembleDebug"
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Write-Host "APK installieren..."
adb install -r "app\build\outputs\apk\debug\app-debug.apk"
