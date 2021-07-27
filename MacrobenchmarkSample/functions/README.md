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

```bash
# Example: Setup Environment Variables.
package_name="com.example.macrobenchmark.target"
device_configurations='["flame-30-en-portrait"]'
```
