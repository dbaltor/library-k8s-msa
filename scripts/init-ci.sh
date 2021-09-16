#!/bin/sh
set -e 
cd $(dirname $0)
cd ..

### commented out to be possibly used by generic pipeline ###
#echo
#echo "Usage: $0 <registry-usr> <registry-pwd>"
echo
echo "==============================================================================================================="
echo "Pre-requisite: helm installed on your workstation"
echo "==============================================================================================================="
echo
### commented out to be possibly used by generic pipeline ###
# Verify if parameters have been informed
#DOCKER_USR=$1
#DOCKER_PWD=$2
#if [[ $# -eq 0 ]]; then
#    read -n 1 -p "The required parameters have not been informed. Do you wish to continue?" answer
#    echo
#    case ${answer:0:1} in
#        y|Y )
#            echo Yes
#            echo
#            read -rep $'Please type in the Container Registry username:\n' DOCKER_USR
#            echo
#            read -sp $'Please type in the Container Registry password:\n' DOCKER_PWD
#        ;;
#        * )
#            echo No
#            exit 1
#        ;;
#    esac
#fi
#echo
#echo "*** Using the following Container Registry username:"
#echo "$DOCKER_USR"

# Creating namespace to be used 
NAMESPACE="concourse"

# Installing concourse
helm repo add concourse https://concourse-charts.storage.googleapis.com/
helm install concourse \
  concourse/concourse \
  --namespace $NAMESPACE \
  --create-namespace

echo
echo "==============================================================================================================="
echo "Congratulations! Concourse has been successfully installed in the $NAMESPACE namespace."
echo
echo "Please execute the steps below before running the reminder of this script"
echo "-------------------------------------------------------------------------"
echo
echo "To have access to the Concourse GUI without DNS configuration..."
echo "Add the line below to your /etc/hosts file:"
echo "127.0.0.1 concourse-web.concourse.svc.cluster.local"
echo
echo "Now run the following command in a separate shell:"
echo "kubectl port-forward svc/concourse-web 8080"
echo
echo "The Concourse web UI can be accessed at http://concourse-web.concourse.svc.cluster.local:8080"
echo "Log in with the username/password as test/test."
echo "You might get the 'Invalid Token' error message if your browser changes the URL to 127.0.0.1."
echo "Just replace the IP 127.0.0.1 for the hostname 'concourse-web.concourse.svc.cluster.local'"
echo
echo "Install fly (the Concourse CLI) by downloading it from the web UI."
echo "The download link is located at the lower right corner of the page."
echo "Install the binary executing the following command:"
echo "sudo install -o 0 -g 0 -m 0755 fly /usr/local/bin/fly"
echo "==============================================================================================================="
echo
read -p "Press ENTER to continue and configure the pipeline..."

# Creating secrets required by the pipelines
### commented out to be possibly used by generic pipeline ###
#kubectl create secret generic registry -n concourse-main \
#  --from-literal=username=$DOCKER_USR \
#  --from-literal=password=$DOCKER_PWD
kubectl create secret generic k8s-config -n concourse-main \
  --from-file=kubeconfig=$HOME/.kube/config

# Targeting the newly installed Concourse as the test user
fly -t ci login -c http://concourse-web.concourse.svc.cluster.local:8080 -u test -p test

# Installing the pipeline
./ci/scripts/set-pipeline.sh ci

# Starting the pipeline
fly -t ci unpause-pipeline -p library-msa

echo
echo "==============================================================================================================="
echo "You can finally follow your pipeline's execution at"
echo "http://concourse-web.concourse.svc.cluster.local:8080/teams/main/pipelines/library-msa"
echo "==============================================================================================================="

exit 0
