@echo off

set ATCS_DIR=%~dp0
set MAX_MEM=512M
set CP=%ATCS_DIR%lib\*
set JAVA=javaw.exe
set JAVA_OPTS=
set ENV_FILE=%ATCS_DIR%ATCS.env.bat
set MAIN_CLASS=com.gpl.rpg.atcontentstudio.ATContentStudio

if exist %ENV_FILE% (
  call %ENV_FILE%
) else (
  echo REM set MAX_MEM=%MAX_MEM% > %ENV_FILE%
  echo REM set JAVA=%JAVA% >> %ENV_FILE%
  echo REM set JAVA_OPTS=%JAVA_OPTS% >> %ENV_FILE%
  echo. >> %ENV_FILE%
)

start "" "%JAVA%" %JAVA_OPTS% -Xmx%MAX_MEM% -cp "%CP%" %MAIN_CLASS%
