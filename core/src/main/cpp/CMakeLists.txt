cmake_minimum_required(VERSION 3.18.1)

project("test")

include_directories( ./ )

aux_source_directory(./ SRC_ROOT)
aux_source_directory(utils SRC_UTIL)
aux_source_directory(hook SRC_NATIVE_HOOK)

add_library(
        virtual
        SHARED
        ${SRC_NATIVE_HOOK}
        ${SRC_UTIL}
        ${SRC_ROOT}
        VirtualCore.cpp
)

find_library(
        log-lib
        log)

target_link_libraries(
        virtual
        ${log-lib})