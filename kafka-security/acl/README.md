# ACLs

## Kafka Server Properties

```properties
authorizer.class.name=kafka.security.auth.SimpleAclAuthorizer
super.users=User:admin;User:kafka
allow.everyone.if.no.acl.found=false
security.inter.broker.protocol=SASL_SSL
```

```bash
kafka-topics.sh --zookeeper $KAFKA_SERVER:2181 --create --topic acl-test --replication-factor 1 --partitions 1
```

Add operation for principals
```bash
kafka-acls.sh --authorizer-properties zookeeper.connect=$KAFKA_SERVER:2181 \
    --add \
    --allow-principal "User:reader" --allow-principal "User:writer" \
    --operation Read \
    --group=* \
    --topic acl-test

kafka-acls.sh --authorizer-properties zookeeper.connect=$KAFKA_SERVER:2181 \
    --add \
    --allow-principal "User:writer" \
    --operation Write \
    --topic acl-test
```

List ACL  principals on a topic
```bash
kafka-acls.sh --authorizer-properties zookeeper.connect=$KAFKA_SERVER:2181 \
    --list \
    --topic acl-test
```

Remove operation for principals
```bash
kafka-acls.sh --authorizer-properties zookeeper.connect=$KAFKA_SERVER:2181 \
    --remove \
    --allow-principal "User:reader" \
    --operation Read \
    --topic acl-test
```

## Debugging Authorization Issues

Log file:  `<KAFKA_INSTALLATION_DIR>/kafka/logs/kafka-authorizer.log`
Default log level is INFO, only DENIED logs are available.

To enable success logs, change `<KAFKA_INSTALLATION_DIR>/kafka/conf/log4j.properties`, set
```properties
log4j.logger.kafka.authorizer.logger=DEBUG
```



