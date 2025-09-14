@ECHO OFF
SETLOCAL

set DIR=%~dp0
set APP_HOME=%DIR%

set CLASSPATH=%APP_HOME%gradle\wrapper\gradle-wrapper.jar

set DEFAULT_JVM_OPTS="-Xmx64m" "-Xms64m"

set JAVA_EXE=java.exe
where %JAVA_EXE% >NUL 2>&1
IF %ERRORLEVEL% NEQ 0 (
  ECHO.
  ECHO ERROR: Java nicht gefunden. Bitte JDK 17 in PATH setzen.
  EXIT /B 1
)

"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %GRADLE_OPTS% -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %*
ENDLOCAL
