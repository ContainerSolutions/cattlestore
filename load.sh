#! /bin/sh

echo Loading cattlestore into Marathon

http POST $(docker-machine ip default):8080/v2/apps @cattlestore.json

