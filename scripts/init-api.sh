#!/bin/bash
set -e
cd $(dirname $0)
cd ..

echo "Usage: $0 <path-to-SCG4K8s-install-dir> <path-to-api-portal-install-dir> <container-registry-URL>/<project>"
echo "==================================================================================================================="
echo "IMPORTANT: you must be logged in your Container Registry through docker login otherwise the installation will fail."
echo "==================================================================================================================="
echo
# Verify if the API Portal install dir was informed
SCG_PATH=$1
API_PORTAL_PATH=$2
REGISTRY=$3
if [[ $# -eq 0 ]]; then
    read -n 1 -p "No Gateway and API Portal installation directories as well as Container Registry URL have been informed. Do you wish to continue?" answer
    echo
    case ${answer:0:1} in
        y|Y )
            echo Yes
            read -rep $'Please type in the path to Spring Cloud Gateway for K8s install dir:\n' SCG_PATH
            echo
            read -rep $'Please type in the path to API Portal install dir:\n' API_PORTAL_PATH
            echo
            read -rep $'Please type in the Container Registry URL:\n' REGISTRY
        ;;
        * )
            echo No
            exit 1
        ;;
    esac
fi
echo
echo "Using the following path to the Spring Cloud Gateway for K8s install dir:"
echo "$SCG_PATH"
echo "Using the following path to the API Portal install dir:"
echo "$API_PORTAL_PATH"
echo "Using the following Container Registry URL:"
echo "$REGISTRY"
echo

# Run the SCG4K8s image relocation script
$SCG_PATH/scripts/relocate-images.sh $REGISTRY
# Install SCG4K8s
$SCG_PATH/scripts/install-spring-cloud-gateway.sh

# Run the API Portal image relocation script
$API_PORTAL_PATH/scripts/relocate-images.sh $REGISTRY
# Install API Portal
$API_PORTAL_PATH/scripts/install-api-portal.sh

# Deploying microservices
./scripts/init.sh $REGISTRY
echo
read -p "Press ENTER to continue"

# Deploy SCG4K8s instances
kubectl apply -f ./api/gateway-config.yaml

#Configure routes
kubectl apply -f ./api/github-route-config.yaml
kubectl apply -f ./api/github-route-mapping.yaml
kubectl apply -f ./k8s/book-route-config.yaml
kubectl apply -f ./k8s/book-route-mapping.yaml
kubectl apply -f ./k8s/reader-route-config.yaml
kubectl apply -f ./k8s/reader-route-mapping.yaml

# Connect API Portal to SCG4K8s
SCG_OPERATOR_IP=$(kubectl get svc -n spring-cloud-gateway | awk '$1 !~ /NAME/ { print $3}')
kubectl set env deployment.apps/api-portal-server -n api-portal API_PORTAL_SOURCE_URLS="http://$SCG_OPERATOR_IP/openapi, https://petstore3.swagger.io/api/v3/openapi.json"
kubectl rollout restart deployment api-portal-server -n api-portal

# Configure ingresses for SCG4K8s and API Portal
kubectl apply -f ./api/api-portal-ingress.yaml
kubectl apply -f ./api/gateway-ingress.yaml

API_PORTAL_IP=$(kubectl get ing -n api-portal | awk '$1 !~ /NAME/ { print $4}')
API_PORTAL_URL=$(kubectl get ing -n api-portal | awk '$1 !~ /NAME/ { print $3}')
GATEWAY_URL=$(kubectl get ing | awk '$1 ~ /library-gateway-ingress/ { print $3}')
echo
echo "##############################################################################################"
echo "Add the line below to your /etc/hosts file and access the API Portal at http://$API_PORTAL_URL"
echo
echo "$API_PORTAL_IP $API_PORTAL_URL $GATEWAY_URL"
echo "##############################################################################################"

