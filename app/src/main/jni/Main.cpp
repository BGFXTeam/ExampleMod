#include <jni.h>
#include <pthread.h>
#include <unistd.h>
#include <cstring>

#include "dobby.h"
#include "Utils.h"
#include "Sigs.h"
#include "TestLogger/Logger.h"

JavaVM* g_vm = nullptr;
const char* targetLibName = "libBlockMan.so";

void* localPlayer = nullptr;

Offsets offsets;

bool nuker = false;
int nukerRange = 1;
bool fastBreak = false;

struct vec3_ti {
    int x, y, z;
};

void (*old_PlayerController__onPlayerDamageBlock)(void*, const vec3_ti&, int);
void PlayerController__onPlayerDamageBlock(void* playerController, const vec3_ti& pos, int face)
{
    //LOGD("onPlayerDamageBlock called");

    if (playerController != nullptr && nuker)
    {
        //LOGI("Nuker active at %d %d %d", pos.x, pos.y, pos.z);

        for (int x = pos.x - nukerRange; x <= pos.x + nukerRange; x++)
        {
            for (int y = pos.y - nukerRange; y <= pos.y + nukerRange; y++)
            {
                for (int z = pos.z - nukerRange; z <= pos.z + nukerRange; z++)
                {
                    vec3_ti current_pos = {x, y, z};
                    old_PlayerController__onPlayerDamageBlock(playerController, current_pos, face);
                }
            }
        }
    }

    old_PlayerController__onPlayerDamageBlock(playerController, pos, face);
}

float (*old_Block_getPlayerRelativeBlockHardness)(void*, void*, void*, vec3_ti&);
float Block_getPlayerRelativeBlockHardness(void* block, void* player, void* world, vec3_ti& pos)
{
    //LOGD("getPlayerRelativeBlockHardness called");

    if (block != nullptr)
    {
        localPlayer = player;

        if (fastBreak)
        {
            //LOGI("FastBreak enabled");
            return 100000;
        }
    }

    return old_Block_getPlayerRelativeBlockHardness(block, player, world, pos);
}

void (*old_World__playAuxSFXAtEntity)(void*, void*,int,const vec3_ti&, int);
void World__playAuxSFXAtEntity(void* world, void* player, int type, const vec3_ti& pos, int data)
{
    if(nuker) return;
    old_World__playAuxSFXAtEntity(world,player,type,pos,data);
}

void* hack_thread(void*)
{
    LOGI("Hack thread started");

    while (!isLibraryLoaded(targetLibName))
    {
        LOGD("Waiting for %s", targetLibName);
        sleep(1);
    }

    LOGI("%s loaded", targetLibName);

    void* damageAddr = (void*)getAbsoluteAddress(
        targetLibName,
        offsets.onPlayerDamageBlock
    );

    void* hardnessAddr = (void*)getAbsoluteAddress(
        targetLibName,
        offsets.getPlayerRelativeBlockHardness
    );
    
    void* effectAddr = (void*)getAbsoluteAddress(
        targetLibName,
        offsets.playAuxSFXAtEntity
    );

    if (damageAddr)
    {
        DobbyHook(
            damageAddr,
            (void*)PlayerController__onPlayerDamageBlock,
            (void**)&old_PlayerController__onPlayerDamageBlock
        );

        LOGI("DamageBlock hook installed");
    }
    else
    {
        LOGE("DamageBlock address invalid");
    }

    if (hardnessAddr)
    {
        DobbyHook(
            hardnessAddr,
            (void*)Block_getPlayerRelativeBlockHardness,
            (void**)&old_Block_getPlayerRelativeBlockHardness
        );

        LOGI("FastBreak hook installed");
    }
    else
    {
        LOGE("Hardness address invalid");
    }
    
    if(effectAddr) {
        DobbyHook(
            effectAddr,
            (void*)World__playAuxSFXAtEntity,
            (void**)&old_World__playAuxSFXAtEntity
        );
        LOGI("No Nuker Lag hook installed");
    } else {
        LOGE("Effect address invaild");
    }

    LOGI("Hooks loaded successfully");

    return nullptr;
}

__attribute__((constructor))
void lib_main()
{
    LOGI("Library constructor called");

    pthread_t ptid;

    pthread_create(
        &ptid,
        nullptr,
        hack_thread,
        nullptr
    );

    pthread_detach(ptid);

    LOGI("Hack thread created");
}

extern "C"
{

JNIEXPORT jint JNICALL
JNI_OnLoad(JavaVM* vm, void*)
{
    g_vm = vm;

    LOGI("JNI_OnLoad called");

    return JNI_VERSION_1_6;
}

JNIEXPORT void JNICALL
Java_com_example_bgfx_mod_NativeClient_setEnableMod(
        JNIEnv* env,
        jclass clazz,
        jstring name,
        jboolean enabled)
{
    const char* mod = env->GetStringUTFChars(
        name,
        nullptr
    );
    
    if(strcmp(mod, "Nuker") == 0){
        nuker = enabled;
    }
    if(strcmp(mod, "Fast Break") == 0){
        fastBreak = enabled;
    }

    LOGI(
        "Mod %s state: %s",
        mod,
        enabled ? "ON" : "OFF"
    );

    env->ReleaseStringUTFChars(
        name,
        mod
    );
}

JNIEXPORT jboolean JNICALL
Java_com_example_bgfx_mod_NativeClient_getEnableMod(
        JNIEnv* env,
        jclass clazz,
        jstring name)
{
    const char* mod = env->GetStringUTFChars(
        name,
        nullptr
    );

    LOGD("Checking mod: %s", mod);

    env->ReleaseStringUTFChars(
        name,
        mod
    );

    return JNI_FALSE;
}

JNIEXPORT void JNICALL
Java_com_example_bgfx_mod_NativeClient_setModValue(
        JNIEnv* env, jclass clazz, jstring name, jint value)
{
    const char* mod = env->GetStringUTFChars(name, nullptr);
    LOGI("Mod %s value: %d", mod, value);
    
    if(strcmp(mod, "Nuker Range") == 0) {
        nukerRange = value;
    }
    
    env->ReleaseStringUTFChars(name, mod);
}

JNIEXPORT jint JNICALL
Java_com_example_bgfx_mod_NativeClient_getModValue(
        JNIEnv* env, jclass clazz, jstring name)
{
    const char* mod = env->GetStringUTFChars(name, nullptr);
    LOGD("Checking mod value: %s", mod);
    env->ReleaseStringUTFChars(name, mod);
    return 0;
}

}
