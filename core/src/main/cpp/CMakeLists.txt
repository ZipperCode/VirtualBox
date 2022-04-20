cmake_minimum_required(VERSION 3.18.1)

project("test")

include_directories( ./ )

aux_source_directory(./ SRC_ROOT)
aux_source_directory(utils SRC_UTIL)

add_library(
        virtual
        SHARED
        VmCore.cpp
        JniHook.cpp
        ${SRC_ROOT}
        ${SRC_UTIL}
)

find_library(
        log-lib
        log)

target_link_libraries(
        virtual
        ${log-lib})