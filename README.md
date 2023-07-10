# IGTPrototypingTool

A tool that provides basic functionalities for IGT.
Tracker can be connected using OpenIGTLink, and the reported information can be used for conveniently implementing some functionality.

This tool started as a student project and is currently actively maintained by various students of [Ulm University of Applied Sciences](https://www.thu.de).

Currently, at least **Java 11** is required, but all LTS versions upwards (Java 15, Java 17) and also Java 20 are supported.

# Building
![Build Status](https://github.com/NAMI-THU/IGTPrototypingTool/actions/workflows/gradle-build.yml/badge.svg?branch=master)

\
For building, you can import this project as a *Gradle project* into your IDE, or you
can run in a terminal:
```bash
# For windows
./gradle.bat build # builds the project
./gradle.bat run # runs the main program

# For linux
./gradlew build
./gradlew run
```

## Coding Guidelines
This project uses Checkstyle to enforce some basic style checks:
* Use spaces for indentation, not tabs
* Don't use Umlaut characters
* End files with a new line
* Don't leave trailing spaces at the end of a line

_*To ensure compatibility with older Java versions, please set the language level of this project to the lowest supported language version, currently Java 11.*_

