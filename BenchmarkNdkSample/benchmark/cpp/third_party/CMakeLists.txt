cmake_minimum_required(VERSION 3.4.1)

## BUILD LIBRARY
# For simplicity, we're not using project's CMAKE, and instead referencing
# https://android.googlesource.com/platform/external/google-benchmark/+/refs/heads/master/Android.bp
# TODO: use the project's CMakeLists.txt directly with add_subdirectory()

add_definitions(
        -DBENCHMARK_ANDROID
        -DHAVE_POSIX_REGEX
        -Werror
        -Wno-deprecated-declarations)

# workaround https://github.com/google/benchmark/issues/731
# TODO: send a PR to fix this
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -D'getloadavg(...)'=0")

set(GOOGLEBENCHMARK_ROOT ${CMAKE_CURRENT_SOURCE_DIR}/googlebenchmark)
add_library(googlebenchmark STATIC
        ${GOOGLEBENCHMARK_ROOT}/src/benchmark_api_internal.cc
        ${GOOGLEBENCHMARK_ROOT}/src/benchmark.cc
        ${GOOGLEBENCHMARK_ROOT}/src/benchmark_name.cc
        ${GOOGLEBENCHMARK_ROOT}/src/benchmark_register.cc
        ${GOOGLEBENCHMARK_ROOT}/src/benchmark_runner.cc
        ${GOOGLEBENCHMARK_ROOT}/src/colorprint.cc
        ${GOOGLEBENCHMARK_ROOT}/src/commandlineflags.cc
        ${GOOGLEBENCHMARK_ROOT}/src/complexity.cc
        ${GOOGLEBENCHMARK_ROOT}/src/console_reporter.cc
        ${GOOGLEBENCHMARK_ROOT}/src/counter.cc
        ${GOOGLEBENCHMARK_ROOT}/src/csv_reporter.cc
        ${GOOGLEBENCHMARK_ROOT}/src/json_reporter.cc
        ${GOOGLEBENCHMARK_ROOT}/src/reporter.cc
        ${GOOGLEBENCHMARK_ROOT}/src/sleep.cc
        ${GOOGLEBENCHMARK_ROOT}/src/statistics.cc
        ${GOOGLEBENCHMARK_ROOT}/src/string_util.cc
        ${GOOGLEBENCHMARK_ROOT}/src/sysinfo.cc
        ${GOOGLEBENCHMARK_ROOT}/src/timers.cc)
target_include_directories(googlebenchmark PRIVATE ${GOOGLEBENCHMARK_ROOT})
target_include_directories(googlebenchmark PUBLIC ${GOOGLEBENCHMARK_ROOT}/include)
