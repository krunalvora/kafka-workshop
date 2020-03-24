#!/bin/bash

USER=$1
CA_DIR="/tmp/ca"
KS_DIR="/tmp/client_keystores"
PASS="clientsecret"

rm -rf $KS_DIR
mkdir -p $KS_DIR

printf "\nCreating SSL Auth Client KeyStores for User: $USER\n\n"
keytool  -genkey -keystore $KS_DIR/$USER.client.keystore.jks \
    -validity 365 -storepass $PASS -keypass $PASS \
    -dname "CN=$USER" -alias $USER -keyalg RSA -storetype pkcs12

keytool -keystore $KS_DIR/$USER.client.keystore.jks -certreq \
    -file $KS_DIR/$USER-csr -alias $USER -storepass $PASS -keypass $PASS

# Done by CA
export SRVPASS=serversecret
openssl x509 -req -CA $CA_DIR/ca-cert -CAkey $CA_DIR/ca-key \
    -in $KS_DIR/$USER-csr -out $KS_DIR/$USER-cert-signed -days 365 \
    -CAcreateserial -passin pass:$PASS

keytool -keystore $KS_DIR/$USER.client.keystore.jks  -import \
    -file $CA_DIR/ca-cert -alias CARoot \
    -storepass $PASS -keypass $PASS -noprompt

keytool -keystore $KS_DIR/$USER.client.keystore.jks  -import \
    -file $KS_DIR/$USER-cert-signed -alias $USER \
    -storepass $PASS -keypass $PASS -noprompt

keytool -keystore $KS_DIR/kafka.client.truststore.jks -alias CARoot \
    -import -file $CA_DIR/ca-cert -storepass $PASS -keypass $PASS -noprompt

printf "\nImported signed certificate into $USER.client.keystore.jks. Done.\n\n"
