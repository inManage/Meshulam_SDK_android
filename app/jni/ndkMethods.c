#include <jni.h>

 jstring Java_com_example_utils_server_requests_InitSdkServerRequest_applicationToken(JNIEnv* env, jobject obj){

     return (*env)->NewStringUTF(env, "inmanga_secure");
 }

 jstring Java_com_example_utils_utils_BaseEncryption_encryptionKey(JNIEnv* env, jobject obj){

     return (*env)->NewStringUTF(env, "RT234T%EFFdr34mbn38J&jf95hj");

 }
