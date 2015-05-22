#!/bin/bash

cat screenlog.0 | grep 'dir' | grep -v 'y[0-9][0-9][0-9][0-9]'
