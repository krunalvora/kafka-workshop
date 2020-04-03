#!/bin/bash

source constants.sh
rm -rf $CA_DIR
mkdir -p $CA_DIR

printf "\nCreating Certificate Authority for SSL\n\n"
openssl req -new -newkey rsa:4096 -days 365 -x509 -subj "/CN=Kafka-Security-CA" -keyout $CA_DIR/ca-key -out $CA_DIR/ca-cert -nodes

printf "\nDone. Generated certificates in $CA_DIR.\n\n"
