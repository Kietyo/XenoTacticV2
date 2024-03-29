## What is this?

This is an attempt to create Entropy TD (a Starcraft 2 arcade map) as a standalone game. This is implemented using
[Korge](https://korge.org/), which is a game engine/framework written in Kotlin.

## Setup & Run

Make sure in your gradle setting that you select the correct JVM version.
Use Java 17+.

![Settings Window](screenshots/gradle_jvm.png)

To Run (only tested in IntelliJ):

- Open Gradle menu on the right side
- Run Tasks > run > runJvm
  ![Run](screenshots/gradle_run.png)

## Building a browser distribution

```shell
./gradlew jsBrowserDistribution # Outputs to /build/distributions
```

## Attributions

- MemDixy/cyanochlorite - Idea for rock counters
- TheCurse - Early development support/advice
- Lawlzer - Early development support/advice

## Notes

Trying to get Native working for curl http client engine

- tried installing curl via https://curl.se/windows/
  was able to install it in to the path, but got error when running afterwards:

```shell
> Task :linkDebugTestNative
e: C:\Users\kietm\.konan\dependencies\llvm-11.1.0-windows-x64-essentials/bin/clang++ invocation reported errors

The C:\Users\kietm\.konan\dependencies\llvm-11.1.0-windows-x64-essentials/bin/clang++ command returned non-zero exit code: 1.
output:
lld: error: unable to find library -lcurl
clang++: error: linker command failed with exit code 1 (use -v to see invocation)

> Task :linkDebugTestNative FAILED

Execution failed for task ':linkDebugTestNative'.
> Compilation finished with errors

* Try:
Run with --stacktrace option to get the stack trace. Run with --info or --debug option to get more log output. Run with --scan to get full insights.
```

Turns out, maybe I need to install libcurl?

- Went to https://github.com/microsoft/vcpkg
- Tried to install libcurl but was told need to install visual studio
-

```shell
PS C:\Users\kietm\OneDrive\Documents\GitHub\vcpkg> .\vcpkg.exe install cocoyaxi
Computing installation plan...
A suitable version of cmake was not found (required v3.22.2). Downloading portable cmake v3.22.2...
Downloading cmake...
https://github.com/Kitware/CMake/releases/download/v3.22.2/cmake-3.22.2-windows-i386.zip -> C:\Users\kietm\OneDrive\Documents\GitHub\vcpkg\downloads\cmake-3.22.2-windows-i386.zip
Extracting cmake...
The following packages will be built and installed:
cocoyaxi[core]:x86-windows -> 2.0.3#2
* vcpkg-cmake[core]:x64-windows -> 2022-01-19
  Additional packages (*) will be modified to complete this operation.
  Error: in triplet x64-windows: Unable to find a valid Visual Studio instance
  Could not locate a complete Visual Studio instance
```

- Installed visual studio

## Issues

- 22/8/21 - After using rock placement mode in editor, can no longer move map.

## TODO

- Ability to delete multiple selected towers
- Multi tower selection display UI. Show the damage/speed upgrades in the overview.
- Monster spawner system
- Air waves
- Editor
- Random wave creator
- Distantling
- Closeling
- Multi shot tower
- Bounce tower
- Make the game open source
- Show up error when user attempts to place tower when tower supply is max
- Supply depot tower

## Changelog

A week is defined from Monday to Sunday.
Date should start on Monday.

### Week of 2023-05-15