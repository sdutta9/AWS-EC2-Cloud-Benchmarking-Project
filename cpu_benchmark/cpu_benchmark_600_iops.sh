#!/bin/bash


javac cpu_benchmark_600sample.java

for (( i = 0; i < 600; i++ ))
do
	echo "Samples are taken for integer :" $i
	java  cpu_benchmark_600sample 2 4 >> Sample"_"600"_integer".txt
	sleep 0.6
done
