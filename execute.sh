#!/bin/bash

export ARGS=$@

if [[ -z "${RUN_CMD}" ]]; then
    export RUN_CMD="java -Xms2048M -Xmx10240M"
fi

${RUN_CMD} \
-Dfile.encoding=UTF-8 \
-Dawt.useSystemAAFontSettings \
-classpath \
./bin\
:${JAVA_HOME}/jre/lib/javazic.jar\
:${JAVA_HOME}/jre/lib/jsse.jar\
:${JAVA_HOME}/jre/lib/charsets.jar\
:${JAVA_HOME}/jre/lib/rt.jar\
:${JAVA_HOME}/jre/lib/rhino.jar\
:${JAVA_HOME}/jre/lib/management-agent.jar\
:${JAVA_HOME}/jre/lib/resources.jar\
:${JAVA_HOME}/jre/lib/compilefontconfig.jar\
:${JAVA_HOME}/jre/lib/jce.jar\
:${JAVA_HOME}/jre/lib/ext/sunjce_provider.jar\
:${JAVA_HOME}/jre/lib/ext/dnsns.jar\
:${JAVA_HOME}/jre/lib/ext/sunpkcs11.jar\
:${JAVA_HOME}/jre/lib/ext/java-atk-wrapper.jar\
:${JAVA_HOME}/jre/lib/ext/pulse-java.jar\
:${JAVA_HOME}/jre/lib/ext/zipfs.jar\
:${JAVA_HOME}/jre/lib/ext/localedata.jar\
:./out/production/LevDistCorpa\
:./out/production/ocrCorpa\
:./out/production/Tess4J\
:./lib/jna-dist/jna.jar\
:./lib/ghost-dist/ghost4j-0.5.1.jar\
:./lib/stanford-corenlp/stanford-corenlp-3.3.1.jar\
:./Modules/Tess4J/lib/jai_imageio.jar\
:./lib/orientdb-dist/orient-commons-2.0-SNAPSHOT.jar\
:./lib/orientdb-dist/orientdb-core-2.0-SNAPSHOT.jar\
:./lib/orientdb-dist/blueprints-core-2.5.0.jar\
:./lib/orientdb-dist/orientdb-client-2.0-SNAPSHOT.jar\
:./lib/orientdb-dist/orientdb-graphdb-2.0-SNAPSHOT.jar\
:./lib/orientdb-dist/concurrentlinkedhashmap-lru-1.4.jar\
:./lib/orientdb-dist/gremlin-java-2.5.0.jar\
:./lib/orientdb-dist/groovy-1.8.9.jar\
:./lib/orientdb-dist/jna-platform-4.0.0.jar\
:./lib/orientdb-dist/snappy-java-1.1.0.1.jar\
:./lib/orientdb-dist/gremlin-groovy-2.5.0.jar\
:/opt/idea-IC-135.1230/lib/idea_rt.jar \
${ARGS}

