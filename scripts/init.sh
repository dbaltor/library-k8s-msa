#!/bin/bash
set -e
cd $(dirname $0)
cd ..

echo "Usage: $0 <container-registry-URL>/<project>"
echo
# Verify if the container registry was informed
REGISTRY=$1
if [[ $# -eq 0 ]]; then
    read -n 1 -p "No Container Registry URL was informed to download the container images from. Do you wish to continue?" answer
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

# Create library namespace
kubectl create ns library
kubectl config set-context --current --namespace=library

# Configure default service as per required by Spring Cloud Kubernetes
kubectl apply -f ./k8s/cluster-reader-library-default-sa.yaml

# Creating required service instances
kubectl apply -f ./k8s/db-secret.yaml
helm install reader-service-db --set existingSecret=db-secret,postgresqlDatabase=readerdb bitnami/postgresql
helm install book-service-db --set existingSecret=db-secret,postgresqlDatabase=bookdb bitnami/postgresql

# Deploying microservices
#########################
kubectl apply -f ./k8s/book-config.yaml
#kubectl apply -f book-deployment.yaml
cat ./k8s/book-deployment.yaml | sed 's@((REGISTRY))@'"$REGISTRY"'@g' | kubectl apply -f -
kubectl apply -f ./k8s/reader-config.yaml
#kubectl apply -f reader-deployment.yaml
cat ./k8s/reader-deployment.yaml | sed 's@((REGISTRY))@'"$REGISTRY"'@g'| kubectl apply -f -
kubectl apply -f ./k8s/application-config.yaml
#kubectl apply -f application-deployment.yaml
cat ./k8s/application-deployment.yaml | sed 's@((REGISTRY))@'"$REGISTRY"'@g'| kubectl apply -f -

# Configure ingress controller
./scripts/install-contour-ingress.sh
kubectl apply -f ./k8s/virtual-library-ingress-http.yaml

# wait for public IP assigned
until kubectl get ing | awk '$1 !~ /NAME/ { print NF}' | grep "6"; do sleep 2; done
LIBRARY_IP=$(kubectl get ing | awk '$1 !~ /NAME/ { print $4}')
LIBRARY_URL=$(kubectl get ing | awk '$1 !~ /NAME/ { print $3}')
echo
echo
echo
echo "########################################################################################"
echo "All set! "
echo "Add the line below to your /etc/hosts file and point your browser to http://$LIBRARY_URL"
echo
echo "$LIBRARY_IP $LIBRARY_URL"
echo "########################################################################################"
