cd esscd
./mvnw package
docker build -t rainboltz/esscd-k8s .
docker push rainboltz/esscd-k8s
kubectl delete pods esscd-deployment
kubectl apply -f ../deploy-configs/deployment.yaml
sleep 10
kubectl port-forward esscd-deployment 8080:8080 &
cd ../
