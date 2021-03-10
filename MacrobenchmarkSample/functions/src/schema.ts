/**
 * The Macrobenchmark Context.
 */
export type BenchmarkContext = {
  "build": {
    'brand': string,
    'device': string,
    'fingerprint': string,
    'model': string,
    'version': {
      "sdk": number
    }
  },
  "cpuCoreCount": number,
  "cpuLocked": boolean,
  "cpuMaxFreqHz": number,
  "memTotalBytes": number,
  "sustainedPerformanceModeEnabled": boolean
};

/**
 * The Metrics Payload.
 */
export type Metrics = {
  'minimum': number;
  'maximum': number;
  'median': number;
  'runs': Array<number>;
};

/**
 * The Macrobenchmark Result.
 */
export type Benchmark = {
  'context': BenchmarkContext
  'name': string;
  'params': object;
  'className': string;
  'totalRunTimeNs': number;
  'metrics': {
    // Startup Timing Metrics
    'startupMs'?: Metrics;
    // Frame Timing Metrics
    'frameTime50thPercentileMs'?: Metrics;
    'frameTime90thPercentileMs'?: Metrics;
    'frameTime95thPercentileMs'?: Metrics;
    'frameTime99thPercentileMs'?: Metrics;
    'totalFrameCount'?: Metrics;
  }
  'warmupIterations': number;
  'repeatIterations': number;
  'thermalThrottleSleepSeconds': number;
};
