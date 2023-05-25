# Vue 3 side project: ESSCD

## 1. web-service
used to render the web page for clients.
### requirements
- Node.js >= 14
- NPM == 6.14.5
- Vue.js >= 3.0 (with Typescript)
### how to run
```bash
cd web-service
npm run serve
```

## 2. api-service
used to provide the data for web-service.
### requirements:
- Java Runtime >= 8
- Spring Boot >= 2.0.0
- Maven >= 3.5.3
- Spring MVC
- Docker >= 20
- minikube >= 1.24.0
### how to run
```bash
cd api-service/esscd
./mvnw spring-boot:run
```
### how to deploy
```bash
minikube start

# if exists, delete the old one
kubectl delete pods esscd-deployment

cd api-service
./deploy.sh
```
