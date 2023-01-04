LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := ndkMethods
LOCAL_SRC_FILES := ndkMethods.c

include $(BUILD_STATIC_LIBRARY)