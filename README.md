# Cattle store

The idea behind this application is that we're going to spin a wheel with a sensor attached to it. The sensor will relay clicks (most basic form of data) to a REST endpoint.

The REST API will be faced with a facade for counting purposes. The facade is transparent and relays the REST calls through to a back end consisting of several "cattlestore." The cattlestore app is deliberately slow because it sleeps for a couple of seconds before returning each REST call. When asleep, it reports its health as `critical` to Consul.

There needs to be a herder or orchestrator that watches Consul, counts the number of healthy cattlestore apps, and spins up more when there are too few of them. This will be done through Marathon, which is part of Mantl.

This app will consist of 5 parts, running on [Mantl.io](http://Mantl.io/).

The parts are
- A facade (see the ms-facade subdir), currently a Dropwizard Java app,
- The cattle store app (cattlestore subdir), this is the thing that will be scaled up and down, currently a golang app,
- The "herder," which will do the scaling,
- A front end application that gets its information from Consul (part of Mantl) and from the facade (through the metrics library),
- A hardware tick counter, that counts the revolutions of a wheel or the clicks on a wheel of fortune, and relays each click to the `/tick` REST endpoint on the server.

![](http://www.remmelt.com/media/cattlestorev1.jpg)

##Current state

Currently the app runs on Consul using Registrator (to be replaced), and the hardware, front end and herder are not yet implemented.

Both provided apps should be buildable with their `build.sh` scripts, and will build in containers.

```
docker run -d --name=consul --net=host -p 8500:8500 -p 8600:8600 gliderlabs/consul-server -bootstrap
docker run -d --name=registrator --net=host --volume=/var/run/docker.sock:/tmp/docker.sock gliderlabs/registrator:latest consul://localhost:8500
docker run --rm -p 8080:8080 -p 8081:8081 --name facade facade

#Run several of these:
docker run -dP cattlestore
```

This should do it.
