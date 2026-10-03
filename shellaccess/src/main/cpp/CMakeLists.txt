# Copyright 2026 Paras Mohandas Khanchandani Chandani
# All rights reserved.

cmake_minimum_required(VERSION 3.22.1)
project(influential C)

add_executable(influential starter.c)

set_target_properties(influential PROPERTIES
    OUTPUT_NAME "influential"
    PREFIX "lib"
    SUFFIX ".so"
    RUNTIME_OUTPUT_DIRECTORY "${CMAKE_LIBRARY_OUTPUT_DIRECTORY}")

target_link_options(influential PRIVATE "-Wl,-z,max-page-size=16384")
