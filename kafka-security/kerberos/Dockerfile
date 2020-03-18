FROM centos:centos8

RUN yum install -y vim 
RUN yum install -y krb5-server

COPY kdc.conf /var/kerberos/krb5kdc/
COPY kadm5.acl /var/kerberos/krb5kdc/
COPY krb5.conf /etc/

RUN /usr/sbin/kdb5_util create -s -r KAFKA.SECURE -P this-is-unsecure

RUN kadmin.local -q "add_principal -pw this-is-unsecure admin/admin"

EXPOSE 88

RUN /usr/sbin/krb5kdc start

CMD ["tail", "-f", "/dev/null"]
