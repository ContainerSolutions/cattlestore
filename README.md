# Cattle store

The idea behind this application is that we're going to spin a wheel with a sensor attached to it. The sensor will relay clicks (most basic form of data) to a REST endpoint.

The REST API will be faced with a facade for counting purposes. The facade is transparent and relays the REST calls through to a back end consisting of several "cattlestore." The cattlestore app is doomed because after a specific number of requests it can't take it anymore and exits.

There is a [herder](https://github.com/ContainerSolutions/cattlestore-herder) that finds the running instances in Marathon, gets the number of requests served so far and pushes this information out through a websocket. Marathon and Mesos are part of [Mantl](http://mantl.io).

This app will consist of 5 parts, running on [Mantl.io](http://Mantl.io/).

The parts are
- A facade, currently a [Traefik](https://traefik.github.io/) container;
- The cattle store app in this repo, this is the thing that will be scaled up and down, currently a golang app;
- The "herder," which will monitor the cattle store apps;
- A front end application that gets its information from Marathon;
- minimesos, using the `traefik` branch;
- A hardware tick counter, that counts the revolutions of a wheel or the clicks on a wheel of fortune, and relays each click to the `/tick` REST endpoint on the server.

![](http://www.remmelt.com/media/cattlestorev1.jpg)

##Current state
The [/infra subdir](https://github.com/ContainerSolutions/cattlestore-herder/tree/master/infra) in the herder repo has a number of scripts that aid in setting up the parts.

Start by checking out the `traefik` branch of [minimesos](https://github.com/ContainerSolutions/minimesos) and building it.
```
git clone, etc
git checkout, etc
./gradlew build -x test
bin/minimesos
```

Now get the `infra` scripts and start Traefik, the herder and a number of cattle store instances
```
docker-compose up -d
./load.sh
```

This should do it.

## Troubleshooting
OSX's Homebrew's go does not come with cross compilation support by default. (Re)install go using:
```
brew reinstall go --with-cc-common
```
This will enable cross compilation.

