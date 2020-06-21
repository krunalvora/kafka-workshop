```bash
docker run -d   --net=host   --name=schema-registry   -p 8081:8081 -e SCHEMA_REGISTRY_KAFKASTORE_CONNECTION_URL=172.17.0.1:2181   -e SCHEMA_REGISTRY_HOST_NAME=172.17.0.1   -e SCHEMA_REGISTRY_LISTENERS=http://localhost:8081   -e SCHEMA_REGISTRY_DEBUG=true confluentinc/cp-schema-registry:5.5.0
```

Follow the schema registry tutorial [here](https://docs.confluent.io/current/schema-registry/schema_registry_tutorial.html).
