# Apply configmap of order
oc apply -f order-config.yaml

# Update configmap to order 
oc set env dc/order --from configmap/order-config

# Deployment
oc new-app  --name order https://github.com/yoonjk/order#cloud-native

# Expose route
oc expose svc/order --hostname order.apps....