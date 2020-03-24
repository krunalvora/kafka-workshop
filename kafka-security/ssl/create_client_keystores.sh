#!/bin/bash

source constants.sh
USER=$1

rm -rf $CLIENT_KS_DIR
mkdir -p $CLIENT_KS_DIR

printf "\nCreating SSL Auth Client KeyStores for User: $USER\n\n"
keytool  -genkey -keystore $CLIENT_KS_DIR/$USER.client.keystore.jks \
    -validity 365 -storepass $CLIPASS -keypass $CLIPASS \
    -dname "CN=$USER" -alias $USER -keyalg RSA -storetype pkcs12

keytool -keystore $CLIENT_KS_DIR/$USER.client.keystore.jks -certreq \
    -file $CLIENT_KS_DIR/$USER-csr -alias $USER -storepass $CLIPASS -keypass $CLIPASS

# Done by CA
openssl x509 -req -CA $CA_DIR/ca-cert -CAkey $CA_DIR/ca-key \
    -in $CLIENT_KS_DIR/$USER-csr -out $CLIENT_KS_DIR/$USER-cert-signed -days 365 \
    -CAcreateserial -passin pass:$SRVPASS

keytool -keystore $CLIENT_KS_DIR/$USER.client.keystore.jks  -import \
    -file $CA_DIR/ca-cert -alias CARoot \
    -storepass $CLIPASS -keypass $CLIPASS -noprompt

keytool -keystore $CLIENT_KS_DIR/$USER.client.keystore.jks  -import \
    -file $CLIENT_KS_DIR/$USER-cert-signed -alias $USER \
    -storepass $CLIPASS -keypass $CLIPASS -noprompt

keytool -keystore $CLIENT_KS_DIR/kafka.client.truststore.jks -alias CARoot \
    -import -file $CA_DIR/ca-cert -storepass $CLIPASS -keypass $CLIPASS -noprompt

printf "\nImported signed certificate into $USER.client.keystore.jks. Done.\n\n"
