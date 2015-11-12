#! /bin/sh

echo Scaling cattlestore to $1 instances 

http -b PUT $(docker-machine ip default):8080/v2/apps/cattlestore instances:=$1

