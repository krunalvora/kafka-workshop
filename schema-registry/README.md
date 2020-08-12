Solution for:

io.confluent.kafka.schemaregistry.storage.exceptions.StoreInitializationException: The retention policy of the schema topic _schemas is incorrect. Expected cleanup.policy to be 'compact' but it is delete

```bash
kafka-configs.sh --zookeeper localhost --entity-type topics --entity-name _schemas --alter --add-config cleanup.policy=compact
```
