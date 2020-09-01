ConstraintLayout performance comparison
=======================================

This app is an example test set that compares how the different ViewGroups
(ConstraintLayout vs traditional layouts) affects the UI performance.

Introduction
------------

This app runs the measure/layout passes with the layout using
ConstraintLayout and using traditional layouts (RelativeLayout and LinearLayout),
both of them result in the same appearance but how the UI components are built is 
different.

![LayoutCodelab-UI](/ConstraintLayoutPerformance/art/layout-codelab.png)

While running the measure/layout passes, the performance of UI is measured 
by using [Systrace](https://developer.android.com/studio/profile/systrace-commandline.html) and 
[OnFrameMetricsAvailableListener](https://developer.android.com/reference/android/view/Window.OnFrameMetricsAvailableListener.html)

Pre-requisites
--------------

You need to know:
- Review the XML file to know how each layout is built
  - [Layout using ConstraintLayout](/app/src/main/res/layout/activity_constraintlayout.xml)
  - [Layout using traditional layouts](/app/src/main/res/layout/activity_traditional.xml)

How to run the tests
---------------

1. Download the code.
2. Open the terminal at the downloaded directory.
3. Run the following shell script `./run.sh <device_id>`
  - The script is going to generate two html files as a result of running Systrace.
    Each represents the performance result for the layout with ConstraintLayout and with
    traditional.

(Optional)
- The app also logs the measurement result using OnFrameMetricsAvailableListener, but it isn't
  exported to any external files. You can check those stats as well.

Measurement result example
---------------
Here is the example measurement result.

![Comparison example](/ConstraintLayoutPerformance/art/constraint-performance-comparison-example.png)
unit: ms, Time taken in measure and layout phases average of 100 frames

Measurement environment

| Device | Nexus 5X |
| :--------: | :----------: |
| Android Version | 8.0 |
| ConstraintLayout version | 1.0.2 |
