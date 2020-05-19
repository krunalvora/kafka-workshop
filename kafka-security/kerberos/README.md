# SASL/Kerberos Authentication

## Table of Contents
1. [Setup Kerberos Server](#setup-kerberos-server)
	1. [Install Kerberos Server](#install-kerberos-server)
	2. [Kerberos Configuration](#kerberos-configuration)
	3. [Kerberos Database](#kerberos-database)
	4. [Kerberos Admin Principal](#kerberos-admin-principal)
	5. [Start Kerberos Services](#start-kerberos-services)
2. [Kerberos Principals and Keytabs](#kerberos-principals-and-keytabs)
	1. [Kerberos User Principals](#kerberos-user-principals)
	2. [Kerberos Keytabs](#kerberos-keytabs)
8. [Kerberos Client Tools](#kerberos-client-tools)
9. [Kafka Server Configuration](#kafka-server-configuration)
	1. [Kafka Server JAAS Configuration](#kafka-server-jaas-configuration)
	2. [Kafka Server SASL/Kerberos Configuration](#kafka-server-saslkerberos-configuration)
10. [Kafka Client Configuration](#kafka-client-configuration)
	1. [Kafka Client JAAS Configuration](#kafka-client-jaas-configuration)
	2. [Kafka Client SASL/Kerberos Properties](#kafka-client-saslkerberos-properties)
14. [Console Producer/Consumer with SASL/Kerberos](#console-producerconsumer-with-saslkerberos)
15. [Changes on Service side for Kerberos](#changes-on-service-side-for-kerberos)


## Setup Kerberos Server

### Install Kerberos Server
```bash
# centos

sudo yum install -y krb5-server
```

### Kerberos Configuration

#### kdc.conf

`/var/kerberos/krb5kdc/kdc.conf`
```properties
[kdcdefaults]
  kdc_ports = 88
  kdc_tcp_ports = 88
  default_realm=KAFKA.SECURE
[realms]
  KAFKA.SECURE = {
    acl_file = /var/kerberos/krb5kdc/kadm5.acl
    dict_file = /usr/share/dict/words
    admin_keytab = /var/kerberos/krb5kdc/kadm5.keytab
    supported_enctypes = aes256-cts:normal aes128-cts:normal des3-hmac-sha1:normal arcfour-hmac:normal camellia256-cts:normal camellia128-cts:normal des-hmac-sha1:normal des-cbc-md5:normal des-cbc-crc:normal
  }
```

#### kadm5.acl
`/var/kerberos/krb5kdc/kadm5.acl`
```
*/admin@KAFKA.SECURE *

```

#### krb5.conf
`/etc/krb5.conf`
```
[logging]
  default = FILE:/var/log/krb5libs.log
  kdc = FILE:/var/log/krb5kdc.log
  admin_server = FILE:/var/log/kadmind.log

[libdefaults]
    default_realm = KAFKA.SECURE
    kdc_timesync = 1
    ticket_lifetime = 24h

[realms]
    KAFKA.SECURE = {
      admin_server = <<KERBEROS-SERVER-PUBLIC-DNS>>
      kdc  = <<KERBEROS-SERVER-PUBLIC-DNS>>
      }

```

### Kerberos Database
```bash
sudo /usr/sbin/kdb5_util create -s -r KAFKA.SECURE -P this-is-unsecure
```

### Kerberos Admin Principal

```bash
sudo kadmin.local -q "add_principal -pw this-is-unsecure admin/admin"
```

### Start Kerberos Services
```bash
sudo systemctl restart krb5kdc

sudo systemctl restart kadmin
```

## Kerberos Principals and Keytabs

### Kerberos User Principals

> kadmin.local -> from within the kerberos server
> kadmin -> remote commands

```bash
sudo kadmin.local -q "add_principal -randkey reader@KAFKA.SECURE"

sudo kadmin.local -q "add_principal -randkey writer@KAFKA.SECURE"

sudo kadmin.local -q "add_principal -randkey admin@KAFKA.SECURE"
```

> `writer@KAFKA.SECURE` means that `writer` user is allowed from any host. To restrict to a specific host, use `writer/<HOST>@KAFKA.SECURE`


Create kafka principal for every single broker in the kafka cluster
```bash
sudo kadmin.local -q "add_principal -randkey kafka/<KAFKA_BROKER_PUBLIC_DNS>@KAFKA.SECURE"
```

#### List Principals

```bash
sudo kadmin.local list_principals
```

### Kerberos Keytabs
```bash
sudo kadmin.local -q "xst -kt /tmp/reader.user.keytab reader@KAFKA.SECURE"

sudo kadmin.local -q "xst -kt /tmp/writer.user.keytab writer@KAFKA.SECURE"

sudo kadmin.local -q "xst -kt /tmp/admin.user.keytab admin@KAFKA.SECURE"

sudo kadmin.local -q "xst -kt /tmp/kafka.service.keytab kafka/<KAFKA_BROKER_PUBLIC_DNS>@KAFKA.SECURE"
```

> In the real world, keytab files would be provided to each user which is supposed to be secured by the user as her identity. It later on can be used for authorization.

```bash
sudo chmod a+r /tmp/*.keytab
```

Secure copy `/tmp/kafka.service.keytab` onto the kafka broker.

Secure copy all the keytab files into local machine.

```bash
chmod 600 /tmp/*.keytab
```


## Kerberos Client Tools 

Install package `krb5-user` on local laptop and kafka server
```bash
export DEBIAN_FRONTEND=noninteractive && sudo apt-get install -y krb5-user
```

Define `/etc/krb5.conf`:
```
[logging]
  default = FILE:/var/log/krb5libs.log
  kdc = FILE:/var/log/krb5kdc.log
  admin_server = FILE:/var/log/kadmind.log

[libdefaults]
    default_realm = KAFKA.SECURE
    kdc_timesync = 1
    ticket_lifetime = 24h

[realms]
    KAFKA.SECURE = {
      admin_server = <<KERBEROS-SERVER-PUBLIC-DNS>>
      kdc  = <<KERBEROS-SERVER-PUBLIC-DNS>>
      }

```

## Kerberos Server Configuration

### Kafka Server JAAS Configuration
`kafka_server_jaas.conf`
```
KafkaServer {
    com.sun.security.auth.module.Krb5LoginModule required
    useKeyTab=true
    storeKey=true
    keyTab="/tmp/kafka.service.keytab"
    principal="kafka/<<KAFKA-SERVER-PUBLIC-DNS>>@KAFKA.SECURE";
};
```

```bash
export "KAFKA_OPTS=-Djava.security.auth.login.config=/home/ubuntu/kafka/config/kafka_server_jaas.conf"
```

OR

Add the environment variable in systemd service file
```
[Unit]
Description=Apache Kafka server (broker)
Documentation=http://kafka.apache.org/documentation.html
Requires=zookeeper.service

[Service]
Type=simple
Environment="KAFKA_OPTS=-Djava.security.auth.login.config=/home/ubuntu/kafka/config/kafka_server_jaas.conf"
ExecStart=/home/ubuntu/kafka/bin/kafka-server-start.sh /home/ubuntu/kafka/config/server.properties
ExecStop=/home/ubuntu/kafka/bin/kafka-server-stop.sh

[Install]
WantedBy=multi-user.target
```


### Kafka Server SASL/Kerberos Configuration
```properties
listeners=PLAINTEXT://0.0.0.0:9092,,SASL_PLAINTEXT://0.0.0.0:9094
advertised.listeners=PLAINTEXT://<<KAFKA-SERVER-PUBLIC-DNS>>:9092,SASL_PLAINTEXT://<<KAFKA-SERVER-PUBLIC-DNS>>:9094
sasl.enabled.mechanisms=GSSAPI
sasl.kerberos.service.name=kafka   # needs to match the kafka principal from kerberos server
```

Restart Kafka Server.


## Kerberos Client Configuration

### Kafka Client JAAS Configuration

`reader.kafka_client_jaas.conf`
```
KafkaClient {
    com.sun.security.auth.module.Krb5LoginModule required
    useKeyTab=true
    storeKey=true
    keyTab="/tmp/reader.user.keytab"
    principal="reader@KAFKA.SECURE";
};
```

```bash
export KAFKA_OPTS="-Djava.security.auth.login.config=<path_to_jaas_conf>/reader.kafka_client_jaas.conf"
```


There is also an option to use the ticket from the Ticket Cache instead of mentioning the keytab and principal as shown above. It is used for development purposes mostly and is not recommended otherwise.
```
KafkaClient {
  com.sun.security.auth.module.Krb5LoginModule required
  useTicketCache=true;
};

```
> This client jaas conf would need a ticket to be put into the Ticket Cache as shown below.

```bash
kdestroy          #Empty the cache

kinit -kt /tmp/reader.user.keytab reader

klist             #Ticket cache
```

### Kafka Client SASL/Kerberos Properties
`kerberos.client.properties`
```properties
security.protocol=SASL_SSL
sasl.kerberos.service.name=kafka
ssl.truststore.location=/home/ubuntu/ssl/kafka.client.truststore.jks
ssl.truststore.password=clientsecret

```

## Console Producer/Consumer with SASL/Kerberos
```bash
kafka-console-producer.sh --broker-list localhost:9094 --topic topic1 --producer.config kerberos.client.properties

kafka-console-consumer.sh --bootstrap-server localhost:9094 --topic topic1 --consumer.config kerberos.client.properties
```

## Changes on Service side for Kerberos

1. Service requests Ops to create a new principal for them and provide a keytab.

2. Ops generates a principal `service@KAFKA.SECURE` and a keytab `service.user.keytab` and provides the keytab back to service. 

        # Done by Ops
        sudo kadmin.local -q "add_principal -randkey service@KAFKA.SECURE"
        sudo kadmin.local -q "xst -kt /tmp/service.user.keytab service@KAFKA.SECURE"
        sudo chmod a+r /tmp/service.user.keytab
	
3. Service defines a `kerberos.client.properties` file

        security.protocol=SASL_PLAINTEXT
        sasl.kerberos.service.name=kafka
		sasl.mechanism=GSSAPI
		sasl.jaas.config=com.sun.security.auth.module.Krb5LoginModule required \
                         keyTab="/tmp/service.user.keytab" \
                         principal="service@KAFKA.SECURE" \
                         useKeyTab="true" \
                         storeKey="true";

4. Service requests Ops to add the principal `User:service` to add Read/Write operation for any topic/group/cluster ACLs.




