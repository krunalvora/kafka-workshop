#!/bin/bash

source constants.sh

rm -rf $SERVER_KS_DIR
mkdir -p $SERVER_KS_DIR

printf "\nCreating SSL Auth Kafka Server KeyStores\n\n"

## Kafka Broker KeyStore
## TODO Adding an alias in the first two commands below causes failure in SSL Handshake.
keytool  -genkey -keystore $SERVER_KS_DIR/kafka.server.keystore.jks \
  -dname "CN=$KAFKA_SERVER" -validity 365 \
  -storepass $SRVPASS -keypass $SRVPASS -keyalg RSA -storetype pkcs12

keytool -keystore $SERVER_KS_DIR/kafka.server.keystore.jks -certreq \
  -file $SERVER_KS_DIR/broker-csr \
  -storepass $SRVPASS -keypass $SRVPASS

openssl x509 -req -CA $CA_DIR/ca-cert -CAkey $CA_DIR/ca-key \
  -in $SERVER_KS_DIR/broker-csr -out $SERVER_KS_DIR/broker-cert-signed \
  -days 365 -CAcreateserial -passin pass:$SRVPASS

keytool -keystore $SERVER_KS_DIR/kafka.server.keystore.jks -alias CARoot \
  -import -file $CA_DIR/ca-cert -storepass $SRVPASS -keypass $SRVPASS -noprompt

keytool -keystore $SERVER_KS_DIR/kafka.server.keystore.jks  -import -file $SERVER_KS_DIR/broker-cert-signed \
  -storepass $SRVPASS -keypass $SRVPASS -noprompt


keytool -keystore $SERVER_KS_DIR/kafka.server.truststore.jks -alias CARoot \
  -import -file $CA_DIR/ca-cert -storepass $SRVPASS -keypass $SRVPASS -noprompt

printf "\nImported signed certificate into $SERVER_KS_DIR/kafka.server.keystore.jks. Done.\n\n"
printf "Copy the files kafka.server.keystore.jks and kafka.server.truststore.jks over to the Kafka Server.\n\n"

