#!/bin/bash
name=$1
ts=$(date +"%Y%m%d_%H%M%S")
touch ../src/main/resources/db/dev-migration/V${ts}__${name}.sql