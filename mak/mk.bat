cd /d %~dp0
cd ..
call mvn clean source:jar install -Dmaven.test.skip -offline
pause
