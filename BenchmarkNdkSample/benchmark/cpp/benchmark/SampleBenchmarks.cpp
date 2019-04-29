/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#pragma clang diagnostic push
// Disable warning of benchmark methods potentially throwing uncaught exceptions
#pragma ide diagnostic ignored "cert-err58-cpp"

#include <benchmark/benchmark.h>
#include <android/log.h>

#define LOGI(...) \
  ((void)__android_log_print(ANDROID_LOG_INFO, "SampleBenchmarks", __VA_ARGS__))

static void BM_Atomic(benchmark::State &state) {
    std::atomic_int myAtomic;
    while (state.KeepRunning()) {
        myAtomic.fetch_add(1);
    }
    ::benchmark::DoNotOptimize(myAtomic);
} BENCHMARK(BM_Atomic);

static void BM_Empty(benchmark::State &state) {
    while (state.KeepRunning()) {
    }
} BENCHMARK(BM_Empty);

static void BM_Skip(benchmark::State &state) {
    while (state.KeepRunning()) {
        // Try uncommenting the below line to print an error:
        //state.SkipWithError("This is an error!");
    }
} BENCHMARK(BM_Skip);

static void BM_memcpy(benchmark::State& state) {
    char* src = new char[state.range(0)];
    char* dst = new char[state.range(0)];
    memset(src, 'x', (size_t) state.range(0));
    while (state.KeepRunning()) {
        memcpy(dst, src, (size_t) state.range(0));

        // DoNotOptimize is needed to prevent the compiler from optimizing away the memcpy()
        // Comment out this line to see the benchmark result (incorrectly) go to near 0!
        ::benchmark::DoNotOptimize(dst);
    }
    state.SetBytesProcessed(
            int64_t(state.iterations()) * int64_t(state.range(0)));
    delete[] src;
    delete[] dst;
}
BENCHMARK(BM_memcpy)->Arg(64)->Arg(256)->Arg(1024);

#pragma clang diagnostic pop
