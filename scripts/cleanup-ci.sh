#!/bin/bash
set -e

echo "==============================================================================================================="
echo "WARNING: Concourse is going to be removed!"
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
helm delete concourse
kubectl delete ns concourse-main
kubectl delete ns concourse