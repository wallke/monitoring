 #!/bin/sh
    SERVICE=$1
    LOGLEVEL=$2
    start(){
        echo "starting..."
        nohup java -jar xwtec-$SERVICE-1.0.war --spring.profiles.active=$SERVICE --spring.config.location=application-$SERVICE.yml --log.level=$LOGLEVEL  >/dev/null  2>&1  &
        if [ $? -ne 0 ]
        then
            echo "start failed, please check the log!"
            exit $?
        else
            echo $! > $SERVICE.pid 
            echo "start success"
        fi
    }
    stop(){
        echo "stopping..."
        kill -9 `cat $SERVICE.pid`
        if [ $? -ne 0 ]
        then
            echo "stop failed, may be $SERVICE isn't running"
            exit $?
        else
            rm -rf $SERVICE.pid 
            echo "stop success"
        fi
    }
    restart(){
        stop&&start
    }
    status(){
        num=`ps -ef | grep $SERVICE | grep -v grep | wc -l`
        if [ $num -eq 0 ]
        then
            echo "$SERVICE isn't running"
        else
            echo "$SERVICE is running"
        fi
    }
    case $3 in    
        start)      start ;;  
        stop)      stop ;;  
        restart)  restart ;;
        status)  status ;; 
        *)          echo "Usage:service.sh $1 $2 {start|stop|restart|status}" ;;     
    esac  
  
    exit 0