# Kafka Security

Broadly, kafka security gets classified into 3 categories:

## Encryption

#### &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; [SSL encryption](https://github.com/krunalvora/apachekafka101/tree/master/kafka-security/ssl): client-broker, broker-broker and broker-zookeeper
#### &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; End-to-end encryption (implemented outside Kafka on the producer and consumer side)

## Authentication

#### &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; [SSL authentication](https://github.com/krunalvora/apachekafka101/tree/master/kafka-security/ssl)

#### &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; SASL:
    
  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
  [SASL/GSSAPI Kerberos](https://github.com/krunalvora/apachekafka101/tree/master/kafka-security/kerberos)
  
  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
  [SASL/OAUTHBEARER: Leverage OAuth 2.0](https://github.com/krunalvora/apachekafka101/tree/master/kafka-security/oauthbearer)

## Authorization

#### &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; [ACLs](https://github.com/krunalvora/apachekafka101/tree/master/kafka-security/acl): Access Control Lists
