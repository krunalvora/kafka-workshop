# SSL Encryption/Authentication

## Table of Contents
1. [Certificate Authority](#certificate-authority)
2. [Kafka Server KeyStore](#kafka-server-keystore)
3. [Kafka Server TrustStore](#kafka-server-truststore)
4. [Kafka Server SSL Properties](#kafka-server-ssl-properties)
5. [Kafka Client TrustStore](#kafka-client-truststore)
6. [Kafka Client SSL Properties](#kafka-client-ssl-properties)
7. [Kafka Client KeyStore for SSL Authentication](#kafka-client-keystore-for-ssl-authentication)
8. [Kafka Client SSL Authentication Properties](#kafka-client-ssl-authentication-properties)
9. [Console Producer/Consumer with SSL Authentication](#console-producerconsumer-with-ssl-authentication)
10. [Quick steps for creating an SSL Auth User](#quick-steps-for-creating-an-ssl-auth-user)

## Certificate Authority
Create a CA
```bash
openssl req -new -newkey rsa:4096 -days 365 -x509 -subj "/CN=Kafka-Security-CA" -keyout ca-key -out ca-cert -nodes

# ca-key -> Public key of the certificate
# ca-cert -> CA certificate
```

## Kafka Broker KeyStore
Export a server password for ease of use
```bash
export SRVPASS=serversecret
```

### Create a Kafka Server keystore
```bash
keytool  -genkey -keystore kafka.server.keystore.jks -validity 365 -storepass $SRVPASS -keypass $SRVPASS -dname "CN=$KAFKA_SERVER" -storetype pkcs12 -keyalg RSA

# kafka.server.keystore.jks -> Kafka Broker keystore file
```

### List keystore
```bash
keytool -list -v -keystore kafka.server.keystore.jks
```

### Get signing request out from server keystore
```bash
keytool -keystore kafka.server.keystore.jks -certreq -file broker-csr -storepass $SRVPASS -keypass $SRVPASS

# broker-csr -> CSR generated
```

### Sign the certificate using CA
```bash
openssl x509 -req -CA ca-cert -CAkey ca-key -in broker-csr -out broker-cert-signed -days 365 -CAcreateserial -passin pass:$SRVPASS

# broker-cert-signed -> signed CA certificate
```

### Print signed certificate
```bash
keytool -printcert -v -file broker-cert-signed
```

### Import CA certificate and signed certificate into server keystore:
```bash
keytool -keystore kafka.server.keystore.jks -alias CARoot -import -file ca-cert -storepass $SRVPASS -keypass $SRVPASS -noprompt

keytool -keystore kafka.server.keystore.jks  -import -file broker-cert-signed -storepass $SRVPASS -keypass $SRVPASS -noprompt
```

## Kafka Server TrustStore
### Create a truststore on kafka server
```bash
keytool -keystore kafka.server.truststore.jks -alias CARoot -import -file ca-cert -storepass $SRVPASS -keypass $SRVPASS -noprompt

# kafka.server.truststore.jks -> truststore jks
```

## Kafka Server SSL Properties 

```properties
listeners=PLAINTEXT://0.0.0.0:9092,SSL://0.0.0.0:9093
advertised.listeners=PLAINTEXT://localhost:9092,SSL://localhost:9093
ssl.keystore.location=<path_to_ssl_files_dir>/kafka.server.keystore.jks
ssl.keystore.password=serversecret
ssl.key.password=serversecret
ssl.truststore.location=<path_to_ssl_files_dir>/kafka.server.truststore.jks
ssl.truststore.password=serversecret

ssl.client.auth=required
ssl.endpoint.identification.algorithm=
```


### Test SSL connection upon restarting Kafka
```bash
openssl s_client -connect localhost:9093
```

## Kafka Client TrustStore
### Create kafka client truststore
```bash
keytool -keystore kafka.client.truststore.jks -alias CARoot -import -file ca-cert -storepass $CLIPASS -keypass $CLIPASS -noprompt
```

## Kafka Client SSL Properties
`client.properties` 
```properties
security.protocol=SSL
ssl.truststore.location=<path_to_ssl_files_dir>/kafka.client.truststore.jks
ssl.truststore.password=clientsecret
```

## Kafka Client KeyStore for SSL Authentication
### Create kafka client keystore
```bash
keytool  -genkey -keystore kafka.client.keystore.jks -validity 365 -storepass $CLIPASS -keypass $CLIPASS -dname "CN=mylaptop" -alias mylaptop -keyalg RSA -storetype pkcs12
```

### Create a CSR from the client keystore
```bash
keytool -keystore kafka.client.keystore.jks -certreq -file client-csr -alias mylaptop -storepass $CLIPASS -keypass $CLIPASS
```

### Sign the client certificate using the CA
```bash
openssl x509 -req -CA ca-cert -CAkey ca-key -in client-csr -out client-cert-signed -days 365 -CAcreateserial -passin pass:$CLIPASS
```

### Import CA certificate into client keystore
```bash
keytool -keystore kafka.client.keystore.jks -alias CARoot -import -file ca-cert -storepass $CLIPASS -keypass $CLIPASS -noprompt
```

### Import signed certificate into client keystore
```bash
keytool -keystore kafka.client.keystore.jks  -import -file client-cert-signed -alias mylaptop -storepass $CLIPASS -keypass $CLIPASS -noprompt
```

## Kafka Client SSL Authentication Properties
`client-ssl-auth.properties` 
```properties
security.protocol=SSL
ssl.truststore.location=<path_to_ssl_files_dir>/kafka.client.truststore.jks
ssl.truststore.password=clientsecret
ssl.keystore.location=<path_to_ssl_files_dir>/kafka.client.keystore.jks
ssl.keystore.password=clientsecret
ssl.key.password=clientsecret
```

### Console Producer/Consumer with SSL Authentication
```bash
kafka-console-producer.sh --broker-list $KAFKA_SERVER:9093 --topic topic1 --producer.config client-ssl-auth.properties

kafka-console-consumer.sh --bootstrap-server $KAFKA_SERVER:9093 --topic topic1 --consumer.config client-ssl-auth.properties
```

### Quick steps for creating an SSL Auth User

To authenticate a new user `bob`, follow these instructions:
```bash
export CLIPASS="clientsecret"

keytool  -genkey -keystore bob.client.keystore.jks -validity 365 -storepass $CLIPASS -keypass $CLIPASS -dname "CN=bob" -alias bob -keyalg RSA -storetype pkcs12

keytool -keystore bob.client.keystore.jks -certreq -file bob-csr -alias bob -storepass $CLIPASS -keypass $CLIPASS

# Done by CA
openssl x509 -req -CA ca-cert -CAkey ca-key -in bob-csr -out bob-cert-signed -days 365 -CAcreateserial -passin pass:$CLIPASS

keytool -keystore bob.client.keystore.jks  -import -file ca-cert -alias CARoot -storepass $CLIPASS -keypass $CLIPASS -noprompt

keytool -keystore bob.client.keystore.jks  -import -file bob-cert-signed -alias bob -storepass $CLIPASS -keypass $CLIPASS -noprompt
```
> `./create_client_keystores.sh bob` automates the above steps.

After the signed certificate for `bob` is imported into `bob.client.keystore.jks`, follow [Console Producer/Consumer with SSL Authentication](#console-producerconsumer-with-ssl-authentication).