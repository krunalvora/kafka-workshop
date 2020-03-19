# ACLs

## Table of Contents
1. [Kafka Server Authorization Properties](#kafka-server-authorization-properties)
2. [ACL commands](#acl-commands)
    1. [List Topic ACL](#list-topic-acl)
    2. [Add ClusterAction Access](#add-clusteraction-access)
    3. [Add Read Topic Access](#add-read-topic-access)
    4. [Add Write Topic Access](#add-write-topic-access)
    5. [Remove Topic Access](#remove-topic-access)
3. [Debugging Authorization Issues](#debugging-authorization-issues)

## Kafka Server Authorization Properties

```properties
authorizer.class.name=kafka.security.auth.SimpleAclAuthorizer
super.users=User:admin;User:kafka
allow.everyone.if.no.acl.found=false
security.inter.broker.protocol=SSL

ssl.principal.mapping.rules=RULE:^CN=(.*?)/$1/L
```

> The rule in `ssl.principal.mapping.rules` extracts the user identity out of the SSL username. For example, `"CN=kafka"` ends up with user `kafka`. For more details, refer [here](https://kafka.apache.org/documentation/#security_authz_ssl).

```bash
kafka-topics.sh --zookeeper $KAFKA_SERVER:2181 --create --topic acl-test --replication-factor 1 --partitions 1
```


## ACL commands
### List Topic ACL
```bash
kafka-acls.sh --authorizer-properties zookeeper.connect=$KAFKA_SERVER:2181 --list --topic acl-test
```

### Add ClusterAction Access
```bash
kafka-acls.sh --authorizer-properties zookeeper.connect=zookeeper:2181 --add --allow-principal "User:admin" --cluster --operation ClusterAction
```

### Add Read Topic Access
```bash
kafka-acls.sh --authorizer-properties zookeeper.connect=$KAFKA_SERVER:2181 add --allow-principal "User:reader" --allow-principal "User:writer" --operation Read --group=* --topic acl-test
```

### Add Write Topic Access
```bash
kafka-acls.sh --authorizer-properties zookeeper.connect=$KAFKA_SERVER:2181 --add --allow-principal "User:writer" --operation Write --topic acl-test
```

### Remove Topic Access 
```bash
kafka-acls.sh --authorizer-properties zookeeper.connect=$KAFKA_SERVER:2181 --remove --allow-principal "User:reader" --operation Read --topic acl-test
```

## Debugging Authorization Issues

Log file:  `<KAFKA_INSTALLATION_DIR>/kafka/logs/kafka-authorizer.log`
Default log level is INFO, only DENIED logs are available.

To enable success logs, change `<KAFKA_INSTALLATION_DIR>/kafka/conf/log4j.properties`, set
```properties
log4j.logger.kafka.authorizer.logger=DEBUG
```



