#!/bin/bash
set -e

echo "==============================================================================================================="
echo "WARNING: Library namespace and its microservices, Contour ingress controller, SCG4K8s and API Portal"
echo "are all going to be removed!"
echo "==============================================================================================================="
echo
read -n 1 -p "Do you really wish to continue?" answer
echo
case ${answer:0:1} in
    y|Y )
        echo Yes
    ;;
    * )
        echo No
        exit 1
    ;;
esac
kubectl delete ns library
kubectl delete ns projectcontour
helm uninstall spring-cloud-gateway -n spring-cloud-gateway
kubectl delete namespace spring-cloud-gateway
helm uninstall api-portal -n api-portal
kubectl delete namespace api-portal

