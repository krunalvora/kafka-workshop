#!/bin/bash

SSL_FILES_DIR="ssl_files"
export SRVPASS="serversecret"
export CLIPASS="clientsecret"

cleanup(){
  if [ -d $SSL_FILES_DIR ]
  then
    rm $SSL_FILES_DIR/*
  else
    mkdir $SSL_FILES_DIR
  fi
}

printf "\n### Cleaning up existing SSL files...\n"
cleanup

## CA
printf "\n### Creating a CA...\n"
openssl req -new -newkey rsa:4096 -days 365 -x509 -subj "/CN=Kafka-Security-CA" \
  -keyout $SSL_FILES_DIR/ca-key -out $SSL_FILES_DIR/ca-cert -nodes


## Kafka Broker KeyStore
printf "\n### Creating a keystore for kafka broker...\n"
keytool  -genkey -keystore $SSL_FILES_DIR/kafka.server.keystore.jks -validity 365 \
  -storepass $SRVPASS -keypass $SRVPASS -dname "CN=localhost" -keyalg RSA -storetype pkcs12

# printf "\n### List keystore...\n"
# keytool -list -v -keystore $SSL_FILES_DIR/kafka.server.keystore.jks

printf "\n### Creating a CSR from the broker keystore...\n"
keytool -keystore $SSL_FILES_DIR/kafka.server.keystore.jks -certreq -file $SSL_FILES_DIR/broker-csr \
  -storepass $SRVPASS -keypass $SRVPASS


printf "\n### Signing the broker certificate using the CA...\n"
openssl x509 -req -CA $SSL_FILES_DIR/ca-cert -CAkey $SSL_FILES_DIR/ca-key \
  -in $SSL_FILES_DIR/broker-csr -out $SSL_FILES_DIR/broker-cert-signed \
  -days 365 -CAcreateserial -passin pass:$SRVPASS

# printf "\n### Printing the signed certificate...\n"
# keytool -printcert -v -file $SSL_FILES_DIR/broker-cert-signed

printf "\n### Importing CA certificate into broker keystore...\n"
keytool -keystore $SSL_FILES_DIR/kafka.server.keystore.jks -alias CARoot \
  -import -file $SSL_FILES_DIR/ca-cert -storepass $SRVPASS -keypass $SRVPASS -noprompt

printf "\n### Importing signed certificate into broker keystore...\n"
keytool -keystore $SSL_FILES_DIR/kafka.server.keystore.jks  -import -file $SSL_FILES_DIR/broker-cert-signed \
  -storepass $SRVPASS -keypass $SRVPASS -noprompt


## Kafka Broker TrustStore
printf "\n### Creating a truststore for kafka broker...\n"
keytool -keystore $SSL_FILES_DIR/kafka.server.truststore.jks -alias CARoot \
  -import -file $SSL_FILES_DIR/ca-cert -storepass $SRVPASS -keypass $SRVPASS -noprompt


## Kafka Client TrustStore
printf "\n### Creating a truststore for kafka client...\n"
keytool -keystore $SSL_FILES_DIR/kafka.client.truststore.jks -alias CARoot \
  -import -file $SSL_FILES_DIR/ca-cert -storepass $CLIPASS -keypass $CLIPASS -noprompt


## Kafka Client KeyStore
printf "\n### Creating a keystore for kafka clients...\n"
keytool  -genkey -keystore $SSL_FILES_DIR/kafka.client.keystore.jks -validity 365 \
  -storepass $CLIPASS -keypass $CLIPASS -dname "CN=mylaptop" -alias mylaptop \
  -keyalg RSA -storetype pkcs12


printf "\n### Creating a CSR from the client keystore...\n"
keytool -keystore $SSL_FILES_DIR/kafka.client.keystore.jks -certreq -file $SSL_FILES_DIR/client-csr \
  -alias mylaptop -storepass $CLIPASS -keypass $CLIPASS


printf "\n### Signing the client certificate using the CA...\n"
openssl x509 -req -CA $SSL_FILES_DIR/ca-cert -CAkey $SSL_FILES_DIR/ca-key \
  -in $SSL_FILES_DIR/client-csr -out $SSL_FILES_DIR/client-cert-signed \
  -days 365 -CAcreateserial -passin pass:$CLIPASS

printf "\n### Importing CA certificate into client keystore...\n"
keytool -keystore $SSL_FILES_DIR/kafka.client.keystore.jks -alias CARoot \
  -import -file $SSL_FILES_DIR/ca-cert -storepass $CLIPASS -keypass $CLIPASS -noprompt

printf "\n### Importing signed certificate into client keystore...\n"
keytool -keystore $SSL_FILES_DIR/kafka.client.keystore.jks  -import -file $SSL_FILES_DIR/client-cert-signed \
  -alias mylaptop -storepass $CLIPASS -keypass $CLIPASS -noprompt

