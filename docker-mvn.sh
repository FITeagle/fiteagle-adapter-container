#!/bin/sh
if [ $# -ge 0 ]; then
	ARGS=$@
else
	ARGS="mvn clean compile test exec:java -Dexec.mainClass=org.fiteagle.adapters.containers.Playground -Dexec.classpathScope=test"
fi

M2_CACHE="-v ${PWD}/m2:/root/.m2"
CMD_ENV=""
CMD_WORK="-v $PWD:/usr/src/adapter ${M2_CACHE} -w /usr/src/adapter"
CMD1="docker run -it --rm --name adapter"
CMD2="maven:3-jdk-8"
CMD="${CMD1} ${CMD_WORK} ${M2_CACHE} ${CMD2} ${ARGS}"

echo $CMD
$CMD
RET=$?

echo "**********************"
#echo "** Note: for deployment you need to provide CI_DEPLOY_USERNAME & CI_DEPLOY_PASSWORD envoriment vars!"
#echo "** e.g. ${CMD1} ${CMD_WORK} ${CMD_ENV} ${M2_CACHE} ${CMD2} mvn deploy -DskipTests --settings .travis/settings.xml"
echo "** examples:"
echo "** mvn compile test exec:java -Dexec.mainClass=org.fiteagle.adapters.containers.Playground -Dexec.classpathScope=test"
echo "**********************"

exit $RET
