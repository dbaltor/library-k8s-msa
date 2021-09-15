#!/bin/sh

echo "==============================================================================================================="
echo "Deleting orfan resources left behind for failed TBS uninstall"
echo "==============================================================================================================="

kubectl delete customresourcedefinition/builders.kpack.io
kubectl delete clusterrole/build-service-admin-role
kubectl delete clusterrole/stacks-operator-manager-role
kubectl delete clusterrole/build-service-authenticated-role
kubectl delete clusterrole/custom-stack-viewer-role
kubectl delete clusterrole/build-service-secret-syncer-role
kubectl delete clusterrole/metrics-reader
kubectl delete clusterrolebinding/build-service-secret-syncer-role-binding 
kubectl delete customresourcedefinition/clusterstacks.kpack.io 
kubectl delete clusterrole/kpack-controller-admin 
kubectl delete clusterrole/build-service-dependency-updater-role 
kubectl delete clusterrole/build-service-user-role 
kubectl delete clusterrolebinding/build-service-admin-role-binding 
kubectl delete clusterrolebinding/stacks-operator-manager-rolebinding 
kubectl delete clusterrolebinding/proxy-rolebinding 
kubectl delete clusterrolebinding/kpack-controller-admin-binding 
kubectl delete clusterrolebinding/build-service-authenticated-role-binding 
kubectl delete customresourcedefinition/tanzunetdependencyupdaters.buildservice.tanzu.vmware.com 
kubectl delete customresourcedefinition/builds.kpack.io 
kubectl delete clusterrole/proxy-role 
kubectl delete clusterrole/build-service-warmer-role 
kubectl delete customresourcedefinition/clusterstores.kpack.io 
kubectl delete customresourcedefinition/sourceresolvers.kpack.io 
kubectl delete customresourcedefinition/clusterbuilders.kpack.io 
kubectl delete clusterrole/cert-injection-webhook-cluster-role 
kubectl delete validatingwebhookconfiguration/validation.webhook.kpack.io 
kubectl delete clusterrole/kpack-webhook-mutatingwebhookconfiguration-admin 
kubectl delete mutatingwebhookconfiguration/defaults.webhook.kpack.io 
kubectl delete mutatingwebhookconfiguration/defaults.webhook.cert-injection.tanzu.vmware.com 
kubectl delete customresourcedefinition/images.kpack.io 
kubectl delete clusterrolebinding/kpack-webhook-certs-mutatingwebhookconfiguration-admin-binding 
kubectl delete clusterrolebinding/cert-injection-webhook-cluster-role-binding 
kubectl delete clusterrolebinding/build-service-warmer-role-binding 
kubectl delete clusterrolebinding/build-service-dependency-updater-role-binding 
kubectl delete clusterrole/custom-stack-editor-role 
kubectl delete customresourcedefinition/customstacks.stacks.stacks-operator.tanzu.vmware.com 
