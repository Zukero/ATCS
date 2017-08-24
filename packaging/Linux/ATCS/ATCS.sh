#!/bin/bash
ATCS_DIR=$(dirname $(readlink -f "$0" || greadlink -f "$0" || stat -f "$0"))
MAX_MEM=512M
CP=$(find ${ATCS_DIR}/lib/ -name '*.jar' | paste -sd: -)
JAVA=java
JAVA_OPTS='-DFONT_SCALE=1.0 -Dswing.aatext=true'
ENV_FILE=${ATCS_DIR}/ATCS.env
MAIN_CLASS=com.gpl.rpg.atcontentstudio.ATContentStudio

if [ -f ${ENV_FILE} ]
then
  source ${ENV_FILE}
else
  echo "#MAX_MEM=${MAX_MEM}" > ${ENV_FILE}
  echo "#JAVA=${JAVA}" >> ${ENV_FILE}
  echo "#JAVA_OPTS=${JAVA_OPTS}" >> ${ENV_FILE}
  echo "" >> ${ENV_FILE}
fi

export ENV_FILE

$JAVA ${JAVA_OPTS} -Xmx${MAX_MEM} -cp ${CP} ${MAIN_CLASS}
