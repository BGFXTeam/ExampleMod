- a sample build

just copy these files into `/storage/emulated/0/BGFX/plugins/`
```
ExampleMod.dex, ExampleMod.json, libs
```

You gotta be loading the plugin by calling this code

```lua
BGFX.loadPlugin("ExampleMod")
```