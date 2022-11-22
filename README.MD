# Simple WordCount Program

## Purpose
The purpose of this program is comparison performance between single-threading vs multi-threading programming.

## Processing flow

EnWiki.xml ----> Lazy XML Parser ----> WordCountProgram ----> Output

In multi-threading mode, we use pattern producer-consumer to perform this task.

## Simple benchmark result
Macbook 16 Inch, 16 GB, M1

Count 500K pages:
- IO time: 17.01s
- Single-threading:
  - Total time: 67.4s
- Multi-threading (3 consumer, 1 producer, 4 thread)
  - Total time: 27.9s (faster 2.4 times)

