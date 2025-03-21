## Brain Project

![Space Invaders Gameplay](media/cosmic-brain-image.jpg)

The Brain Project is a model to mimic the human brain patterns through the incorporation of AI transformer models that communicate to each other through the use of channels. The project hopes to create a model capable of replicating human emotions.

## Gradle Build Instructions

This project uses Gradle to manage dependencies and build tasks.

### Prerequisites

Before running the game, ensure you have the following installed:
- [Java Development Kit (JDK) 8 or higher](https://www.oracle.com/java/technologies/javase-downloads.html)
- [Gradle](https://gradle.org/install/)
- [Python 3.8 or higher](https://www.python.org/downloads/)

### Build and run the project

Go to `build.gradle` and set the variable `pythonExecutable` to the name of the Python command on your system if it is not **`python3`**.

On Windows, run the following:
```powershell
.\gradlew.bat build
.\gradlew.bat run
```

On Linux/Mac, instead run the following:
```bash
./gradlew build
./gradlew run
```

### Clean the project

To remove any generated files and start fresh, run the following on Windows:
```powershell
.\gradlew.bat clean
```

To clean the project on Linux/Mac, run the following:
```powershell
./gradlew clean
```