#include "utils.h"
#include "main.h"

static jclass cl_String;
static jmethodID mt_String_valueOf_Object;

bool InitUtils(JNIEnv *env) {
    cl_String = env->FindClass("java/lang/String");
    if (!cl_String) {
        sdk->logger->Critical(plugin, "Cannot find class: java.lang.String");
        return false;
    }
    mt_String_valueOf_Object = env->GetStaticMethodID(cl_String, "valueOf", "(Ljava/lang/Object;)Ljava/lang/String;");
    if (!mt_String_valueOf_Object) {
        sdk->logger->Critical(plugin, "Cannot find method: java.lang.String java.lang.String#valueOf(java.lang.Object)");
        return false;
    }

    return true;
}


void LogJVMException(JNIEnv *env) {
    jobject except = env->ExceptionOccurred();
    auto str = (jstring) env->CallStaticObjectMethod(cl_String, mt_String_valueOf_Object, except);

    jboolean isCopy;
    auto* c = env->GetStringUTFChars(str, &isCopy);
    sdk->logger->ErrorF(plugin, "Java exception occurred: %s", const_cast<char*>(c));
    env->ReleaseStringUTFChars(str, c);

    env->ExceptionClear();
}
