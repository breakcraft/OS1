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
