#!/bin/bash

SSL_FILES_DIR="ssl_files"
SRVPASS="serversecret"

printf "\nCreating SSL Auth Kafka Server KeyStores\n\n"

## Kafka Broker KeyStore
keytool  -genkey -keystore $SSL_FILES_DIR/kafka.server.keystore.jks -validity 365 \
  -storepass $SRVPASS -keypass $SRVPASS -dname "CN=$KAFKA_SERVER" -keyalg RSA -storetype pkcs12

# keytool -list -v -keystore $SSL_FILES_DIR/kafka.server.keystore.jks

keytool -keystore $SSL_FILES_DIR/kafka.server.keystore.jks -certreq -file $SSL_FILES_DIR/broker-csr \
  -storepass $SRVPASS -keypass $SRVPASS


openssl x509 -req -CA $SSL_FILES_DIR/ca-cert -CAkey $SSL_FILES_DIR/ca-key \
  -in $SSL_FILES_DIR/broker-csr -out $SSL_FILES_DIR/broker-cert-signed \
  -days 365 -CAcreateserial -passin pass:$SRVPASS

# keytool -printcert -v -file $SSL_FILES_DIR/broker-cert-signed

keytool -keystore $SSL_FILES_DIR/kafka.server.keystore.jks -alias CARoot \
  -import -file $SSL_FILES_DIR/ca-cert -storepass $SRVPASS -keypass $SRVPASS -noprompt

keytool -keystore $SSL_FILES_DIR/kafka.server.keystore.jks  -import -file $SSL_FILES_DIR/broker-cert-signed \
  -storepass $SRVPASS -keypass $SRVPASS -noprompt


keytool -keystore $SSL_FILES_DIR/kafka.server.truststore.jks -alias CARoot \
  -import -file $SSL_FILES_DIR/ca-cert -storepass $SRVPASS -keypass $SRVPASS -noprompt

printf "\nImported signed certificate into kafka.server.keystore.jks. Done.\n\n"
printf "Copy the files kafka.server.keystore.jks and kafka.server.truststore.jks over to the Kafka Server.\n\n"

