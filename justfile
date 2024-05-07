
# Start the database
start-infra:
    podman run --ulimit memlock=-1:-1 -d -it --rm=true \
        -e POSTGRES_USER=movies \
        -e POSTGRES_PASSWORD=movies \
        -e POSTGRES_DB=movies \
        -p 5432:5432 postgres:15-bullseye

    podman run -d -p 9092:9092 -it --rm \
        -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
        quay.io/ogunalp/kafka-native:0.8.0-kafka-3.7.0

stop-infra:
    podman stop $(podman ps -q)

build-images:
    cd movie-rater && quarkus build --clean -Dquarkus.container-image.push=true -DskipTests -Dquarkus.profile=kubernetes
    cd movie-stream && quarkus build --clean -Dquarkus.container-image.push=true -DskipTests

kube-prerequisites:
    kubectl apply -f kubernetes/kafka.yaml -f kubernetes/database.yaml -f kubernetes/routes.yaml -f kubernetes/secret.yaml
    kubectl wait pods -l name=kafka --for condition=Ready --timeout=90s
    kubectl wait pods -l name=movies-db --for condition=Ready --timeout=90s

deploy-to-kube: kube-prerequisites
    cd movie-rater && quarkus deploy kubernetes -Dquarkus.profile=kubernetes
    cd movie-stream && quarkus deploy kubernetes
    echo "Movie Stream route: https://`oc get routes -o json --field-selector metadata.name=movie-stream | jq -r '.items[0].spec.host'`"
    echo "Movie Rater route: https://`oc get routes -o json --field-selector metadata.name=movie-rater | jq -r '.items[0].spec.host'`"

add-movies host:
    curl -X POST -H "Content-Type: application/json" -d '{"title":"The Shawshank Redemption","rating":4}' {{host}}/movies
    curl -X POST -H "Content-Type: application/json" -d '{"title":"The Godfather", "rating": 4}' {{host}}/movies
    curl -X POST -H "Content-Type: application/json" -d '{"title":"The Dark Knight", "rating: 3}' {{host}}/movies
    curl -X POST -H "Content-Type: application/json" -d '{"title":"The Lord of the Rings: The Return of the King", "rating": 4}' {{host}}/movies
    curl -X POST -H "Content-Type: application/json" -d '{"title":"Pulp Fiction", "rating": 3}' {{host}}/movies
    curl -X POST -H "Content-Type: application/json" -d '{"title":"Inception", "rating": 5}' {{host}}/movies



