FROM openjdk:21
WORKDIR /usr/app
COPY ./build/libs/* ./app.jar
EXPOSE 8080
CMD ["java" , "-jar", "app.jar"]
