#!/bin/sh
grep MemFree /proc/meminfo | tr -s ' ' | cut -d ' ' -f 2
