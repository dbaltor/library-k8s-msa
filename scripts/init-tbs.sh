#!/bin/bash
set -e 
cd $(dirname $0)
cd ..

echo
echo "Usage: $0 <registry-URL> <registry-project> <registry-ca-file> <registry-usr> <registry-pwd> <tanzunet-usr> <tanzunet-pwd> <git-repo>"
echo
echo "==============================================================================================================="
echo "Pre-requisites:" 
echo "You are going to need of curl, kp cli (https://network.pivotal.io/products/build-service/)"
echo "as well as all the VMware Carvel tools (https://carvel.dev)"
echo "==============================================================================================================="
echo
# Verify if parameters have been informed
REGISTRY=$1
REGISTRY_PRJ=$2
DOCKER_CA=$3
DOCKER_USR=$4
DOCKER_PWD=$5
TANZU_USR=$6
TANZU_PWD=$7
GIT_REPO=$8
if [[ $# -eq 0 ]]; then
    read -n 1 -p "The required parameters have not been informed. Do you wish to continue?" answer
    echo
    case ${answer:0:1} in
        y|Y )
            echo Yes
            read -rep $'Please type in the Container Registry URL:\n' REGISTRY
            echo
            read -rep $'Please type in the project name in the Registry to store the built images:\n' REGISTRY_PRJ
            echo
            read -rep $'Please type in the path to the Container Registry CA file:\n' DOCKER_CA
            echo
            read -rep $'Please type in the Container Registry username:\n' DOCKER_USR
            echo
            read -sp $'Please type in the Container Registry password:\n' DOCKER_PWD
            echo
            read -rep $'Please type in the Tanzu Network username:\n' TANZU_USR
            echo
            read -sp $'Please type in the Tanzu Network password:\n' TANZU_PWD
            echo
            read -sp $'Please type in the Git Repo to be used:\n' GIT_REPO
        ;;
        * )
            echo No
            exit 1
        ;;
    esac
fi
echo
echo "*** Using the following Container Registry URL:"
echo "$REGISTRY"
echo
echo "*** Using the following project in the Container Registry to store the built images:"
echo "$REGISTRY_PRJ"
echo
echo "*** Using the following Container Registry CA file:"
echo "$DOCKER_CA"
echo
echo "*** Using the following Container Registry username:"
echo "$DOCKER_USR"
echo
echo "*** Using the following Tanzu Network username:"
echo "$TANZU_USR"
echo
echo "*** Using the following Git Repo to retrive the source code:"
echo "$GIT_REPO"
echo

# Namespace to be used by kapp and kpack images
NAMESPACE="library-tbs"
kubectl create ns $NAMESPACE

# Logging in the docker repos
echo $DOCKER_PWD | docker login -u $DOCKER_USR --password-stdin $REGISTRY
echo $TANZU_PWD | docker login -u $TANZU_USR --password-stdin registry.pivotal.io 

# Creating the tbs project in harbor
curl -H "Content-Type: application/json" \
  -u "$DOCKER_USR":"$DOCKER_PWD" -i -k \
  -X POST -d '{"project_name": "tbs", "public": true}' \
  https://"$REGISTRY":443/api/v2.0/projects

# Copying bundle from the Tanzu Network to the private registry
imgpkg copy -b "registry.pivotal.io/build-service/bundle:1.2.2" --to-repo $REGISTRY/tbs/build-service --registry-verify-certs=false
if [ -n "$ret" ] && [ $ret -ne 0 ]; then
  exit $ret
fi

# Downloading image
imgpkg pull -b "$REGISTRY/tbs/build-service:1.2.2" -o /tmp/bundle --registry-verify-certs=false
if [ -n "$ret" ] && [ $ret -ne 0 ]; then
  exit $ret
fi

# Preparing valyes.yaml file for installationa
cat ./tbs/values.template \
  | sed 's^((REGISTRY))^'"$REGISTRY"'^g' \
  | sed 's^((DOCKER_USR))^'"$DOCKER_USR"'^g' \
  | sed 's^((DOCKER_PWD))^'"$DOCKER_PWD"'^g' \
  | sed 's^((TANZU_USR))^'"$TANZU_USR"'^g' \
  | sed 's^((TANZU_PWD))^'"$TANZU_PWD"'^g' \
  > /tmp/deploy-values.yaml

# Deploying TBS
ytt -f /tmp/deploy-values.yaml \
    -f /tmp/bundle/config/ \
    -f $DOCKER_CA \
    | kbld -f /tmp/bundle/.imgpkg/images.yml -f- \
    | kapp deploy -a tanzu-build-service -n $NAMESPACE -f- -y
if [ -n "$ret" ] && [ $ret -ne 0 ]; then
  exit $ret
fi

# Deleting the file with credentials
rm /tmp/deploy-values.yaml

echo
echo "==============================================================================================================="
echo "Verifying if Dependency Updaters have been correctly installed"
echo "==============================================================================================================="
kubectl get TanzuNetDependencyUpdaters -A

echo
echo "==============================================================================================================="
echo "Verifying if builders have been correctly installed"
echo "==============================================================================================================="
kp clusterbuilder list

echo
echo "==============================================================================================================="
echo "Installing kpack images for the microservices in the $NAMESPACE namespace"
echo "==============================================================================================================="

REGISTRY_PASSWORD=$DOCKER_PWD kp secret create harbor-secret --registry $REGISTRY --registry-user $DOCKER_USR -n $NAMESPACE

cat ./tbs/library-kp.yaml \
  | sed 's@((NAMESPACE))@'"$NAMESPACE"'@g' \
  | sed 's@((REGISTRY))@'"$REGISTRY"'@g' \
  | sed 's@((REGISTRY_PRJ))@'"$REGISTRY_PRJ"'@g' \
  | sed 's@((GIT_REPO))@'"$GIT_REPO"'@g' \
  | kubectl apply -f -

cat ./tbs/reader-kp.yaml \
  | sed 's@((NAMESPACE))@'"$NAMESPACE"'@g' \
  | sed 's@((REGISTRY))@'"$REGISTRY"'@g' \
  | sed 's@((REGISTRY_PRJ))@'"$REGISTRY_PRJ"'@g' \
  | sed 's@((GIT_REPO))@'"$GIT_REPO"'@g' \
  | kubectl apply -f -

cat ./tbs/book-kp.yaml \
  | sed 's@((NAMESPACE))@'"$NAMESPACE"'@g' \
  | sed 's@((REGISTRY))@'"$REGISTRY"'@g' \
  | sed 's@((REGISTRY_PRJ))@'"$REGISTRY_PRJ"'@g' \
  | sed 's@((GIT_REPO))@'"$GIT_REPO"'@g' \
  | kubectl apply -f -

exit 0
