#!/bin/bash

./create_ca.sh
./create_server_keystores.sh
./create_client_keystores.sh bob

# Copy ssl.client.properties to /tmp/kafka/ for easy usage in Kakfa producer and consumer commands
cp ssl.client.properties /tmp/kafka
