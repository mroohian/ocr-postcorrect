#!/bin/bash

printUsage () {
    echo >&2 " Usage: $0 dbMode db mode [param]"
    echo >&2
    echo >&2 " Available db modes:"
    echo >&2 " - local"
    echo >&2 " - remote"
    echo >&2
    echo >&2 " Available modes:"
    #echo >&2 " - test"
    echo >&2 " - tesseract"
    echo >&2 " - generateArchive [param=archivePath]"
    echo >&2 " - levCorpa"

    exit 1
}

[ "$#" -eq 3 ] || [ "$#" -eq 4 ] || printUsage
export BASE_DIR="`pwd`/"
export DB_MODE="$1"
export DB="$2"
export MODE="$3"
export PARAM="$4"

if [[ -z "${RUN_CMD}" ]]; then
    export RUN_CMD="java"
fi

if [[ -z "${JAVA_HOME}" ]]; then
    export JAVA_HOME=/usr/lib/jvm/java-1.7.0-openjdk-amd64
    echo setting java path ro ${JAVA_HOME}
fi

source execute.sh de.iisys.ocr.App \
${BASE_DIR} \
${DB_MODE} \
${DB} \
${MODE} \
${PARAM}


#-agentlib:jdwp=transport=dt_socket,address=127.0.0.1:46758,suspend=y,server=n \
