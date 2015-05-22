#!/bin/bash

PROG="de.iisys.ocr.App"
PID=`ps x | grep -v grep | grep -v SCREEN | grep $PROG | head -n 1 | awk '{split($0, a, " "); print a[1]}'`

if [ -z "$PID" ]; then
   echo "app is not running."
else
    echo "pid: $PID"
    ps -p $PID -o etime=
fi
