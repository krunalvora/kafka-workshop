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

Create a Kafka Broker keystore
```bash
keytool  -genkey -keystore kafka.server.keystore.jks -validity 365 -storepass $SRVPASS -keypass $SRVPASS -dname "CN=localhost" -storetype pkcs12 -keyalg RSA

# kafka.server.keystore.jks -> Kafka Broker keystore file
```

List keystore
```bash
keytool -list -v -keystore kafka.server.keystore.jks
```

Get signing request out from keystore
```bash
keytool -keystore kafka.server.keystore.jks -certreq -file cert-csr -storepass $SRVPASS -keypass $SRVPASS

# cert-csr -> CSR generated
```

Sign the certificate
```bash
openssl x509 -req -CA ca-cert -CAkey ca-key -in cert-csr -out cert-signed -days 365 -CAcreateserial -passin pass:$SRVPASS

# cert-signed -> signed CA certificate
```

Print signed certificate
```bash
keytool -printcert -v -file cert-signed
```

Import CA certificate and signed certificate into broker keystore:
```bash
keytool -keystore kafka.server.keystore.jks -alias CARoot -import -file ca-cert -storepass $SRVPASS -keypass $SRVPASS -noprompt

keytool -keystore kafka.server.keystore.jks  -import -file cert-signed -storepass $SRVPASS -keypass $SRVPASS -noprompt
```

## Kafka Broker TrustStore
Create a truststore on kafka broker
```bash
keytool -keystore kafka.server.truststore.jks -alias CARoot -import -file ca-cert -storepass $SRVPASS -keypass $SRVPASS -noprompt

# kafka.server.truststore.jks -> truststore jks
```

## Kafka Broker SSL Properties 
SSL properties in Kafka's `server.properties`
```properties
listeners=PLAINTEXT://0.0.0.0:9092,SSL://0.0.0.0:9093
advertised.listeners=PLAINTEXT://localhost:9092,SSL://localhost:9093
ssl.keystore.location=<path_to_ssl_files_dir>/kafka.server.keystore.jks
ssl.keystore.password=serversecret
ssl.key.password=serversecret
ssl.truststore.location=<path_to_ssl_files_dir>/kafka.server.truststore.jks
ssl.truststore.password=serversecret
```


Test SSL connection upon restarting Kafka
```bash
openssl s_client -connect localhost:9093
```

## Kafka Client TrustStore
Create kafka client truststore
```bash
printf "\nCreating a truststore for kafka client...\n"
keytool -keystore $SSL_FILES_DIR/kafka.client.truststore.jks -alias CARoot -import -file $SSL_FILES_DIR/ca-cert -storepass $CLIPASS -keypass $CLIPASS -noprompt
```

## Kafka Client SSL Properties
Define a `client.properties` 
```properties
security.protocol=SSL
ssl.truststore.location=<path_to_ssl_files_dir>/kafka.client.truststore.jks
ssl.truststore.password=clientsecret
```

