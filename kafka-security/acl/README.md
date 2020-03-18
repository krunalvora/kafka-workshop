# ACLs

## Kafka Server Properties

```properties
authorizer.class.name=kafka.security.auth.SimpleAclAuthorizer
super.users=User:admin;User:kafka
allow.everyone.if.no.acl.found=false
security.inter.broker.protocol=SSL
```

```bash
kafka-topics.sh --zookeeper localhost:2181 --create --topic acl-test --replication-factor 1 --partitions 1
```

```bash
kafka-acls.sh --authorizer-properties zookeeper.connect=localhost:2181 --add --allow-principal "User:CN=mylaptop" --allow-principal "User:CN=localhost" --operation Read --group=* --topic acl-test

kafka-acls.sh --authorizer-properties zookeeper.connect=localhost:2181 --add --allow-principal "User:CN=mylaptop" --allow-principal "User:CN=localhost" --operation Write --topic acl-test
```

```bash
kafka-acls.sh --authorizer-properties zookeeper.connect=localhost:2181 --list --topic acl-test
```



