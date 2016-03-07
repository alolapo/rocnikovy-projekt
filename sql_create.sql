DROP DATABASE IF EXISTS projekt;

CREATE DATABASE projekt;
use projekt;

CREATE TABLE Osoba (
	id INTEGER UNSIGNED AUTO_INCREMENT PRIMARY KEY,
	meno VARCHAR(30) NOT NULL,
	heslo VARCHAR(30) NOT NULL
);

CREATE TABLE Jazyk (
	id INTEGER,
	nazov VARCHAR(30),
	skratka VARCHAR(5)
);

CREATE TABLE Hovori (
	osobaId INTEGER,
	jazykId INTEGER
);

CREATE TABLE Slovo (
	id INTEGER,
	text VARCHAR(30)
	/* pocet_vyskytov INTEGER */
);

CREATE TABLE Pozna (
	osobaId INTEGER,
	slovoId INTEGER
);

CREATE TABLE Film (
	id INTEGER,
	/* zadal INTEGER, --meno ID zadavatela-osoby */
	nazov VARCHAR(50)
);

CREATE TABLE Titulky (
	id INTEGER,
	filmId INTEGER,
	meno_suboru VARCHAR(50),
	jazykId INTEGER
);

CREATE TABLE Replika (
	id INTEGER,
	titulkyId INTEGER,
	poradove_cislo INTEGER,
	casOd VARCHAR(30),
	casDo VARCHAR(30),	/* '17:51:04,777' */
	text TEXT
);

CREATE TABLE Obsahuje (
	slovoId INTEGER,
	replikaId INTEGER
);

CREATE TABLE Dvojica (
	replika1 INTEGER,
	replika2 INTEGER
);
