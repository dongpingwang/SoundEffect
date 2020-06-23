LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional
LOCAL_PACKAGE_NAME := SoundEffect
LOCAL_CERTIFICATE := platform
LOCAL_PRIVATE_PLATFORM_APIS := true
LOCAL_USE_AAPT2 := true
# LOCAL_DEX_PREOPT := false


LOCAL_SRC_FILES := $(call all-java-files-under, app/src/main)

LOCAL_RESOURCE_DIR := $(LOCAL_PATH)/app/src/main/res/comm \
                      $(LOCAL_PATH)/app/src/main/res/config \
                      $(LOCAL_PATH)/app/src/main/res/main
LOCAL_MANIFEST_FILE :=app/src/main/AndroidManifest.xml

######################################## flyaudio-dsp ########################################
LOCAL_STATIC_JAVA_LIBRARIES := flyaudio-dsp-client

######################################## flyaudio-base ########################################
LOCAL_STATIC_FLYAUDIO_LIBRARIES := flyaudio-base \
                                   flyaudio-ui \
                                   flyaudio-res \
                                   glide \
                                   rxjava \
                                   leakcanary

LOCAL_STATIC_ANDROID_LIBRARIES := android-support-v4
LOCAL_STATIC_ANDROID_LIBRARIES += android-support-v7-appcompat
LOCAL_STATIC_ANDROID_LIBRARIES += android-support-v7-recyclerview

include $(LOCAL_PATH)/Libraries.mk
include $(BUILD_PACKAGE)