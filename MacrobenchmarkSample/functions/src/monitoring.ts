import { MetricServiceClient } from "@google-cloud/monitoring";
import { google } from "@google-cloud/monitoring/build/protos/protos";
import { log } from "./logger";
import { Benchmarks, Benchmark, BenchmarkContext, Metrics } from "./schema";

/**
 * Uploads Metrics to Google Cloud Monitoring.
 */
export const uploadMetrics = async (
  client: MetricServiceClient,
  container: Benchmarks,
  timeInSeconds: number = Date.now() / 1000) => {

  const projectId = process.env.GCLOUD_PROJECT || process.env.GOOGLE_CLOUD_PROJECT;
  if (!projectId) {
    throw Error('Unknown project id.');
  }

  const benchmarks = container.benchmarks;
  const timeSeries: google.monitoring.v3.ITimeSeries[] = [];

  if (benchmarks && benchmarks.length > 0) {
    log('Total number of benchmarks ', benchmarks.length);
    // Accumulate time series data
    for (let i = 0; i < benchmarks.length; i += 1) {
      const benchmark = benchmarks[i];
      const metrics = benchmark.metrics;
      type MetricTypes = keyof typeof metrics;
      const knownMetricTypes: MetricTypes[] = [
        'startupMs',
        'frameTime50thPercentileMs',
        'frameTime90thPercentileMs',
        'frameTime95thPercentileMs',
        'frameTime99thPercentileMs',
        'totalFrameCount',
      ];
      for (let j = 0; j < knownMetricTypes.length; j += 1) {
        const metricType = knownMetricTypes[j];
        const metric = metrics[metricType];
        if (metric) {
          timeSeries.push(
            ...createTimeSeries(
              projectId,
              metricType,
              container.context,
              benchmark,
              metric,
              timeInSeconds
            )
          );
        }
      }
    }
  }

  // Upload Time Series
  log(`Total Time Series: ${timeSeries.length}`);
  const requests: google.monitoring.v3.ICreateTimeSeriesRequest[] = timeSeries.map(series => {
    return {
      name: client.projectPath(projectId),
      timeSeries: [
        series
      ]
    };
  });

  const uploads = requests.map(request => {
    return client.createTimeSeries(request)
      .catch(error => {
        log(`Error creating metrics for ${request.name}`, error);
      });
  });
  return Promise.all(uploads);
}

const createTimeSeries = (
  projectId: string,
  metricName: string,
  benchmarkContext: BenchmarkContext,
  benchmark: Benchmark,
  metrics: Metrics,
  timeInSeconds: number): google.monitoring.v3.ITimeSeries[] => {

  type Keys = keyof Metrics;
  // The qualified metrics we care about.
  const qualifiers: Array<Keys> = ['maximum', 'median', 'minimum'];

  return qualifiers.map(qualifier => {
    return {
      metric: {
        type: `custom.googleapis.com/${metricName}/${qualifier}`,
        labels: {
          name: benchmark.name,
          className: benchmark.className,
          brand: benchmarkContext.build.brand,
          device: benchmarkContext.build.device,
          fingerprint: benchmarkContext.build.fingerprint,
          model: benchmarkContext.build.model,
          sdkVersion: `${benchmarkContext.build.version.sdk}`
        }
      },
      resource: {
        type: 'global',
        labels: {
          project_id: projectId,
        }
      },
      metricKind: google.api.MetricDescriptor.MetricKind.GAUGE,
      valueType: google.api.MetricDescriptor.ValueType.DOUBLE,
      points: [
        dataPoint(
          // This is always going to be a number.
          metrics[qualifier] as number,
          timeInSeconds
        )
      ]
    };
  });
}

/**
 * Converts a value to a Cloud Monitoring data point.
 */
const dataPoint = (value: number, timeInSeconds: number) => {
  return {
    interval: {
      endTime: {
        seconds: timeInSeconds,
      },
    },
    value: {
      doubleValue: value,
    }
  };
}
