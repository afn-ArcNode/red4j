#ifndef UTILS_H
#define UTILS_H

#include <jni.h>

bool InitUtils(JNIEnv *env);
void LogJVMException(JNIEnv *env);

#endif //UTILS_H
