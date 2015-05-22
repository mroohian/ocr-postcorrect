#!/bin/bash

rm -rf ./db/test-db1/
./run.sh remote test-db1 generateArchive mnt/dump/archive/en/computer/TechNews/y2014/m8/d1/

#./runBG.sh local /home/reza/releases/orientdb/databases/complete-de1 generateArchive mnt/dump/archive/de/
