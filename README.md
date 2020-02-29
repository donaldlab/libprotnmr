
# LibProtNMR

*Library for Protein NMR*


## Building

### Prerequisites

LibProtNMR is only supported on Debian-based Linux operating systems,
like Ubuntu.

You'll need CGAL (the development version) installed on your system:
```shell
sudo apt install cgal-dev
```

## Compiling

First, compile the native code for your platform:
```shell
./gradlew native:assemble
```

Then, compile the java code for your platform:
```shell
./gradlew jar
```

The jar file should appear at `build/libs/libprotnmr.jar`

Then you can include the jar file in your java project as usual.
