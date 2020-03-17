#!/bin/bash

SSL_FILES_DIR="ssl_files"

cleanup(){
  if [ -d $SSL_FILES_DIR ]
  then
    rm $SSL_FILES_DIR/*
  else
    mkdir $SSL_FILES_DIR
  fi
}

printf "\nCleaning up existing SSL files...\n"
cleanup


printf "\nCreating a CA...\n"
openssl req -new -newkey rsa:4096 -days 365 -x509 -subj "/CN=Kafka-Security-CA" \
  -keyout $SSL_FILES_DIR/ca-key -out $SSL_FILES_DIR/ca-cert -nodes

printf "\nCreating a keystore for kafka broker...\n"
keytool  -genkey -keystore $SSL_FILES_DIR/kafka.server.keystore.jks -validity 365 \
  -storepass $SRVPASS -keypass $SRVPASS -dname "CN=localhost" -storetype pkcs12

# List keystore
# keytool -list -v -keystore $SSL_FILES_DIR/kafka.server.keystore.jks

printf "\nCreating a CSR from the keystore...\n"
keytool -keystore $SSL_FILES_DIR/kafka.server.keystore.jks -certreq -file $SSL_FILES_DIR/cert-csr \
  -storepass $SRVPASS -keypass $SRVPASS


printf "\nSigning the certificate using the CA...\n"
openssl x509 -req -CA $SSL_FILES_DIR/ca-cert -CAkey $SSL_FILES_DIR/ca-key \
  -in $SSL_FILES_DIR/cert-csr -out $SSL_FILES_DIR/cert-signed \
  -days 365 -CAcreateserial -passin pass:$SRVPASS

printf "\nPrinting the signed certificate...\n"
keytool -printcert -v -file $SSL_FILES_DIR/cert-signed

printf "\nImporting CA certificate into broker keystore...\n"
keytool -keystore $SSL_FILES_DIR/kafka.server.keystore.jks -alias CARoot \
  -import -file $SSL_FILES_DIR/ca-cert -storepass $SRVPASS -keypass $SRVPASS -noprompt

printf "\nImporting signed certificate into broker keystore...\n"
keytool -keystore $SSL_FILES_DIR/kafka.server.keystore.jks  -import -file $SSL_FILES_DIR/cert-signed \
  -storepass $SRVPASS -keypass $SRVPASS -noprompt

printf "\nCreating a truststore for kafka broker...\n"
keytool -keystore $SSL_FILES_DIR/kafka.server.truststore.jks -alias CARoot \
  -import -file $SSL_FILES_DIR/ca-cert -storepass $SRVPASS -keypass $SRVPASS -noprompt

