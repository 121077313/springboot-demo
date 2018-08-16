#!/bin/sh  

LIB=./libs
for jar in $LIB/*.*  
do  
CLASSPATH=$CLASSPATH:$jar  
done

case "$1" in  
  
  start)  
   java -jar -server libs/springboot-newpackage-demo-1.0.jar &
      
    echo $! > server.pid  
    ;;  
  
  stop)  
    kill `cat server.pid`  
    rm -rf server.pid  
    ;;  
  
  restart)  
    $0 stop  
    sleep 1  
    $0 start  
    ;;  
  
  *)  
    echo "Usage: run.sh {start|stop|restart}"  
    ;;  
  
esac 
exit 0