# Brain Project

![Space Invaders Gameplay](media/cosmic-brain-image.jpg)

## Project Overview

The Brain Project aims to create a model to mimic the human brain patterns through the incorporation of AI transformer models that communicate to each other through the use of channels. The project hopes to create a model capable of replicating human emotions.

The model has multiple neurons, each running as a separate Java thread that invokes a Python client process. Each Python client process communicates with the Java thread through socket programming.

## Gradle Build Instructions

This project uses Gradle to manage dependencies and build tasks.

### Prerequisites

Before running the game, ensure you have the following installed:
- [Java Development Kit (JDK) 14 or higher](https://www.oracle.com/java/technologies/javase-downloads.html)
- [Gradle](https://gradle.org/install/)
- [Python 3.10 or higher](https://www.python.org/downloads/)

To verify you have all the requirements installed, run:
```bash
java --version
javac --version
gradle --version
python3 --version
```

Clone this project's repository in a desired directory using:
```bash
git clone https://github.com/pythagon-code/BrainProject.git
```

Go to the project directory through:
```bash
cd BrainProject
```

### Build and run the project

Go to `application.yml` and set the variable `python_executable` to the name of the Python command on your system if it is not **`python3`**. Also, note that building this project will modify your Python dependencies.\

Also install the essential Python dependencies through the command:
```bash
python3 -m pip install -r requirements.txt
```
\
If you are using Windows, you should use `.\gradlew.bat` instead of `./gradlew` for all the next commands.

The following command will automatically install Python dependencies and build the project:
```bash
./gradlew build
```
You should call the build command every time you change `python_executable` within `application.yml`.

To start the project, run the following:
```bash
./gradlew run
```

### Clean the project

To remove any generated files and start fresh, run the following:
```bash
./gradlew clean
```
