package com.example.bgfx.mod;

public class NativeClient {

    public static native void    setEnableMod(String name, boolean b);
    public static native boolean getEnableMod(String name);

    public static native void setModValue(String name, int value);
    public static native int  getModValue(String name);
}
