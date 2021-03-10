import { MetricServiceClient } from "@google-cloud/monitoring";
import { google } from "@google-cloud/monitoring/build/protos/protos";
import { log } from "./logger";
import { Benchmark, BenchmarkContext, Metrics } from "./schema";

/**
 * Uploads Metrics to Google Cloud Monitoring.
 */
export const uploadMetrics = async (
  client: MetricServiceClient,
  benchmark: Benchmark,
  timeInSeconds: number = Date.now() / 1000) => {

  const projectId = process.env.GCLOUD_PROJECT || process.env.GOOGLE_CLOUD_PROJECT;
  if (!projectId) {
    throw Error('Unknown project id.');
  }

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

  const timeSeries: google.monitoring.v3.ITimeSeries[] = [];
  for (let i = 0; i < knownMetricTypes.length; i += 1) {
    const metricType = knownMetricTypes[i];
    const metric = metrics[metricType];
    if (metric) {
      timeSeries.push(
        ...createTimeSeries(
          projectId,
          metricType,
          benchmark,
          metric,
          timeInSeconds
        )
      );
    }
  }

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
        log(`Error creating metrics for ${request.name}`);
      });
  });
  return Promise.all(uploads);
}

const createTimeSeries = (
  projectId: string,
  metricName: string,
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
          brand: benchmark.context.build.brand,
          device: benchmark.context.build.device,
          fingerprint: benchmark.context.build.fingerprint,
          model: benchmark.context.build.model,
          sdkVersion: `${benchmark.context.build.version.sdk}`
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
