#ifndef MAIN_H
#define MAIN_H

#include <jni.h>
#include <RED4ext/RED4ext.hpp>

static JavaVM* vm;
static JNIEnv* env;

static const RED4ext::Sdk* sdk;
static RED4ext::PluginHandle plugin;
static HMODULE module;

#endif //MAIN_H
