FROM scratch

EXPOSE 8080

COPY dist/cattlestore /

ENTRYPOINT ["/cattlestore"]
