#!/bin/bash

CA_DIR="/tmp/ca"
KS_DIR="/tmp/server_keystores"
PASS="serversecret"
KAFKA_SERVER="localhost"

mkdir -p $KS_DIR

printf "\nCreating SSL Auth Kafka Server KeyStores\n\n"

## Kafka Broker KeyStore

keytool  -genkey -keystore $KS_DIR/kafka.server.keystore.jks \
  -dname "CN=$KAFKA_SERVER" -validity 365 \
  -storepass $SRVPASS -keypass $SRVPASS -keyalg RSA -storetype pkcs12

keytool -keystore $KS_DIR/kafka.server.keystore.jks -certreq -file $KS_DIR/broker-csr \
  -storepass $SRVPASS -keypass $SRVPASS

openssl x509 -req -CA $CA_DIR/ca-cert -CAkey $CA_DIR/ca-key \
  -in $KS_DIR/broker-csr -out $KS_DIR/broker-cert-signed \
  -days 365 -CAcreateserial -passin pass:$SRVPASS

keytool -keystore $KS_DIR/kafka.server.keystore.jks -alias CARoot \
  -import -file $CA_DIR/ca-cert -storepass $SRVPASS -keypass $SRVPASS -noprompt

keytool -keystore $KS_DIR/kafka.server.keystore.jks  -import -file $KS_DIR/broker-cert-signed \
  -storepass $SRVPASS -keypass $SRVPASS -noprompt


keytool -keystore $KS_DIR/kafka.server.truststore.jks -alias CARoot \
  -import -file $CA_DIR/ca-cert -storepass $SRVPASS -keypass $SRVPASS -noprompt

printf "\nImported signed certificate into kafka.server.keystore.jks. Done.\n\n"
printf "Copy the files kafka.server.keystore.jks and kafka.server.truststore.jks over to the Kafka Server.\n\n"

