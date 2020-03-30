# SASL/OAUTHBEARER

## Table of Contents
1. [Setting up Okta OAuth Server](#setting-up-okta-oauth-server)
2. [Kafka OAuth2 JAR](#kafka-oauth2-jar)
3. [Kafka Server JAAS configuration](#kafka-server-jaas-configuration)
4. [Kafka Server SASL/OAUTHBEARER properties](#kafka-server-sasloauthbearer-properties)
5. [Kafka Server KAFKA_OPTS](#kafka-server-kafkaopts)
6. [Kafka Client SASL/OAUTHBEARER properties](#kafka-client-sasloauthbearer-properties)
7. [Console Producer/Consumer with SASL/OAUTHBEARER](#console-producerconsumer-with-sasloauthbearer)
8. [Quick steps for creating a SASL/OAUTHBEARER Application](#quick-steps-for-creating-a-sasloauthbearer-application)


For a detailed step-by-step description, this [medium article](https://medium.com/egen/how-to-configure-oauth2-authentication-for-apache-kafka-cluster-using-okta-8c60d4a85b43) does a great job.

## Setting up Okta OAuth Server

1. Set up a new free developer account for about 1000 requests at [developer.okta.com](https://developer.okta.com).

2. Verify that a `default` Authorization Server is created under API / Authorization Server menu.

3. Add a scope for the default Authorization Server:

        Name: kafka
        Description: kafka scope description
        Check Include in public metadata
        Leave Set as default scope unchecked

4. From the `Applications` menu, create a new application `kafkabroker`:

        Add Application
        Select Platform as service(Machine to Machine)
        Name: kafkabroker

5. Repeat the above steps for adding new applications `kafkaproducerapp` and `kafkaconsumerapp`.

6. Test whether we can retrieve token back from the Okta OAuth Server:

        curl -i -H 'Content-Type: application/x-www-form-urlencoded' -X POST 'https://<auth-server-url>/oauth2/default/v1/token' -d 'grant_type=client_credentials&scope=kafka' -H 'Authorization: Basic <encoded-clientId:clientsecret>'


## Kafka OAuth2 JAR

1. Implement the classes to connect with external OAuth2 Server to generate tokens, introspect/validate tokens and renew tokens for Kafka cluster brokers and clients.

        OAuthAuthenticateLoginCallbackHandler
                
        OAuthAuthenticateValidatorCallbackHandler
        
Both the classes implement org.apache.kafka.common.security.auth.AuthenticateCallbackHandler to configure and handle call back events.

OAuthAuthenticateLoginCallbackHandler is used by clients and brokers to authenticate and to generate token using OAuth server.

OAuthAuthenticateValidatorCallbackHandler is used by clients and brokers to make the validation of the generated token using OAuth token introspection.

> To generate an example JAR: clone [kafka-oauth2](https://github.com/vishwavangari/kafka-oauth2) github repo and run `.gradlew clean build`. Make sure kafka-oauth2–0.0.1.jar is generated at folder <oauth2-repo-dir>/kafka-oauth2/build/libs/.

2. Add kafka-oauth2 JAR to Kafka classpath

        cp <oauth2-project-dir>/build/libs/kafka-oauth2–0.0.1.jar <kafka-binary-dir>/libs



## Kafka Server JAAS configuration
`oauth.kafka_server_jaas.conf`
```
KafkaServer {
  org.apache.kafka.common.security.oauthbearer.OAuthBearerLoginModule required
  LoginStringClaim_sub="<brokerapp-clientId>";
};
```


## Kafka Server SASL/OAUTHBEARER properties
`oauth.server.properties`

        ##########SECURITY using OAUTHBEARER authentication ###############
        sasl.enabled.mechanisms=OAUTHBEARER
        sasl.mechanism.inter.broker.protocol=OAUTHBEARER
        security.inter.broker.protocol=SASL_PLAINTEXT
        listeners=SASL_PLAINTEXT://localhost:9094
        advertised.listeners=SASL_PLAINTEXT://localhost:9094
        #Authorizer for ACL
        authorizer.class.name=kafka.security.auth.SimpleAclAuthorizer
        super.users=User:<brokerapp-clientId>;
        ################ OAuth Classes #####################
        sasl.jaas.config=org.apache.kafka.common.security.oauthbearer.OAuthBearerLoginModule required OAUTH_LOGIN_SERVER=<auth-server-url> OAUTH_LOGIN_ENDPOINT='/oauth2/default/v1/token' OAUTH_LOGIN_GRANT_TYPE=client_credentials OAUTH_LOGIN_SCOPE=broker.kafka OAUTH_AUTHORIZATION='Basic <encoded-clientId:clientsecret>' OAUTH_INTROSPECT_SERVER=<auth-server-url> OAUTH_INTROSPECT_ENDPOINT='/oauth2/default/v1/introspect' OAUTH_INTROSPECT_AUTHORIZATION='Basic <encoded-clientId:clientsecret>';
        listener.name.sasl_plaintext.oauthbearer.sasl.login.callback.handler.class=com.oauth2.security.oauthbearer.OAuthAuthenticateLoginCallbackHandler
        listener.name.sasl_plaintext.oauthbearer.sasl.server.callback.handler.class=com.oauth2.security.oauthbearer.OAuthAuthenticateValidatorCallbackHandler
        ########## SECURITY using OAUTHBEARER authentication ###############


## Kafka Server KAFKA_OPTS

```bash
export OAUTH_SERVER_URL=<oauth-server-url>

export OAUTH_AUTHORIZATION=<base64 encoded clientId:clientSecret>

export KAFKA_OPTS="-Djava.security.auth.login.config=/usr/local/kafka/config/oauth.kafka_server_jaas.conf -DOAUTH_WITH_SSL=true -DOAUTH_LOGIN_SERVER=$OAUTH_SERVER_URL -DOAUTH_LOGIN_ENDPOINT=/oauth2/default/v1/token -DOAUTH_LOGIN_GRANT_TYPE=client_credentials -DOAUTH_LOGIN_SCOPE=kafka -DOAUTH_INTROSPECT_SERVER=$OAUTH_SERVER_URL -DOAUTH_INTROSPECT_ENDPOINT=/oauth2/default/v1/introspect -DOAUTH_AUTHORIZATION=Basic%20$OAUTH_AUTHORIZATION -DOAUTH_INTROSPECT_AUTHORIZATION=Basic%20$OAUTH_AUTHORIZATION"
```

Start kafka server with `oauth.server.properties`.

Verify the kafka logs for `OAuthAuthenticateLoginCallbackHandler` and `OAuthAuthenticateValidatorCallbackHandler`.


## Kafka Client SASL/OAUTHBEARER properties

`oauth.client.properties`

```properties
security.protocol=SASL_PLAINTEXT
sasl.mechanism=OAUTHBEARER
sasl.login.callback.handler.class=com.oauth2.security.oauthbearer.OAuthAuthenticateLoginCallbackHandler
sasl.jaas.config=org.apache.kafka.common.security.oauthbearer.OAuthBearerLoginModule required OAUTH_LOGIN_SERVER=<OAuth-server-url> OAUTH_LOGIN_ENDPOINT='/oauth2/default/v1/token' OAUTH_LOGIN_GRANT_TYPE=client_credentials OAUTH_LOGIN_SCOPE=kafka OAUTH_AUTHORIZATION='Basic <encoded-producer-clientId:clientsecret>' OAUTH_INTROSPECT_SERVER=<OAuth-server-url> OAUTH_INTROSPECT_ENDPOINT='/oauth2/default/v1/introspect' OAUTH_INTROSPECT_AUTHORIZATION='Basic <encoded-producer-clientId:clientsecret>';
```

### Console Producer/Consumer with SASL/OAUTHBEARER
```bash
kafka-console-producer.sh --broker-list localhost:9094 --topic topic1 --producer.config oauth.client.properties

kafka-console-consumer.sh --bootstrap-server localhost:9094 --topic topic1 --consumer.config oauth.client.properties
```



### Quick steps for creating a SASL/OAUTHBEARER Application

1. Service `login` requests the Okta Service to create a new application and provide back a clientID and client secret.

2. `login` service user defines a `client_oauthbearer.properties` file.

        security.protocol=SASL_PLAINTEXT
        sasl.mechanism=OAUTHBEARER
        sasl.login.callback.handler.class=com.oauth2.security.oauthbearer.OAuthAuthenticateLoginCallbackHandler
        sasl.jaas.config=org.apache.kafka.common.security.oauthbearer.OAuthBearerLoginModule required OAUTH_LOGIN_SERVER=<OAuth-server-url> OAUTH_LOGIN_ENDPOINT='/oauth2/default/v1/token' OAUTH_LOGIN_GRANT_TYPE=client_credentials OAUTH_LOGIN_SCOPE=kafka OAUTH_AUTHORIZATION='Basic <encoded-producer-clientId:clientsecret>' OAUTH_INTROSPECT_SERVER=<OAuth-server-url> OAUTH_INTROSPECT_ENDPOINT='/oauth2/default/v1/introspect' OAUTH_INTROSPECT_AUTHORIZATION='Basic <encoded-producer-clientId:clientsecret>';


3. `login` service member requests the Kafka Ops to add the principal `User:<client-id>` to add Read/Write operation for any topic/group/cluster ACLs.



`login` service members can now follow [Console Producer/Consumer with SASL/Kerberos](#console-producerconsumer-with-sasloauthbearer).




