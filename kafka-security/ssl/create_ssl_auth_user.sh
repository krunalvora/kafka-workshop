#!/bin/bash

USER=$1
SSL_FILES_DIR="ssl_files"
CLIPASS="clientsecret"


printf "\nCreating SSL Auth User: $USER\n\n"
keytool  -genkey -keystore $SSL_FILES_DIR/$USER.client.keystore.jks \
    -validity 365 -storepass $CLIPASS -keypass $CLIPASS \
    -dname "CN=$USER" -alias $USER -keyalg RSA -storetype pkcs12

keytool -keystore $SSL_FILES_DIR/$USER.client.keystore.jks -certreq \
    -file $SSL_FILES_DIR/$USER-csr -alias $USER -storepass $CLIPASS -keypass $CLIPASS

# Done by CA
openssl x509 -req -CA $SSL_FILES_DIR/ca-cert -CAkey $SSL_FILES_DIR/ca-key \
    -in $SSL_FILES_DIR/$USER-csr -out $SSL_FILES_DIR/$USER-cert-signed -days 365 \
    -CAcreateserial -passin pass:$CLIPASS

keytool -keystore $SSL_FILES_DIR/$USER.client.keystore.jks  -import \
    -file $SSL_FILES_DIR/ca-cert -alias CARoot \
    -storepass $CLIPASS -keypass $CLIPASS -noprompt

keytool -keystore $SSL_FILES_DIR/$USER.client.keystore.jks  -import \
    -file $SSL_FILES_DIR/$USER-cert-signed -alias $USER \
    -storepass $CLIPASS -keypass $CLIPASS -noprompt

printf "\nImported signed certificate into $USER.client.keystore.jks. Done.\n\n"
