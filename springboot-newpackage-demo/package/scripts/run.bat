@echo off

set CLASSPATH=.
FOR %%F IN (libs/*.jar) DO call :updateClassPath %%F  
goto :startjava  
:updateClassPath
set CLASSPATH=%CLASSPATH%;libs/%1
goto :eof

:startjava
java -server -cp %CLASSPATH% app.App

pause
