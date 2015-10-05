FROM java:8

ADD run.sh /microservice/
RUN chmod 0755 /microservice/run.sh

ADD target/dependencies/*.jar /microservice/dependencies/
ADD target/*.jar /microservice/
ADD target/classes/config.yml /microservice/

#ENV SERVICE_8080_CHECK_INTERVAL 2s
#ENV SERVICE_8080_CHECK_HTTP /ping
#ENV SERVICE_8080_NAME facade
#ENV SERVICE_8081_NAME facade-mon

ENV SERVICE_8080_IGNORE ignore
ENV SERVICE_8081_IGNORE ignore

EXPOSE 8080 8081

WORKDIR /microservice

ENTRYPOINT ["/microservice/run.sh"]
