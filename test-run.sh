
#set SELENIUM_REMOTE_URL=http://localhost:4444
#SELENIUM_REMOTE_URL=http://localhost:4444 mvn test

#set SELENIUM_REMOTE_URL=http://localhost:4444
#SELENIUM_REMOTE_URL=http://localhost:4444 mvn test -Dbrowser=firefox
#SELENIUM_REMOTE_URL=http://localhost:4444 mvn test -Dbrowser=webkit
SELENIUM_REMOTE_URL=http://localhost:4444 mvn test -Dbrowser=chromium

sleep 10m