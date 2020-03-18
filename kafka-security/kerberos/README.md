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

Create kerberos database
```bash
sudo /usr/sbin/kdb5_util create -s -r KAFKA.SECURE -P this-is-unsecure
```

Create admin principal
```bash
sudo kadmin.local -q "add_principal -pw this-is-unsecure admin/admin"
```

Start kerberos services
```bash
sudo systemctl restart krb5kdc

sudo systemctl restart kadmin
```

Create User principal

> kadmin.local -> from within the kerberos server
> kadmin -> remote commands

```bash
sudo kadmin.local -q "add_principal -randkey reader@KAFKA.SECURE"

sudo kadmin.local -q "add_principal -randkey writer@KAFKA.SECURE"

sudo kadmin.local -q "add_principal -randkey admin@KAFKA.SECURE"

sudo kadmin.local -q "add_principal -randkey kafka/<FQDN-kafka-server>@KAFKA.SECURE"
```

Export principals into keytab
```bash
sudo kadmin.local -q "xst -kt /tmp/reader.user.keytab reader@KAFKA.SECURE"

sudo kadmin.local -q "xst -kt /tmp/writer.user.keytab writer@KAFKA.SECURE"

sudo kadmin.local -q "xst -kt /tmp/admin.user.keytab admin@KAFKA.SECURE"

sudo kadmin.local -q "xst -kt /tmp/kafka.service.keytab kafka/73.162.202.55@KAFKA.SECURE"
```

```bash
sudo chmod a+r /tmp/*.keytab
```

```bash
scp -i ~/.ssh/id_rsa_aws.pem centos@ec2-54-153-70-110.us-west-1.compute.amazonaws.com:/tmp/*.keytab /tmp/

chmod 600 /tmp/*.keytab
```


Install kerberos client tools on local laptop and kafka server
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

```bash
sudo kinit -kt /tmp/admin.user.keytab admin

sudo klist
```








