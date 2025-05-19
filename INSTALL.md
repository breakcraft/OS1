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

=======
# Installation commands

To set up the build environment and run the client:

```bash
# Install Java 8 (Debian/Ubuntu)
sudo apt-get update && sudo apt-get install -y openjdk-8-jdk

# For macOS via Homebrew
# brew install openjdk@8

# Build using the Gradle wrapper
./gradlew build

# Start a server on ports 40000/50000, then launch the client
./gradlew run
```