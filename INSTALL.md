# Build setup

The following commands will install Java 8 and build the client using the Gradle wrapper:

```bash
sudo apt-get update
sudo apt-get install -y openjdk-8-jdk
./gradlew build
./gradlew run
```

Running `./gradlew build` compiles all sources and packages the application.
`./gradlew run` launches `jagex3.client.Client` directly.

