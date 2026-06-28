# ExampleMod

ExampleMod is a BGFX native mod template using Java, JNI and C++.

## Features

- Java plugin integration
- Native `.so` loading
- JNI bridge
- ARM64 native code
- Hooking utilities
- Memory utilities
- Signature support

## Structure

```
ExampleMod/

app/src/main/java/
└── com/example/bgfx/mod/
    ├── ExampleMod.java
    ├── NativeClient.java
    └── LibraryLoader.java

app/src/main/jni/
├── Main.cpp
├── Utils.h
├── Sigs.h
└── obfuscate.h

app/src/main/jniLibs/
└── arm64-v8a/
    └── libMyMod.so

plugins/
├── ExampleMod.dex
└── ExampleMod.json
```

## Java Side

`ExampleMod.java` is the plugin entry point.

It loads the native library:

```java
LibraryLoader.load(context, "libMyMod.so");
```

## Native Side

Native code is located in:

```
jni/Main.cpp
```

The output library:

```
libMyMod.so
```

contains native mod code.

## JNI

Java communicates with C++ using:

```java
NativeClient.java
```

## Dependencies

Included libraries:

- Dobby
- And64InlineHook
- KittyMemory
- Logger

## Building

Build:

```bash
./gradlew assembleRelease
```

Native output:

```
libMyMod.so
```

## Plugin Support

Example plugin folder:

```
plugins/

├── ExampleMod.dex
└── ExampleMod.json
```

JSON:

```json
{
    "name": "ExampleMod",
    "main": "com.example.bgfx.client.ExampleClient"
}
```

## Architecture

Target:

```
arm64-v8a
```

## Notes

- Native library must match device architecture.
- JNI names must match between Java and C++.
- Rebuild after changing native code.

## License

Free to modify and create your own BGFX mods.
