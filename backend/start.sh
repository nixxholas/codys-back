#!/bin/bash
gradle --version
gradle build --continuous &
gradle bootRun
