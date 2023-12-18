[![CI](https://github.com/SpoderVapson/puzzle-game-factory/actions/workflows/build.yml/badge.svg?branch=main)](https://github.com/SpoderVapson/puzzle-game-factory/actions/workflows/build.yml)
# puzzle-game-factory
Web application for users to create their own worlde-like puzzle games

### Overview and Setup

#### Basics
The application runs on spring boot with default embedded tomcat server.
MongoDB is the application's main database and data source.
A complimentary Mongo Express instance has been added to aid development on windows. It acts as a Non-relational web-based DBMS.

Ports occupied (Make sure these are free at the time of startup)
- Tomcat: 8080
- MongoDB: 27017
- Mongo Express: 8081

Mongo Express can be accessed on 0.0.0.0:8081
The database part of the application is containerized using docker compose.

Both docker engine and docker compose are recommended for easy application startup, but they are not required. The MongoDB instance can be initiated manually, as long as the credentials and authentication database names match the ones in [application properties file](src/main/resources/application.properties)

Instantiating Mongo Express is optional, if you wish to manage MongoDB via terminal, simply remove (or comment out) the mongo-express container from the [docker compose file](docker-compose.yaml)

Before running the application make sure to adjust application properties in src/main/resources folder
to match your running preferences since. Moreover, when starting application from the terminal remove 
spring-app from the list of services in docker compose file.



#### Linux
1. Install docker engine and docker compose ([engine docs](https://docs.docker.com/engine/install/ubuntu/), [compose docs](https://docs.docker.com/compose/install/linux/))
2. Navigate to the main directory of this project `.../puzzle-game-factory`
3. Start dockers using `docker compose up -d`
4. Build the project with `./gradlew build`
5. Run the app with `./gradlew bootRun`

#### Windows
1. Install Docker Desktop ([desktop docs](https://docs.docker.com/desktop/install/windows-install/))
2. Open the Docker Desktop app as administrator and make sure the docker engine is running 
3. Navigate to the main directory of this project `...\puzzle-game-factory`
4. Start dockers using `docker-compose up -d`
5. Build the project with `./gradlew build`
6. Run the app with `./gradlew bootRun`

All of the above steps except installing docker can be performed via IDE, Intellij IDEA is recommended.

#### After startup
Open your browser and navigate to `http://localhost:8080/ping` to check if the app is responsive.

Navigate to `http://localhost:8081` to view the Mongo Express admin panel