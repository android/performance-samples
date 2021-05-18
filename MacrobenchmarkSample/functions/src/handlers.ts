import { EventContext } from "firebase-functions";
import { TestMatrix } from "firebase-functions/lib/providers/testLab";
import { Storage } from "@google-cloud/storage";
import { log } from "./logger";
import { Benchmarks } from "./schema";
import { MetricServiceClient } from "@google-cloud/monitoring";
import { uploadMetrics } from "./monitoring";
import * as express from 'express';

// The Namespace
const BENCHMARK = "benchmark";

// The key under which package name is saved.
// This is an environment variable.
const PACKAGE_NAME = 'package_name';

// The key under which well known device configurations are saved.
// This is an environment variable.
const DEVICE_CONFIGURATIONS = 'device_configurations';


export const firebaseTestLabHandler = async function (matrix: TestMatrix, context: EventContext) {
  const id = matrix.testMatrixId;
  const gcsPath = matrix.resultStorage.gcsPath;
  if (matrix.state == 'FINISHED') {
    // Completed FTL run.
    log(`Completed test run with id ${id}`);

    if (!gcsPath) {
      log('Unable to find an output storage path.')
      return;
    }

    const packageName = environmentConfig(PACKAGE_NAME);
    if (!packageName) {
      log('No package name specified. Look at `README.md` for more information.');
      return;
    }

    const deviceConfigurations = environmentConfig(DEVICE_CONFIGURATIONS);
    if (!deviceConfigurations) {
      log('No device configurations specified. Look at `README.md` for more information.');
      return;
    }

    log(`Using device configurations ${deviceConfigurations}`);
    const knownConfigurations = parseDeviceConfigurations(deviceConfigurations);
    if (!knownConfigurations) {
      log('Invalid device configurations.');
      return;
    }

    return _handleRequest(gcsPath, packageName, knownConfigurations);
  }
};

export const firebaseTestLabHttpsHandler = async (request: express.Request, response: express.Response) => {
  log(`Request payload ${JSON.stringify(request.body)}`);

  const gcsPath = request.body['gcsPath'];
  if (!gcsPath) {
    log('Unable to find an output storage path.')
    response.status(400).send('Need `gcsPath`.');
    return;
  }

  const packageName = environmentConfig(PACKAGE_NAME);
  if (!packageName) {
    log('No package name specified. Look at `README.md` for more information.');
    response.status(400).send('Need `packageName`.');
    return;
  }

  const deviceConfigurations = environmentConfig(DEVICE_CONFIGURATIONS);
  if (!deviceConfigurations) {
    log('No device configurations specified. Look at `README.md` for more information.');
    response.status(400).send('Need `device configurations`.');
    return;
  }

  log(`Using device configurations ${deviceConfigurations}`);
  const knownConfigurations = parseDeviceConfigurations(deviceConfigurations);
  if (!knownConfigurations) {
    log('Invalid device configurations.');
    response.status(400).send('Invalid `device configurations`.');
    return;
  }

  await _handleRequest(gcsPath, packageName, knownConfigurations);
  response.status(200);
};


const _handleRequest = async (gcsPath: string, packageName: string, knownConfigurations: string[]) => {
  log(`Looking for results in ${gcsPath}`);
  // pathComponents should be gcs: [bucketName] [fileName]
  const pathComponents = gcsPath.split('/').filter((c) => c && c.length > 0);

  const [gs, bucket, ...objectFragments] = pathComponents;
  const objectPath = objectFragments.join('/');
  log(`Parsed GCS Paths: Bucket(${bucket}), Object Path(${objectPath})`);

  const storage = googleStorage();
  const monitoring = googleMonitoring();

  for (let i = 0; i < knownConfigurations.length; i += 1) {
    // Known device configuration.
    const configuration = knownConfigurations[i];
    try {
      const benchmarks: Benchmarks = await readBenchmark(
        storage,
        bucket,
        objectPath,
        packageName,
        configuration
      );

      // Upload Metrics
      await uploadMetrics(monitoring, benchmarks);
      log('All done.');
    } catch (error) {
      log(`Error Processing Benchmark Data for ${configuration}.`, error);
    }
  }
};

const readBenchmark = async (
  storage: Storage,
  bucket: string,
  baseObjectPath: string,
  packageName: string,
  configuration: string): Promise<Benchmarks> => {

  const path = `${baseObjectPath}/${configuration}/artifacts/${packageName}-benchmarkData.json`;
  log(`Looking for gs://${bucket}/${path}`);
  const objectFile = storage.bucket(bucket).file(path);
  let objectExists = false;
  try {
    [objectExists] = await objectFile.exists();
  } catch (ignore) {
    // Do nothing.
  }
  if (!objectExists) {
    const message = `Unable to find object with path gs://${bucket}/${path}`;
    log(message);
    // rethrow
    throw Error(message);
  }

  log(`Processing object with path: ${path}`);
  const [rawBuffer] = await objectFile.download();
  const contents = rawBuffer.toString('utf-8');
  log(`Downloaded contents for path: ${path}`);
  return JSON.parse(contents) as Benchmarks;
};

const googleStorage = () => {
  return new Storage({
    maxRetries: 3,
    autoRetry: true
  });
};

const googleMonitoring = (): MetricServiceClient => {
  return new MetricServiceClient();
};

const environmentConfig = (key: string): string | undefined => {
  const env = process.env;
  const value = env[key];
  if (value != null) {
    log('process.env', key, value);
  }
  return env[key];
};

const parseDeviceConfigurations = (configuration: any): Array<string> | null => {
  try {
    return JSON.parse(configuration) as Array<string>;
  } catch (error) {
    log(`Unable to parse configuration ${configuration}`);
  }
  return null;
};
