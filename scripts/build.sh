#!/bin/sh
set -e
cd $(dirname $0)
cd ..
   
echo "Usage: $0 <container-registry-URL>/<project>"
echo "==============================================================================================================="
echo "IMPORTANT: you must be logged in your Container Registry through docker login otherwise the building will fail."
echo "==============================================================================================================="
echo
# Verify if a container registry was informed
REGISTRY=$1
if [[ $# -eq 0 ]]; then
    read -n 1 -p "No Container Registry URL was informed to upload the built container images to. Do you wish to continue?" answer
    echo
    case ${answer:0:1} in
        y|Y )
            echo Yes
            read -rep $'Please type in the Container Registry URL:\n' REGISTRY
        ;;
        * )
            echo No
            exit 1
        ;;
    esac
fi
echo
echo "Using the following Container Registry URL:"
echo "$REGISTRY"
echo

./gradlew reader:publishToMavenLocal
ret=$?
if [ $ret -ne 0 ]; then
  exit $ret
fi

./gradlew book:publishToMavenLocal
ret=$?
if [ $ret -ne 0 ]; then
  exit $ret
fi

./gradlew build --parallel
if [ $ret -ne 0 ]; then
  exit $ret
fi

# Building and pushing container images
./gradlew reader:bootBuildImage --imageName $REGISTRY/library-reader:1.0.0
if [ $ret -ne 0 ]; then
  exit $ret
fi
docker push $REGISTRY/library-reader:1.0.0
if [ $ret -ne 0 ]; then
  exit $ret
fi
./gradlew book:bootBuildImage --imageName $REGISTRY/library-book:1.0.0
if [ $ret -ne 0 ]; then
  exit $ret
fi
docker push $REGISTRY/library-book:1.0.0
if [ $ret -ne 0 ]; then
  exit $ret
fi
./gradlew application:bootBuildImage --imageName $REGISTRY/library-application:1.0.0
if [ $ret -ne 0 ]; then
  exit $ret
fi
docker push $REGISTRY/library-application:1.0.0
if [ $ret -ne 0 ]; then
  exit $ret
fi

exit 0
