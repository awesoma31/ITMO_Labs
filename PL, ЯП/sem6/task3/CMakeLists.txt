cmake_minimum_required(VERSION 3.29)
project(task3 C)

set(CMAKE_C_STANDARD 11)

include_directories(..)

add_executable(task3
        main.c
        vector.c
        vector.h)
