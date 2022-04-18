# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

add_library( # Sets the name of the library.
             free-reflection
             SHARED
             main.cpp art.cpp )


find_library( # Sets the name of the path variable.
              log-lib

              log )


target_link_libraries( # Specifies the target library.
                       free-reflection

                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} )