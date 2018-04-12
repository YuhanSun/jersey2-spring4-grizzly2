# ASU CSE412 DBMS course project phase 3 template

[![Build Status](https://travis-ci.org/jiayuasu/jersey2-spring4-grizzly2.svg?branch=master)](https://travis-ci.org/jiayuasu/jersey2-spring4-grizzly2)

This repository shows an example of how to integrate Jersey 2.6, Spring 4.0.2, and Grizzly 2.3.11, PostgreSQL JDBC.

I have updated the template a little bit to make it suitable for our project. To see the old template, please refer to "Forked from".

#### Note: this template is only tested on Ubuntu 16.04 LTS. All terminal commands mentioned in the README is only tested on Ubuntu.

## DBMS setting

The example requires a PostgreSQL database (9.X or later) called `test` with a user called `testuser` and password `1234`.

```CREATE USER testuser WITH PASSWORD '1234';```

The database should contain a table called "users" with the following schema:

```
CREATE TABLE users
(
  id integer NOT NULL,
  username character varying(64) NOT NULL,
  name character varying(64) NOT NULL,
  CONSTRAINT users_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE users
  OWNER TO testuser;
```

## Required software

Apache Maven 3

```
sudo apt-get install maven
```

JRE 1.7 or later

```
sudo apt-get install default-jre
```


## Start the server

Run the server program with a embedded server

### From an IDE

Run **Start** class.

A server should be now be running on localhost:3388. DO NOT CLOSE YOUR IDE.

### From a Linux terminal

Package the code:
```
mvn clean install
```

Then run the executable jar:
```
java -jar target/mywebapp-1.0-SNAPSHOT.jar
```

A server should be now be running on localhost:3388. DO NOT CLOSE YOUR TERMINAL.

### Optional: Turn the server program to a daemon program

In terminal:

```
nohup java -jar target/mywebapp-1.0-SNAPSHOT.jar &
```

This will detach the server program from the terminal. 

Kill the daemon server program:

1. Find the PID
```
ps ax|grep mywebapp-1.0-SNAPSHOT
```

2. Kill the program
```
kill -9 THEPID
```

## Issue a HTTP request

### Add a user

Open your browser and enter the address in the addree box:
```
http://localhost:3388/adduser/jack
```
which should the string "added one user". Your PostgreSQL users table should also have a new user called "jack".

### Get a user
Open your browser and enter the address in the addree box:
```
http://localhost:3388/getuser/jack
```
which should return the users which have username "jack".

### Get all users
Open your browser and enter the address in the addree box:
```
http://localhost:3388/getuser/
```
which should return all users.

## What's the next step?
Use your own movide database. Use your own HTML and Javascript to send/receive the HTTP requests and draw charts.
