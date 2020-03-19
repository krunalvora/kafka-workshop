# Kafka Security

Broadly, kafka security gets classified into 3 categories:

## Encryption

#### [SSL encryption](https://github.com/krunalvora/apachekafka101/tree/master/kafka-security/ssl): client-broker, broker-broker and broker-zookeeper
#### End-to-end encryption (implemented outside Kafka on the producer and consumer side)

## Authentication

#### [SSL authentication](https://github.com/krunalvora/apachekafka101/tree/master/kafka-security/ssl)

#### SASL:
  
  SASL/PLAIN: Passwords hardcoded in the broker
  
  SASL/SCRAM: Passwords in zookeeper
  
  [SASL/GSSAPI Kerberos](https://github.com/krunalvora/apachekafka101/tree/master/kafka-security/kerberos)
  
  SASL/OAUTHBEARER: Leverage OAuth 2.0  

## Authorization

#### [ACLs](https://github.com/krunalvora/apachekafka101/tree/master/kafka-security/acl)
