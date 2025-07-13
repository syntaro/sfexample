cmake_minimum_required(VERSION 3.4.1)

project(mylib
        VERSION 1.0
        DESCRIPTION "mylib project"
        LANGUAGES CXX
)

add_library(native-lib SHARED src/main/cpp/native-lib.cpp)

# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

target_compile_features(native-lib PUBLIC cxx_std_20)

# Create a variable fluidsynth_DIR to specify where the fluidsynth library is located.
set(fluidsynth_DIR C:/github/fluidsynth-2.4.6-android24)
set(fluidsynth_DIR C:/github/fluidsynth-android24-noopenmp)

#message(“Architecture1 = ${ANDROID_ABI}")
#message(“Architecture2 = ${CMAKE_ANDROID_ARCH_ABI}")

#set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -fopenmp")
#set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -fopenmp")

set(NDK_DIR C:/Users/yaman/AppData/Local/Android/Sdk/ndk/android-ndk-r26d)
set(NDK_SUBDIR ${NDK_DIR}/toolchains/llvm/prebuilt/windows-x86_64/lib/clang/17.0.2/lib/linux)

set(NDK_DIR C:/Users/yaman/AppData/Local/Android/Sdk/ndk/28.2.13676358)
set(NDK_SUBDIR ${NDK_DIR}/toolchains/llvm/prebuilt/windows-x86_64/lib/clang/19/lib/linux)

set(NDK_DIR C:/Users/yaman/AppData/Local/Android/Sdk/ndk/27.2.12479018)
set(NDK_SUBDIR ${NDK_DIR}/toolchains/llvm/prebuilt/windows-x86_64/lib/clang/18/lib/linux)


set(NDK_DIR C:/Users/yaman/AppData/Local/Android/Sdk/ndk/29.0.13599879)
set(NDK_SUBDIR ${NDK_DIR}/toolchains/llvm/prebuilt/windows-x86_64/lib/clang/20/lib/linux)

set(LIBC_SUBDIR ${NDK_DIR}/toolchains/llvm/prebuilt/windows-x86_64/sysroot/usr/lib)


if (${ANDROID_ABI} STREQUAL "arm64-v8a")
    set(lib_omp_DIR ${NDK_SUBDIR}/aarch64)
    set(lib_c_DIR ${LIBC_SUBDIR}/aarch64-linux-android)
elseif (${ANDROID_ABI} STREQUAL "armeabi-v7a")
    set(lib_omp_DIR ${NDK_SUBDIR}/arm)
    set(lib_c_DIR ${LIBC_SUBDIR}/arm-linux-androideabi)
elseif (${ANDROID_ABI} STREQUAL "x86_64")
    set(lib_omp_DIR ${NDK_SUBDIR}/x86_64)
    set(lib_c_DIR ${LIBC_SUBDIR}/x86_64-linux-android)
elseif (${ANDROID_ABI} STREQUAL "x86")
    set(lib_omp_DIR ${NDK_SUBDIR}/i386)
    set(lib_c_DIR ${LIBC_SUBDIR}/i686-linux-android)
endif ()

# Create a variable lib_other_DIR to specify where the other non-fluidsynth libraries is located.
#set(lib_c_DIR C:/github/android-ndk-r27c/toolchains/llvm/prebuilt/windows-x86_64/sysroot/usr/lib/aarch64-linux-android/)
# Fluidsynth library code will be calling some non-fluidsynth functions which are not part of
# default NDK, so we add the binaries as dependencies of our code.

add_library(libc++_shared SHARED IMPORTED)
set_target_properties(libc++_shared PROPERTIES IMPORTED_LOCATION ${lib_c_DIR}/libc++_shared.so)

add_library(libomp SHARED IMPORTED)
set_target_properties(libomp PROPERTIES IMPORTED_LOCATION ${lib_omp_DIR}/libomp.so)

# Our code (native-lib.cpp) will be calling fluidsynth functions, so adding the fluidsynth binaries as dependencies.

add_library(libFLAC SHARED IMPORTED)
set_target_properties(libFLAC PROPERTIES IMPORTED_LOCATION ${fluidsynth_DIR}/lib/${ANDROID_ABI}/libFLAC.so)

add_library(libfluidsynth SHARED IMPORTED)
set_target_properties(libfluidsynth PROPERTIES IMPORTED_LOCATION ${fluidsynth_DIR}/lib/${ANDROID_ABI}/libfluidsynth.so)

add_library(libfluidsynth-assetloader SHARED IMPORTED)
set_target_properties(libfluidsynth-assetloader PROPERTIES IMPORTED_LOCATION ${fluidsynth_DIR}/lib/${ANDROID_ABI}/libfluidsynth-assetloader.so)

add_library(libgio-2.0 SHARED IMPORTED)
set_target_properties(libgio-2.0 PROPERTIES IMPORTED_LOCATION ${fluidsynth_DIR}/lib/${ANDROID_ABI}/libgio-2.0.so)

add_library(libglib-2.0 SHARED IMPORTED)
set_target_properties(libglib-2.0 PROPERTIES IMPORTED_LOCATION ${fluidsynth_DIR}/lib/${ANDROID_ABI}/libglib-2.0.so)

add_library(libgmodule-2.0 SHARED IMPORTED)
set_target_properties(libgmodule-2.0 PROPERTIES IMPORTED_LOCATION ${fluidsynth_DIR}/lib/${ANDROID_ABI}/libgmodule-2.0.so)

add_library(libgobject-2.0 SHARED IMPORTED)
set_target_properties(libgobject-2.0 PROPERTIES IMPORTED_LOCATION ${fluidsynth_DIR}/lib/${ANDROID_ABI}/libgobject-2.0.so)

add_library(libgthread-2.0 SHARED IMPORTED)
set_target_properties(libgthread-2.0 PROPERTIES IMPORTED_LOCATION ${fluidsynth_DIR}/lib/${ANDROID_ABI}/libgthread-2.0.so)

add_library(libinstpatch-1.0 SHARED IMPORTED)
set_target_properties(libinstpatch-1.0 PROPERTIES IMPORTED_LOCATION ${fluidsynth_DIR}/lib/${ANDROID_ABI}/libinstpatch-1.0.so)

add_library(liboboe SHARED IMPORTED)
set_target_properties(liboboe PROPERTIES IMPORTED_LOCATION ${fluidsynth_DIR}/lib/${ANDROID_ABI}/liboboe.so)

add_library(libogg SHARED IMPORTED)
set_target_properties(libogg PROPERTIES IMPORTED_LOCATION ${fluidsynth_DIR}/lib/${ANDROID_ABI}/libogg.so)

add_library(libopus SHARED IMPORTED)
set_target_properties(libopus PROPERTIES IMPORTED_LOCATION ${fluidsynth_DIR}/lib/${ANDROID_ABI}/libopus.so)

add_library(libpcre SHARED IMPORTED)
set_target_properties(libpcre PROPERTIES IMPORTED_LOCATION ${fluidsynth_DIR}/lib/${ANDROID_ABI}/libpcre.so)

add_library(libsndfile SHARED IMPORTED)
set_target_properties(libsndfile PROPERTIES IMPORTED_LOCATION ${fluidsynth_DIR}/lib/${ANDROID_ABI}/libsndfile.so)

add_library(libvorbis SHARED IMPORTED)
set_target_properties(libvorbis PROPERTIES IMPORTED_LOCATION ${fluidsynth_DIR}/lib/${ANDROID_ABI}/libvorbis.so)

add_library(libvorbisenc SHARED IMPORTED)
set_target_properties(libvorbisenc PROPERTIES IMPORTED_LOCATION ${fluidsynth_DIR}/lib/${ANDROID_ABI}/libvorbisenc.so)

add_library(libvorbisfile SHARED IMPORTED)
set_target_properties(libvorbisfile PROPERTIES IMPORTED_LOCATION ${fluidsynth_DIR}/lib/${ANDROID_ABI}/libvorbisfile.so)

add_library(libclang SHARED IMPORTED)
set_target_properties(libclang PROPERTIES IMPORTED_LOCATION C:/github/android-ndk-r27c/toolchains/llvm/prebuilt/windows-x86_64/lib/clang/18/lib/linux/libclang_rt.asan-aarch64-android.so)

add_library(openmp SHARED IMPORTED)

# Specifies the directory where the C or C++ source code will look the #include <yourlibrary.h> header files
target_include_directories(native-lib PRIVATE ${fluidsynth_DIR}/include)

find_library(ANDROID_LIB android)
list(APPEND PAG_SHARED_LIBS ${ANDROID_LIB})

# Link everything all together. Notice that native-lib should be the first element in the list.
target_link_libraries(
        native-lib
        android
        # Non-fluidsynth binaries
        #libc++_shared
        #libomp
        #libclang
        log

        #Gnu Package
        libgmodule-2.0
        libglib-2.0
        libgobject-2.0
        libgthread-2.0
        libgio-2.0
        libinstpatch-1.0

        # fluidsynth binaries
        libFLAC
        libfluidsynth
        libfluidsynth-assetloader
        liboboe
        libogg
        libopus
        libpcre
        libsndfile
        libvorbis
        libvorbisenc
        libvorbisfile
)
