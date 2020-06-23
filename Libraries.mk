define include-flyaudio-libraries

$(eval LIB_FLYAUDIO_BASE := flyaudio-base)
$(eval LIB_FLYAUDIO_UI := flyaudio-ui)
$(eval LIB_FLYAUDIO_RES := flyaudio-res)
$(eval LIB_GSON := gson)
$(eval LIB_GLIDE := glide)
$(eval LIB_RXJAVA := rxjava)
$(eval LIB_LEAKLANARY := leakcanary)

$(foreach lib, $(LOCAL_STATIC_FLYAUDIO_LIBRARIES), $(                                                                   \
    $(if $(call streq, $(lib), $(LIB_FLYAUDIO_BASE)), $(                                                                \
        $(eval LOCAL_STATIC_JAVA_AAR_LIBRARIES += flyaudio-base)                                                        \
        $(eval LOCAL_RESOURCE_DIR += $(call intermediates-dir-for,JAVA_LIBRARIES,flyaudio-base,,COMMON)/aar/res)        \
        $(eval LOCAL_AAPT_FLAGS += --auto-add-overlay)                                                                  \
        $(eval LOCAL_AAPT_FLAGS += --extra-packages com.flyaudio.lib)                                                   \
    ), $(if $(call streq, $(lib), $(LIB_FLYAUDIO_UI)), $(                                                               \
        $(eval LOCAL_STATIC_JAVA_AAR_LIBRARIES += flyaudio-ui)                                                          \
        $(eval LOCAL_RESOURCE_DIR += $(call intermediates-dir-for,JAVA_LIBRARIES,flyaudio-ui,,COMMON)/aar/res)          \
        $(eval LOCAL_AAPT_FLAGS += --auto-add-overlay)                                                                  \
        $(eval LOCAL_AAPT_FLAGS += --extra-packages com.flyaudio.lib.ui)                                                \
    ), $(if $(call streq, $(lib), $(LIB_FLYAUDIO_RES)), $(                                                              \
        $(eval LOCAL_STATIC_JAVA_AAR_LIBRARIES += flyaudio-res)                                                         \
        $(eval LOCAL_RESOURCE_DIR += $(call intermediates-dir-for,JAVA_LIBRARIES,flyaudio-res,,COMMON)/aar/res)         \
        $(eval LOCAL_AAPT_FLAGS += --auto-add-overlay)                                                                  \
        $(eval LOCAL_AAPT_FLAGS += --extra-packages com.flyaudio.lib.res)                                               \
    ), $(if $(call streq, $(lib), $(LIB_GSON)), $(                                                                      \
        $(eval LOCAL_STATIC_JAVA_LIBRARIES += gson)                                                                     \
    ), $(if $(call streq, $(lib), $(LIB_GLIDE)), $(                                                                     \
        $(eval LOCAL_STATIC_JAVA_LIBRARIES += glide)                                                                    \
    ), $(if $(call streq, $(lib), $(LIB_RXJAVA)), $(                                                                    \
        $(eval LOCAL_STATIC_JAVA_LIBRARIES += rxjava)                                                                   \
        $(eval LOCAL_STATIC_JAVA_LIBRARIES += reactive-streams)                                                         \
        $(eval LOCAL_STATIC_JAVA_AAR_LIBRARIES += rxandroid)                                                            \
    ), $(if $(call streq, $(lib), $(LIB_LEAKLANARY)), $(                                                                \
        $(eval LOCAL_STATIC_JAVA_LIBRARIES += leakcanary-watcher)                                                       \
        $(eval LOCAL_STATIC_JAVA_LIBRARIES += haha)                                                                     \
        $(eval LOCAL_STATIC_JAVA_AAR_LIBRARIES += leakcanary-android)                                                   \
        $(eval LOCAL_STATIC_JAVA_AAR_LIBRARIES += leakcanary-analyzer)                                                  \
        $(eval LOCAL_RESOURCE_DIR += $(call intermediates-dir-for,JAVA_LIBRARIES,leakcanary-android,,COMMON)/aar/res)   \
        $(eval LOCAL_AAPT_FLAGS += --auto-add-overlay)                                                                  \
        $(eval LOCAL_AAPT_FLAGS += --extra-packages com.squareup.leakcanary)                                            \
    ), $(                                                                                                               \
        $(warning Library '$(lib)' is undefined!)                                                                       \
    ))))))))                                                                                                            \
))

endef

ifneq ($(strip $(LOCAL_STATIC_FLYAUDIO_LIBRARIES)),)
    $(eval $(call include-flyaudio-libraries))
endif
