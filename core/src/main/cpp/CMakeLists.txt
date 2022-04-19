cmake_minimum_required(VERSION 3.18.1)

project("test")

add_library(
        virtual
        SHARED
        VmCore.cpp)

find_library(
        log-lib
        log)

target_link_libraries(
        virtual
        ${log-lib})