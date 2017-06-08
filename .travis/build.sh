#!/usr/bin/env bash

./sbt "project geowave-index" compile || { exit 1; }
./sbt "project hilbert" test          || { exit 1; }
./sbt "project zorder" test           || { exit 1; }
