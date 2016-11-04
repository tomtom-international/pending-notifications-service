# Read Me of the Pending Notification Service

[![Build Status](https://img.shields.io/travis/tomtom-international/notification-service.svg?maxAge=3600)](https://travis-ci.org/tomtom-international/notification-service)
[![Coverage Status](https://coveralls.io/repos/github/tomtom-international/notification-service/badge.svg?branch=master&maxAge=3600)](https://coveralls.io/github/tomtom-international/notification-service?branch=master)
[![License](http://img.shields.io/badge/license-APACHE2-blue.svg)]()
[![Release](https://img.shields.io/github/release/tomtom-international/notificationn-service.svg?maxAge=3600)](https://github.com/tomtom-international/notification-service/releases)

## Introduction

The *Pending Notification Service* offers a way to provide notifications for devices, which are retrieved in a polling
fashion using a REST API, rather than a push channel.

A 'pending notification' is a message of some sort, waiting to be seen/retrieved by a device. As opposed to 'push notifications',
which are pro-actively pushed by back-end server to a device. These pending notifications can be used instead of push notifications
if a push channel is not available.

The service offers connected devices a way to figure out, in a very data usage friendly manner, whether it needs to
contact a back-end service in a secure way. Contrary to common push notification solutions, the pending notifications in this service do not
contain any useful information themselves, other than redirecting the device to contact a specific service.

Contacting the actual service to retrieve the information is not part of the pending notifications service itself.
In general, this is a secured call to retrieve pending messages, fetch new firmware, get a new software
configuration, or anything else.

The notification service is, like most push notification services, a very generic service. It tries to
accommodate a wide range of use case, two of which are considered very common:

 1. A *single* back-end service wishes to notify devices to contact them in a secure way to retrieve more
 information.

 2. *Multiple* back-end services, independently of each other, wish to notify devices.


## The Pending Notifications API

The full set of API methods offered by the Notification Service is split into unsecured HTTP calls and secured
HTTPS calls.

Externally available, unsecured HTTP calls:

    GET    /pending/notifications/{deviceId}             -- get notification(s) for a specific ID (returns 200 or 404)
                                                            this returns a list of services with pending notifications
Internally available, possibly secured HTTPS calls:

    POST   /pending/notifications/{deviceId}             -- create pending notifications, for a specific device
    POST   /pending/notifications/{deviceId}/{serviceId} -- ibid, for a specific service
    DELETE /pending/notifications/{deviceId}             -- delete all notifications for a specific device
    DELETE /pending/notifications/{deviceId}/{serviceId} -- ibid, but only for 1 service at a time

    GET    /pending/notifications[?offset={x}&count={y}] -- get all IDs that have pending notifications

And then there are some helper methods, for development and monitoring:

    GET    /pending                               -- produces this help text
    GET    /pending/version                       -- returns the service version
    GET    /pending/status                        -- returns 204 if all OK

The distinction between HTTP and HTTPS calls should be provided during deployment as a configuration of the
router/firewall in front of the application server. It is not part of this source code.

The use of the API methods is explained in more details below, in the usage scenarios, but below is a short
description of the calls.


### Short Description of the API

The service is extremely simple. It allows a device to check if there is something at the server worthwhile
contacting the server for. The service only tells the device "yes, there is something for you" or
"no, don't bother checking". This is achieved by providing an unauthenticated HTTP (not HTTPS!)
REST API `GET /pending/notifications/{id}`, which returns either HTTP status code `200 OK` or
`404 NOT FOUND`. The provided `{id}` is the ID of the device which you wish to check the pending notifications for.

Status code `200 OK` indicates there is indeed something on the server waiting for the device
to be picked up. The device should in that case contact the server via its regular, authenticated and
authorized (possibly HTTPS, TLS or otherwise secured) channel.

Status code `404 NOT FOUND` indicates there is nothing for the device on the server of interest.
The device can then go back to sleep and return to the server after its predetermined interval.

The service does not provide ANY other information than this. In particular, in should not be extended
with features like providing a new sleep interval or such, as the interface is not secure and the
device is not allowed to trust anything from this interface.


## Scenario 1: A Single Back-End System Provides Notifications for Devices

This scenario describes a system `S` which wants to be able to notify devices there is something waiting for them at `S`.
If there is something waiting for devices, they will contact `S` in a secure manner to fetch the information. (Which is
not part of the pending notifications service.) The system will then remove its pending notification for the device.

The only API calls used in this scenario, are:

    GET     /pending/notifications/{deviceId}                (called by device D)

    POST    /pending/notifications/{deviceId}/{serviceId}    (called by system S)
    DELETE  /pending/notifications/{deviceId}/{serviceId}    (called by system S)

1. System `S` wants to notify device `D` it needs to contact `S` in a secure way for more info. It calls:

        POST /pending/notifications/D

2. Device `D` want to check if there’s a pending notification. It calls:

        GET /pending/notifications/D

    The returned status code will be 200 (message body can be ignored).

3. Device `D` contacts system `S` in a secure way. Had the result status not been 200, the device would have gone
back to sleep and retry later.

4. When device `D` contacts system `S` for more info, `S` removes the notification for `D` by calling:

        DELETE /pending/notifications/D

    This ensures subsequent calls to `GET /pending/notifications/D` will return 404.


### Sequence Diagram for Scenario 1

    Device D                              Wake-Up Service
    ----+---                              ------+--------
        |                                       |
        :                                       :
    Scenario: Nothing is waiting for D          :
    =========                                   :
        |                                       |
        | HTTP GET /pending/notifications/D              |   (Note the use of HTTP)
        |-------------------------------------->|-+
        |                         404 NOT FOUND | |
        |<--------------------------------------|-+
        | (device goes back to sleep)           |
        :                                       :
    Scenario: Something is waiting for D        :
    =========                                   :
        |                                       |
        | HTTP GET /pending/notifications/D              |   (Note the use of HTTP)
        |-------------------------------------->|-+
        |                                200 OK |
        |<--------------------------------------|-+
        |                                       |
        | (device initiates secure retrieval)   |
        | HTTPS GET /someSystem/...             |   (Note the use of HTTPS)
        |-------------------------------------->|-+
        :                                       :
        :                                       :


## Scenario 2: Multiple Back-End Systems Provide Notifications for Devices

This scenario describes systems `S` and `T` which want to be able to notify devices there is something waiting for them.
If there is something waiting for a device, it must contact the appropriate system in a secure way. The system will then
remove its pending notification for the device.

The API calls used in this scenario, are:

    GET     /pending/notifications/{deviceId}                (called by device D)

    POST    /pending/notifications/{deviceId}/{serviceId}    (called by system S and T)
    DELETE  /pending/notifications/{deviceId}/{serviceId}    (called by system S and T)
    DELETE  /pending/notifications/{deviceId}                (not strictly needed, but may be used to reset all calls for D)

1. System `S` wants to notify device `D` it needs to contact `S` in a secure way for more info. It calls:

        POST /pending/notifications/D/S      (note the append "/S", as compared to scenario 1)

2. System `T` also wants to notify device `D`. It calls:

        POST /pending/notifications/D/T

3. Device `D` want to check if there’s a pending notification. It calls:

        GET /pending/notifications/D

    The returned status code will be 200 and the body contains: `["S", "T"]`

3. Device `D` now contacts services listed (`S` and `T`) in a secure way. Had the return status not been 200, the device
would have gone back to sleep and retry later.

4. When device `D` contacts `S` for more info, `S` removes the notification by calling:

        DELETE /pending/notifications/D/S

    The same applies for `T`, which also removes its notification by calling:

        DELETE /pending/notifications/D/T

    At this point, there are no waiting notifications anymore, so subsequent calls to `GET /pending/notifications/D` will
    return 404.

5. Should a system wish to 'reset' all pending notifications for a device, then a single `DELETE` call suffices:

            DELETE /pending/notifications/D/T

    This removes all notifications for all services for device `D`.


### General Remark and Word of Caution

In the above scneario, the final call to `DELETE /pending/notifications/D/T` effectively removed the last pending
notification for device `D`, causing `GET /pending/notifications/D` to return 404 subsequently.

However, it would be possible that, although not advised, another back-end system had called `POST /pending/notifications/D` previously,
*without* specifying a service identification (effectively using an API call you would use in scenarion 1 only).

In that case, the "serviceless" notification would still be pending, preventing the service from returning 404. Furthermore,
that notification can only be deleted with `DELETE /pending/notifications/D` (as seen in scenario 1). Such a `DELETE` call
always deletes *all* pending notifications for a device.

Mixing the use of scenario 1 type `POST /pending/notifications/{deviceId}` and scenario 2 type
`POST /pending/notifications/{deviceId}/{deviceId}` pending notifications is not advised.

If you are going to use the service and you are unsure whether additional systems will make use of this service, it may be
best to assume scenario 2, and simply always provide a service ID for your service (and use
`POST|DELETE /pending/notifications/{deviceId}/{serviceId}`).


## Minimize Data Usage

The reason for making the service unauthenticated and HTTP, rather than HTTPS, is that this allows
a device to figure out whether its needs to contact a server securely with as few TCP packets
sent "over the line" as possible.

Using the HTTP scheme in this service, the device would normally require:

- 1 upstream DNS query packet for a DNS lookup of domain of the REST API.

    - 1 downstream packet for the resolved domain IP address.

- 1 upstream SYN packet to the web server of the REST API.

    - 1 downstream SYN-ACK packer from the web server.

- 1 upstream ACK packet to the web server, establishing the TCP connection.

- 1 upstream HTTP packet with the REST API request.

    - 1 downstream HTTP packet from the REST API web server.

- 1 upstream FIN packet to close the TCP connection.

Using HTTPS would require a handshake protocol with many more TCP packets, sending around the TLS version,
a certificate, exchanging the client key, the cipher specification and more. Especially for devices which
regularly need to poll a back-end over a long period, using a GPRS/3G/4G connection, the proposed HTTP scheme
reduces data usage for those device significantly.

The back-end service itself does not actively call any other system (nor should it be possible to
do so). It gets its information injected from other internal systems, via a secured REST interface
(to be secured by setting firewall and router settings, to not allow any external calls).


## Security Concerns

As for its security: there are 2 attack scenarios.

1. The HTTP service is faked and the device contacts a hacked service instead. In this case
the service can incorrectly provide a 204 or 404 response. If the service returned 204, the
device will simply go the server in vain and find out there is nothing there. This will
cost some additional packets of data usage, but do no further harm. If the services returned
404, the device will not contact the server, where it should have, but it will try again later.
This type of attack is similar to a hacker effectively disconnecting the route the server,
which cannot be avoided either.

2. The HTTP service called by a hacked device. In this case the server will be under additional
load (which, like a DoS, cannot be prevented, even for authenticated services), but the hacked
device cannot do anything useful with the returned 204 or 404 codes. At most, the hacker may
find out there are few or many devices with pending notifications. For that matter, it is wise
to use, for example, UUIDs as the IDs, as the hacker will not be able to generate a single
ID which would not return 404 (as, statistically, the hacker cannot generate or guess the UUIDs
that were generated for the devices).


## Customizing the Properties File `notification-secret.properties`

The system requires a properties file called `notification-secret.properties` on the class path.
This file should contain the following lines:

    MongoDB.servers = localhost:27017
    MongoDB.database = pending
    MongoDB.password = admin
    MongoDB.userName = admin

If the file is `notification-secret.properties` and placed in `src/main/resources`, Maven
will include it in the WAR file which can be deployed on a server.

The file should normally not be included in this source repository, because it will always need
to be provided for a specific deployment.


## Initializing the MongoDB Database

The service can be run with an in-memory hash-map implementation or can be backed by a MongoDB
database. If you wish to use the MongoDB implementation, then you should specify the hostname,
port and username/password in the properties file `notifications-secret.properties` (see above).

The MongoDB database can be initialized with these commands from the `mongo` shell:

    use pending
    db.createUser({"user":"admin", "pwd":"admin", "roles":[]})

This creates the user `admin` with password `admin`, as specified in the properties file.


## Build and Run

In order to run the service, you need to make sure your system uses UTF-8 encoding by default.
If you run directly from Maven, this is best done by adding this environment variable to your shell:

    MAVEN_OPTS='-Dfile.encoding=UTF-8'

The source uses Java JDK 1.8, so make sure your Java compiler is set to 1.8, for example
using something like (MacOSX):

    export JAVA_HOME=`/usr/libexec/java_home -v 1.8`

To build and run the REST API, type:

    mvn clean install
    mvn jetty:run           (alternatively, you can use: "mvn tomcat7:run")

Try out if the web services work by entering the following URL in your web browser
(this should show you a HTML help page):

    http://localhost:8080/pending

Or use a tool like cURL:

    curl -X GET http://localhost:8080/pending

To create a pending notification for a specific ID:

    curl -s -X POST http://localhost:8080/pending/notifications/123

And to retrieve and delete notifications for an ID:

    curl -s -X GET    http://localhost:8080/pending/notifications/123
    curl -s -X DELETE http://localhost:8080/pending/notifications/123

    curl -s -X GET http://localhost:8080/pending/notifications

This should produce a list of IDs that have a pending notification.
If there are none, this should produce something like: `{"total":0}`
If there are some, it could produce something like: `{"total":5, ["123", ...]}`

# License

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

# Using Git and `.gitignore`

It's good practice to set up a personal global `.gitignore` file on your machine which filters a number of files
on your file systems that you do not wish to submit to the Git repository. You can set up your own global
`~/.gitignore_global` file by executing:
`git config --global core.excludesfile ~/.gitignore_global`

Note that running this command does not *create* the file, it just makes `git` use it. You need to create the
file in advance yourself (with a simple text editor).

In general, add the following file types to `~/.gitignore` (each entry should be on a separate line):
`*.com *.class *.dll *.exe *.o *.so *.log *.sql *.sqlite *.tlog *.epoch *.swp *.hprof *.hprof.index *.releaseBackup *~`

If you're using a Mac, filter:
`.DS_Store* Thumbs.db`

If you're using IntelliJ IDEA, filter:
`*.iml *.iws .idea/`

If you're using Eclips, filter:
`.classpath .project .settings .cache`

If you're using NetBeans, filter:
`nb-configuration.xml *.orig`

The local `.gitignore` file in the Git repository itself to reflect those file only that are produced by executing
regular compile, build or release commands, such as:
`target/ out/`

# Bug Reports and New Feature Requests

If you encounter any problems with this library, don't hesitate to use the `Issues` session to file your issues.
Normally, one of our developers should be able to comment on them and fix.
