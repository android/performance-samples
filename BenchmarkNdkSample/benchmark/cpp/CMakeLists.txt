cmake_minimum_required(VERSION 3.4.1)

add_subdirectory("third_party")

# build ndkbenchmark
add_library(ndkbenchmark
        SHARED
        benchmark/SampleBenchmarks.cpp
        googlebenchmarkrunner/googlebenchmarkrunner.cpp)

# define include dirs
target_include_directories(
        ndkbenchmark
        PRIVATE
        third_party/scopedlocalref/include
        third_party/googlebenchmark/include
)

# link all libs
target_link_libraries(
        ndkbenchmark
        android
        googlebenchmark
        log)
