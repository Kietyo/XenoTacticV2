Attributions
- MemDixy/cyanochlorite - Idea for rock counters
- TheCurse - Early development support/advice
- Lawlzer - Early development support/advice

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