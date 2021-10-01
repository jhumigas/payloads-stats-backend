# Payloads-stats-backend

## Getting Started

You'll need pre-installed:

* Java 11 ([get it](https://adoptopenjdk.net/installation.html))
* Maven ([get it](https://maven.apache.org/install.html))
* Docker ([get it](https://docs.docker.com/get-docker/))

### Build and Tests

Run:

```sh
# Run tests
make mvn-test
# Package app which will produce .jar in target/
make mvn-package
```

### Run App Locally


#### Dev Mode

During development, you will need the database and a tool to administrate your database, you need to run the following

```sh
# Start db in docker and set up pgadmin 
make start-db
# Start spring-boot
make spring-boot-run
```

You can test if it is working with curl (or even postman):

```sh
curl --location --request POST 'http://localhost:8080/stats' \
    --header 'Content-Type: application/json' \
    --data-raw '{
    "token": "c98arf53-ae39-4c9d-af44-c6957ee2f748",
    "customer": "Customer1",
    "content": "channel1",
    "timespan": 30000,
    "p2p": 0,
    "cdn": 1,
    "sessionDuration": 2
    }'
```

Navigate to http://localhost:5050/ to see your postgres database.
#### Prod Mode
To run the whole application locally, you can just run:

```sh
# Start App
make server-up
# Stop App
make server-down
```

## Context

### Introduction

Each device playing a video sends statistics to our data pipeline. That data is sent by each device through HTTP as **JSON payloads**. Each device sends one payload **every 30 seconds**.

The content of each payload is described below: 
```
{
    "token" : "c98arf53-ae39-4c9d-af44-c6957ee2f748",
    "customer": "Customer1",
    "content": "channel1",
    "timespan": 30000,
    "p2p": 560065,
    "cdn": 321123,
    "sessionDuration": 120000,
}
```

- **token** (string) : Unique id generated **at the beginning of each video session (i.e when a new video is started)**, and sent in all the payloads of that video session. Each token is then unique to a video session.
- **customer** (string) : The name of the company broadcasting the video being played
- **content** (string) : The name of the content being played
- **timespan** (int64) : the time (in milliseconds) elapsed since the last time the device sent a payload in that video session (so in our example, timespan is always equal to 30000)
- **p2p** (int64) : The volume (in bytes) downloaded from other devices in Peer-to-Peer since the last time the device sent a payload in that video session.
- **cdn** (int64) : The volume (in bytes) downloaded from the broadcaster's servers (cdn is short for Content Delivery Network) since the last time the device sent a payload in that video session.
- **sessionDuration** (int64) : Total time elapsed (in milliseconds) **since the beginning of the video session**.

### Overview

#### Payload Stats 

HTTP server that exposes a `/stats` route. The server is be able to: 

- Receive the JSON payloads sent by devices
- Write the data into a postgreSQL database (in a `stats` table). Each row in the table should be the aggregation of payloads received during a **5 minutes time window**. The table should have the following columns:

    * **Time** (datetime) : the timestamp of **the end** of the 5-minute window. We expect this time to be based on the time of reception of the payload by the server.
    * **customer** (string) : the customer aggregated in that row
    * **content** (string) : the content aggregated in that row
    * **cdn** (integer) : the **sum** (in bytes) of all the data downloaded via the CDN during the time window (for the corresponding content and customer)
    * **p2p** (integer) : the **sum** (in bytes) of all the data downloaded via P2p during the time window (for the corresponding content and customer)

#### Payload Stats with Sessions

If we had to count the number of video sessions started in a given time window, we could use the token sent in each payload, with the session duration.
Given a time window, a payload corresponds to a new video session if its `sessionDuration` is less than the time window length and if one of the two following is fulfilled:

    * its `token` was never seen before or,
    * `cdn` + `p2p` is 0

### Payloads In Messaging Queue

If we consider scalability due to numerous incoming payloads, and if we have multiple Spring Boot Apps processing payloads, but writing to the same database, how can we make sure that:
* each backend is synchronized w.r.t to the payloads processed ? 
* two backends processing two payloads at the same time compute in a sequential fashions the payload stats

We could either play on the types of transactions made to the database, make them isolated. Or/And we could add intermediate queues so that we process the payloads in a distributed fashion: make services on the side that receive the payloads, and also services that will process the payloads.

### How It Was Done

1. Init app with [spring boot initializr](https://start.spring.io/), add dependencies: web, jpa, postgres driver, lombok
2. Install and configure postgres locally using docker
3. Work on data layer: models, dto, repository
4. Work on services to interact with repository
5. Work on controller


# References

* [Spring Boot](https://spring.io/projects/spring-boot)
* [Docker Example with Postgres](https://towardsdatascience.com/how-to-run-postgresql-and-pgadmin-using-docker-3a6a8ae918b5)
* [Sprint Boot Testing](https://www.baeldung.com/spring-boot-testing)
* [Spribg Boot Kafka Testing](https://www.baeldung.com/spring-boot-kafka-testing)
* [Spring Boot Kafka Docker](https://habr.com/en/post/529222/)
* [Shared State Microservices for Distributed Systems Using Kafka](https://www.confluent.io/blog/building-shared-state-microservices-for-distributed-systems-using-kafka-streams/)

