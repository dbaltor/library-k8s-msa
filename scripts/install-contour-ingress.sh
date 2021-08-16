#!/bin/bash
set -e

#source: https://projectcontour.io/getting-started/

kubectl apply -f https://projectcontour.io/quickstart/contour.yaml

# test with kuard
#kubectl apply -f https://projectcontour.io/examples/kuard.yaml
