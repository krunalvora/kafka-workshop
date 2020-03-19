## Kerberos Setup

```bash
# centos

sudo yum install -y krb5-server
```

kdc.conf - `/var/kerberos/krb5kdc/kdc.conf`
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

kadm5.acl - `/var/kerberos/krb5kdc/kadm5.acl`
```
*/admin@KAFKA.SECURE *

```

krb5.conf - `/etc/krb5.conf`
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

### Create kerberos database
```bash
sudo /usr/sbin/kdb5_util create -s -r KAFKA.SECURE -P this-is-unsecure
```

### Create admin principal
```bash
sudo kadmin.local -q "add_principal -pw this-is-unsecure admin/admin"
```

Start kerberos services
```bash
sudo systemctl restart krb5kdc

sudo systemctl restart kadmin
```

### Create User principals

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

### Export principals into keytab
```bash
sudo kadmin.local -q "xst -kt /tmp/reader.user.keytab reader@KAFKA.SECURE"

sudo kadmin.local -q "xst -kt /tmp/writer.user.keytab writer@KAFKA.SECURE"

sudo kadmin.local -q "xst -kt /tmp/admin.user.keytab admin@KAFKA.SECURE"

sudo kadmin.local -q "xst -kt /tmp/kafka.service.keytab kafka/<KAFKA_BROKER_PUBLIC_DNS>@KAFKA.SECURE"
```

> In the real world, keytab files would be provided to each user which is supposed to be secured by the user as her identity. It later on can be used for authorization.

#### View the keytab files 
```bash
sudo klist -kt /tmp/writer.user.keytab
```

```bash
sudo chmod a+r /tmp/*.keytab
```

Secure copy `/tmp/kafka.service.keytab` onto the kafka broker


Secure copy all the keytab files into local machine
```bash

scp -i ~/.ssh/id_rsa_aws.pem centos@ec2-54-153-70-110.us-west-1.compute.amazonaws.com:/tmp/*.keytab /tmp/

chmod 600 /tmp/*.keytab
```


### Install kerberos client tools (`krb5-user`) on local laptop and kafka server
```bash
export DEBIAN_FRONTEND=noninteractive && sudo apt-get install -y krb5-user
```

/etc/krb5.conf
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

Grab a ticket and list the ticket cache
```bash
# kinit -kt <keytab> <principal> to grab a ticket for a principal
sudo kinit -kt /tmp/admin.user.keytab admin

# List the ticket cache
sudo klist
```


### Kafka Server Configuration for SASL/GSSAPI
```properties
listeners=PLAINTEXT://0.0.0.0:9092,SSL://0.0.0.0:9093,SASL_SSL://0.0.0.0:9094
advertised.listeners=PLAINTEXT://<<KAFKA-SERVER-PUBLIC-DNS>>:9092,SSL://<<KAFKA-SERVER-PUBLIC-DNS>>:9093,SASL_SSL://<<KAFKA-SERVER-PUBLIC-DNS>>:9094

sasl.enabled.mechanisms=GSSAPI
sasl.kerberos.service.name=kafka # needs to match the kafka principal from kerberos server
```

### kafka_server_jaas.conf
```
KafkaServer {
    com.sun.security.auth.module.Krb5LoginModule required
    useKeyTab=true
    storeKey=true
    keyTab="/tmp/kafka.service.keytab"
    principal="kafka/<<KAFKA-SERVER-PUBLIC-DNS>>@KAFKA.SECURE";
};
```

### SASL configuration environment variable
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

```bash
sudo systemctl daemon-reload

sudo systemctl restart kafka
```


### Kafka Client Configuration for SASL/GSSAPI

Create jaas file for client `/tmp/kafka_client_jaas.conf`
```
KafkaClient {
 com.sun.security.auth.module.Krb5LoginModule required
 useTicketCache=true;
 };

```

`/tmp/kafka_client_kerberos.properties`
```properties
security.protocol=SASL_SSL
sasl.kerberos.service.name=kafka
ssl.truststore.location=/home/ubuntu/ssl/kafka.client.truststore.jks
ssl.truststore.password=clientpass

```















