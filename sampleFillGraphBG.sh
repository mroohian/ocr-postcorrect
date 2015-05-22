#!/bin/bash

rm -rf ./db/test-db2/
./runBG.sh remote test-db2 generateArchive mnt/dump/archive/de/computer/
