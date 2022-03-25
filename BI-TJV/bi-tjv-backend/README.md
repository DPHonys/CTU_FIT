##Requiremets
- JDK 11+
- Docker

##Docker
First we need to create a volume
```
    docker volume create pokemon_data
```
In project folder we run docker compose 
```
    docker-compose -f docker-compose.yml up
```
Image with database should be running after docker compose
```
    docker start tjv-semestralka_postgres_1
```
so if its not use ^

##Build the project
```
    ./gradlew build
```


##Run the server
```
    java -jar ./build/libs/*.jar
```

#Runs on port 8080 (needs to run on that port for client).