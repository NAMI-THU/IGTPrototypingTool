# TrackingDataAnalysisTool

A tool to perform data analysis on tools exposed over an OpenIGTLink interface.

# Building

To build, you can import this project as a Gradle project into your IDE, or you
can run in a terminal:
```bash
# For windows
./gradle.bat build # builds the project
./gradle.bat run # runs the main program

# For linux
./gradlew build
./gradlew run
```

Note:  This requires that openjfx comes bundled with your installation of the
jvm.  This is because one of the dependencies (`smack`, a dependency for
`java-bowler`) is currently broken for java >= 8.
