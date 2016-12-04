#!/bin/bash

timeLimit=120

playerPool="PlayerPool.txt"

while true
do

  for item in `ls *.txt`
  do
    now=`date +%s`
    fileModTime=`date -r $item +%s`
    deltaT=`expr $now - $fileModTime`

    if [ "$item" != "$playerPool" ] && [ "$deltaT" -gt "$timeLimit" ] 
    then
      rm -f $item
    fi
  done

  sleep 60
done
