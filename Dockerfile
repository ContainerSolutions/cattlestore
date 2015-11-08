FROM scratch

EXPOSE 8080

COPY cattlestore /

ENTRYPOINT ["/cattlestore"]
