# IGTPrototypingTool ![IGTPT Icon](src/main/resources/icon/icon_small.png "IGTPT Icon")

![Build Status](https://github.com/NAMI-THU/IGTPrototypingTool/actions/workflows/gradle-build.yml/badge.svg?branch=master)
[![Javadoc](https://img.shields.io/badge/JavaDoc-Online-green)](https://nami-thu.github.io/IGTPrototypingTool/)

A tool that provides basic functionalities for IGT.
Tracker can be connected using OpenIGTLink, and the reported information can be used for conveniently implementing some functionality.

This tool started as a student project and is currently actively maintained by various students of [Ulm University of Applied Sciences (THU)](https://www.thu.de).

Currently, at least **Java 17** or **Java 21** are supported.

The software was presented at the German Conference for Medical Image Computing 2026 (Software Demonstration S03). You can find the conference paper here:

Tababi, M., Baumgärtner, T., Morales, C., Komposch, J., Roßkopf, J., Malzacher, T., Braun, M., Schmitz, B. and Franz, A.M. (2026). Software Prototyping for IGT Research in Java - Examples on Automatic Annotation and AI-based Instrument Tracking for Stroke Treatment. In: Handels, H., et al. Bildverarbeitung für die Medizin 2026. BVM 2026. Informatik aktuell. Springer Vieweg, Wiesbaden. [https://doi.org/10.1007/978-3-658-51100-5_47](https://doi.org/10.1007/978-3-658-51100-5_47)

# Building

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
Run the checkstyle gradle task to check your code:
```bash
# Windows
./gradle.bat checkstyleMain

# Linux
./gradlew checkstyleMain
```

_*To ensure compatibility with older Java versions, please set the language level of this project to the lowest supported language version, currently Java 11.*_

## Getting Started
With the basic version of the IGTPrototypingTool, you can connect to tracking systems and video sources and display 3D objects in relation to them. Based on this, you can implement your own projects. To get started, see also the example view.

To connect a tracker, you can either play back recorded CSV data (see repository under src\test\resources) or connect to a real tracker using OpenIGTLink. [MITK-IGT](https://github.com/IMSY-DKFZ/MITK-IGT) or [PLUS](https://plustoolkit.github.io/) are suitable OpenIGTLink sources. The current version has been tested with [MITK-IGT-2025.02](https://github.com/IMSY-DKFZ/MITK-IGT/releases/tag/mitk-igt-2025.02), for which Windows binaries are available. To connect, start the MITK Workbench, connect to a tracker using the “TrackingToolbox” view, and activate OpenIGTLink using the ‘OpenIGTLinkManager’ view (click “go online”). You can now connect to an instance of the IGTPrototypingTool. Once tracking is running, you can load STL files in the “Visualization” tab, which will then be displayed in relation to the tracking data.

The connection to a video source (e.g., ultrasound device via frame grabber) is established via OpenCV. Alternatively, ultrasound data can also be transmitted via [PLUS](https://plustoolkit.github.io/) using OpenIGTLink. For a function test, you can use a webcam and connect to it with the IGTPrototypingTool (tab “Video Input”). If there are problems with multiple video sources, a workaround is to disable all unnecessary ones in the device manager of the operating system. The connection to PLUS was tested, for example, with a Telemed ultrasound system running PlusApp version 2.9.0.

## Documentation
JavaDoc is automatically compiled and published to [GitHub Pages](https://nami-thu.github.io/IGTPrototypingTool/). 
Please make sure to document all public methods and classes to help lowering the barrier for new students.

## License
This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

The icon is taken from MITK, which is licensed under the BSD 3-Clause License. Check the [respective attribution](src/main/resources/icon/attribution.txt) for more information.
