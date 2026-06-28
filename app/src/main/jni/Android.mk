LOCAL_PATH := $(call my-dir)

# ======================
# Keystone static lib
# ======================
include $(CLEAR_VARS)

LOCAL_MODULE := Keystone
LOCAL_SRC_FILES := Deps/KittyMemory/Deps/Keystone/libs-android/$(TARGET_ARCH_ABI)/libkeystone.a

include $(PREBUILT_STATIC_LIBRARY)


# ======================
# Dobby static lib
# ======================
include $(CLEAR_VARS)

LOCAL_MODULE := Dobby
LOCAL_SRC_FILES := Deps/Dobby/$(TARGET_ARCH_ABI)/libdobby.a

include $(PREBUILT_STATIC_LIBRARY)


# ======================
# MyMod
# ======================
include $(CLEAR_VARS)

LOCAL_MODULE := MyMod


LOCAL_SRC_FILES := \
    Main.cpp \
    Deps/KittyMemory/KittyArm64.cpp \
    Deps/KittyMemory/KittyScanner.cpp \
    Deps/KittyMemory/KittyMemory.cpp \
    Deps/KittyMemory/KittyUtils.cpp \
    Deps/KittyMemory/MemoryPatch.cpp \
    Deps/KittyMemory/MemoryBackup.cpp \
    Deps/And64InlineHook/And64InlineHook.cpp \
    Deps/TestLogger/Logger.cpp \


LOCAL_C_INCLUDES := \
    $(LOCAL_PATH) \
    $(LOCAL_PATH)/Deps \
    $(LOCAL_PATH)/Deps/Dobby


LOCAL_CPPFLAGS := \
    -std=c++14 \
    -w \
    -Wno-error=format-security \
    -fvisibility=hidden \
    -fpermissive \
    -fexceptions


LOCAL_CPP_FEATURES := \
    exceptions \
    rtti


LOCAL_LDFLAGS := \
    -Wl,--gc-sections \
    -Wl,--strip-all


LOCAL_STATIC_LIBRARIES := \
    Keystone \
    Dobby


LOCAL_LDLIBS := \
    -llog \
    -landroid \
    -lEGL \
    -lGLESv2


include $(BUILD_SHARED_LIBRARY)
