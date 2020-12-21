## ChunkyBot
This is a very simple bot for auto-moderating renders channel on Chunky's discord

### Building
Clone this repository, cd into it and execute:

```
./gradlew build
./gradlew fatJar
```

Your jar file should be now in `./build/libs/`
(If you're building docker image you shouldn't care about jar file location)

### Running
This bot expects the following environment variables:

- `DISCORD_TOKEN`
- `DISCORD_RENDER_CHANNEL_ID`

Usage example:
```
export DISCORD_TOKEN=myTokenFromDiscordDeveloperPortal
export DISCORD_RENDER_CHANNEL_ID=1337
java -Xms60M -jar build/libs/jarfile.jar
```

### Building docker image
Before you start: this section assumed you executed [building](#building) instructions

edit (or create) `./Dockerfile.properties`

configure your docker image maintainer name and email, example:
```
maintainer_name = UNuX
maintainer_email = UNuX@example.com
```
generate the Dockerfile
```
./gradlew generateDockerFile
```
build container image
```
docker build .
```

if everything went ok you should get a message like this:
```
Successfully built <container_image_id>
```

### Running docker image
This section assumes you've managed to successfully build container image in [building docker image](#building-docker-image)

```
docker run --env DISCORD_TOKEN=myTokenFromDiscordDeveloperPortal --env DISCORD_RENDER_CHANNEL_ID=1337 --name ChunkyGroovyBot <container_image_id>
```

container image id was given to you by docker at the end of [building docker image](#building-docker-image)
