# Cloud Functions for Macrobenchmarks.

## Initialize Project

```bash
gcloud config set project macrobenchmark-samples-6670d
```

## Deploy Functions

```bash
firebase deploy --only functions
```

### Setup Environment variables

Example:

```
PACKAGE_NAME="androidx.benchmark.macrobenchmarks.test"
DEVICE_CONFIGURATIONS=["x1q-29-en-portrait"]
```
