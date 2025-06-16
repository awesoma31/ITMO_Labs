cmake_minimum_required(VERSION 3.29)
project(sem5 C)

set(CMAKE_C_STANDARD 11)

add_executable(sem5 task1/main.c
        task2/main.c
        task2/main.h
        task3/main.c
        task4/main.c)
