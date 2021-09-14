#!/bin/sh
set -e
cd $(dirname $0)
cd ..

echo "Usage: $0 <Concourse target>"
echo
# Verify if a target was informed
TARGET=$1
if [[ $# -eq 0 ]]; then
    read -n 1 -p "No Concourse target was informed. Do you wish to continue?" answer
    echo
    case ${answer:0:1} in
        y|Y )
            echo Yes
            read -rep $'Please type in the desired Concourse target:\n' TARGET
        ;;
        * )
            echo No
            exit 1
        ;;
    esac
fi
echo
echo "Using the following Concourse target:"
echo "$TARGET"
echo

fly -t $TARGET set-pipeline -p library-msa -c ./pipeline.yaml