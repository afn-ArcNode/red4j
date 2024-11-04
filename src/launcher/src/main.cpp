#include "main.h"
#include "utils.h"

#include <csignal>
#include <cstdio>
#include <windows.h>
#include <shlwapi.h>

jint InitVM() {
    JavaVMInitArgs vm_args;
    JavaVMOption options[3];

    sdk->logger->Info(plugin, "Creating JVM arguments");
    vm_args.version = JNI_VERSION_10;
    vm_args.ignoreUnrecognized = false;

    // JDK module path
    auto* modulePathOpt = static_cast<char *>(malloc(1024));
    sprintf(modulePathOpt, "--module-path=%s\\jmods", getenv("JAVA_HOME"));
    sdk->logger->InfoF(plugin, "Module path: %s", modulePathOpt);
    options[0].optionString = modulePathOpt;
    options[1].optionString = const_cast<char*>("--add-modules=ALL-MODULE-PATH");

    // RED4J classpath
    auto* classpathOpt = static_cast<char *>(malloc(1024));
    // Get current module directory
    TCHAR szModuleName[MAX_PATH];
    DWORD dwLen = GetModuleFileNameA(NULL, szModuleName, 1024);
    if (dwLen == 0)
        return -1;  // Failed
    PathRemoveFileSpecA(szModuleName);
    // Build classpath option
    sprintf(classpathOpt, "-Djava.class.path=%s\\jmods;%s\\red4j\\red4j.jar", getenv("JAVA_HOME"), szModuleName);
    sdk->logger->InfoF(plugin, "Classpath: %s", classpathOpt);
    options[2].optionString = classpathOpt;

    vm_args.nOptions = std::size(options);
    vm_args.options = options;

    sdk->logger->Info(plugin, "Creating JVM");
    return JNI_CreateJavaVM(&vm, (void**) &env, &vm_args);
}

void DestroyJVM() {
    if (vm) {
        vm->DestroyJavaVM();
        vm = nullptr;
        env = nullptr;
    }
}

// Used for get module handle
BOOL WINAPI DllMain(
    HINSTANCE hinstDLL,  // handle to DLL module
    DWORD fdwReason,     // reason for calling function
    LPVOID lpvReserved )  // reserved
{
    if (fdwReason == DLL_PROCESS_ATTACH) {
        module = hinstDLL;
    } else if (fdwReason == DLL_PROCESS_DETACH) {
        module = nullptr;
    }

    return TRUE;
}


RED4EXT_C_EXPORT bool RED4EXT_CALL Main(RED4ext::PluginHandle aHandle, RED4ext::EMainReason aReason,
                                        const RED4ext::Sdk* aSdk) {
    switch (aReason) {
        case RED4ext::EMainReason::Load: {
            sdk = aSdk;
            plugin = aHandle;

            jint result = InitVM();
            if (result == JNI_OK) {
                aSdk->logger->Info(aHandle, "JVM created successfully");
                if (!InitUtils(env)) {
                    aSdk->logger->Critical(aHandle, "Failed to initialize utils");
                }
                sdk->logger->Info(aHandle, "utils initialized, running RED4J setup");

                // Initialize Java part
                jclass cl = env->FindClass("cn/afternode/red4j/RED4J");
                if (cl == nullptr) {
                    aSdk->logger->Error(aHandle, "Failed to find class cn.afternode.red4j.RED4J");
                    if (env->ExceptionCheck()) {
                        LogJVMException(env);
                    }
                    return false;
                }
                jmethodID initMethod = env->GetStaticMethodID(cl, "initialize", "()V");
                if (initMethod == nullptr) {
                    aSdk->logger->Error(aHandle, "Failed to find initialize method");
                    if (env->ExceptionCheck()) {
                        LogJVMException(env);
                    }
                    return false;
                }
                env->CallStaticVoidMethod(cl, initMethod);
                aSdk->logger->Info(plugin, "Initialization completed");
            } else {
                aSdk->logger->ErrorF(aHandle, "Failed to initialize JVM: %ld", result);
                return false;
            }
            break;
        }
        case RED4ext::EMainReason::Unload: {
            sdk->logger->Info(aHandle, "Destroying JVM");
            DestroyJVM();

            sdk = nullptr;
            plugin = nullptr;
            break;
        }
    }
    return true;
}

RED4EXT_C_EXPORT void RED4EXT_CALL Query(RED4ext::PluginInfo* aInfo)
{
    /*
     * This function supply the necessary information about your plugin, like name, version, support runtime and SDK. DO
     * NOT do anything here yet!
     *
     * You MUST have this function!
     *
     * Make sure to fill all of the fields here in order to load your plugin correctly.
     *
     * Runtime version is the game's version, it is best to let it set to "RED4EXT_RUNTIME_LATEST" if you want to target
     * the latest game's version that the SDK defined, if the runtime version specified here and the game's version do
     * not match, your plugin will not be loaded. If you want to use RED4ext only as a loader and you do not care about
     * game's version use "RED4EXT_RUNTIME_INDEPENDENT".
     *
     * For more information about this function see https://docs.red4ext.com/mod-developers/creating-a-plugin#query.
     */

    aInfo->name = L"RED4J";
    aInfo->author = L"Zyklone";
    aInfo->version = RED4EXT_SEMVER(1, 0, 0);
    aInfo->runtime = RED4EXT_RUNTIME_LATEST;
    aInfo->sdk = RED4EXT_SDK_LATEST;
}

RED4EXT_C_EXPORT uint32_t RED4EXT_CALL Supports()
{
    /*
     * This functions returns only what API version is support by your plugins.
     * You MUST have this function!
     *
     * For more information about this function see https://docs.red4ext.com/mod-developers/creating-a-plugin#supports.
     */
    return RED4EXT_API_VERSION_LATEST;
}
