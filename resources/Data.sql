/* Setting up PROD DB */
DROP DATABASE IF EXISTS prod;
create database prod;
use prod;
create table parking(
PARKING_NUMBER int PRIMARY KEY,
AVAILABLE boolean NOT NULL,
TYPE varchar(10) NOT NULL
);

create table ticket(
 ID int PRIMARY KEY AUTO_INCREMENT,
 PARKING_NUMBER int NOT NULL,
 VEHICLE_REG_NUMBER varchar(10) NOT NULL,
 PRICE double,
 IN_TIME DATETIME NOT NULL,
 OUT_TIME DATETIME,
 FOREIGN KEY (PARKING_NUMBER)
 REFERENCES parking(PARKING_NUMBER));

insert into parking(PARKING_NUMBER,AVAILABLE,TYPE) values(1,true,'CAR');
insert into parking(PARKING_NUMBER,AVAILABLE,TYPE) values(2,true,'CAR');
insert into parking(PARKING_NUMBER,AVAILABLE,TYPE) values(3,true,'CAR');
insert into parking(PARKING_NUMBER,AVAILABLE,TYPE) values(4,true,'BIKE');
insert into parking(PARKING_NUMBER,AVAILABLE,TYPE) values(5,true,'BIKE');
commit;

/* Setting up TEST DB */
DROP DATABASE IF EXISTS test;
create database test;
use test;
create table parking(
PARKING_NUMBER int PRIMARY KEY,
AVAILABLE boolean NOT NULL,
TYPE varchar(10) NOT NULL
);

create table ticket(
 ID int PRIMARY KEY AUTO_INCREMENT,
 PARKING_NUMBER int NOT NULL,
 VEHICLE_REG_NUMBER varchar(10) NOT NULL,
 PRICE double,
 IN_TIME DATETIME NOT NULL,
 OUT_TIME DATETIME,
 FOREIGN KEY (PARKING_NUMBER)
 REFERENCES parking(PARKING_NUMBER));

insert into parking(PARKING_NUMBER,AVAILABLE,TYPE) values(1,true,'CAR');
insert into parking(PARKING_NUMBER,AVAILABLE,TYPE) values(2,true,'CAR');
insert into parking(PARKING_NUMBER,AVAILABLE,TYPE) values(3,true,'CAR');
insert into parking(PARKING_NUMBER,AVAILABLE,TYPE) values(4,true,'BIKE');
insert into parking(PARKING_NUMBER,AVAILABLE,TYPE) values(5,true,'BIKE');
commit;

/* Setting up SONAR DB */
DROP DATABASE IF EXISTS sonar;
create database sonar;
use sonar;
create table users (
ID int PRIMARY KEY AUTO_INCREMENT,
LOGIN varchar(20) NOT NULL,
PASSWORD varchar(20) NOT NULL,
CONNECTION_NUMBER int(4) NOT NULL DEFAULT 0);

insert into users( ID, LOGIN, PASSWORD, CONNECTION_NUMBER) values(2, 'Bounama', 'Ly', 0 );
insert into users( ID, LOGIN, PASSWORD, CONNECTION_NUMBER) values(3, 'Admin', 'Admincd', 0 );



