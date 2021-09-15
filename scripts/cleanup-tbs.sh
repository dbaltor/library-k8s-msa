#!/bin/bash
set -e

echo "==============================================================================================================="
echo "WARNING: Tanzu Build Service is going to be removed!"
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

kapp delete -a tanzu-build-service -n library-tbs
kubectl delete ns library-tbs

