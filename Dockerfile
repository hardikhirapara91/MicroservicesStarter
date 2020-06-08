FROM repository.broadleafcommerce.com:5001/broadleaf/boot-service-jdk11
ARG JAR_FILE
ADD ${JAR_FILE} app.jar

RUN apk add --no-cache --virtual .build-deps imagemagick

# https
EXPOSE 8447
# debug
EXPOSE 8004
