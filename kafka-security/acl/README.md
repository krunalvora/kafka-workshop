# ACLs

## Table of Contents
1. [Kafka Server Authorization Properties](#kafka-server-authorization-properties)
2. [ACL Principals](#acl-principals)
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
super.users=User:admin;User:kafka,User:localhost
allow.everyone.if.no.acl.found=false
security.inter.broker.protocol=SSL

ssl.principal.mapping.rules=RULE:^CN=(.*?)/$1/L
```

> The rule in `ssl.principal.mapping.rules` extracts the user identity out of the SSL username. For example, `"CN=kafka"` ends up with user `kafka`. For more details, refer [here](https://kafka.apache.org/documentation/#security_authz_ssl).

```bash
kafka-topics.sh --zookeeper localhost:2181 --create --topic topic1 --replication-factor 1 --partitions 1
```

## ACL Principals
Users identified by the Kafka authentication mechanism serve as the principals in Kafka authorization. Refer:

[Quick steps for creating an SSL Auth User](https://github.com/krunalvora/apachekafka101/tree/master/kafka-security/ssl#quick-steps-for-creating-an-ssl-auth-user) for SSL Authentication 

> Principal for SSL -> `dname` while generating key for `kafka.client.keystore.jks` (optionally with rules as shown above) 

[Quick steps for creating a SASL/Kerberos User](https://github.com/krunalvora/apachekafka101/tree/master/kafka-security/kerberos#quick-steps-for-creating-a-saslkerberos-user) for SASL/Kerberos Authentication

> Principal for SASL/Kerberos -> kerberos principal of the user 

[Quick steps for creating a SASL/OAUTHBEARER Application](https://github.com/krunalvora/apachekafka101/tree/master/kafka-security/kerberos#quick-steps-for-creating-a-sasloauthbearer-application) for SASL/OAUTHBEARER Authentication

> Principal for SASL/OAUTHBEARER -> OAuth2 Application Client ID

## ACL commands
### List Topic ACL
```bash
kafka-acls.sh --authorizer-properties zookeeper.connect=localhost:2181 --list --topic topic1
```

### Add ClusterAction Access
```bash
kafka-acls.sh --authorizer-properties zookeeper.connect=localhost:2181 --add --allow-principal "User:admin" --cluster --operation ClusterAction
```

### Add Read Topic Access
```bash
kafka-acls.sh --authorizer-properties zookeeper.connect=localhost:2181 --add --allow-principal "User:bob" --allow-principal "User:alice" --operation Read --group=* --topic topic1
```

### Add Write Topic Access
```bash
kafka-acls.sh --authorizer-properties zookeeper.connect=localhost:2181 --add --allow-principal "User:bob" --operation Write --topic topic1
```

### Remove Topic Access 
```bash
kafka-acls.sh --authorizer-properties zookeeper.connect=localhost:2181 --remove --allow-principal "User:bob" --operation Read --topic topic1
```

## Debugging Authorization Issues

Log file:  `/usr/local/kafka/logs/kafka-authorizer.log`
Default log level is INFO, only DENIED logs are available.

To enable success logs, change `/usr/local/kafka/conf/log4j.properties`, set
```properties
log4j.logger.kafka.authorizer.logger=DEBUG
```



