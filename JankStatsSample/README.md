JankStats Sample
===================================

This sample project shows how to use the JankStats library.

It includes multiple simple sample activities:

* [JankLoggingActivity](app/src/main/java/com/example/jankstats/JankLoggingActivity.kt)
creates a JankStats object and tracks the performance of a simple RecyclerView
UI, logging every frame to logcat via the JankStats API.

* [JankAggregatorActivity](app/src/main/java/com/example/jankstats/JankAggregatorActivity.kt)
creates an activity much like JankLoggingActivity, but with an aggregation layer
on top, using the utility class
[JankStatsAggregator](app/src/main/java/com/example/jankstats/JankStatsAggregator.kt).
JankStatsAggregator uses the same per-frame mechanism of JankStats under the hood,
but collects that per-frame information and saves it for later logging when the client
asks for it.

### Running

Open the project in Android Studio and run the activities as you usually would.
Scrolling the RecyclerView will cause performance metrics to be sent to the activity.
In the case of JankLoggingActivity, that data will be output inline to logcat.
For JankAggregatorActivity, the data will be cached internally and output later,
when requested (which for that activity happens when it goes to the background).

License
-------

Copyright 2022 The Android Open Source Project, Inc.

Licensed to the Apache Software Foundation (ASF) under one or more contributor
license agreements.  See the NOTICE file distributed with this work for
additional information regarding copyright ownership.  The ASF licenses this
file to you under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License.  You may obtain a copy of
the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
License for the specific language governing permissions and limitations under
the License.
