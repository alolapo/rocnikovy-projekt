DROP DATABASE IF EXISTS project;

CREATE DATABASE project;
use project;

CREATE TABLE User (
	id INTEGER UNSIGNED AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(30) NOT NULL,
	pasw VARCHAR(30) NOT NULL
);

CREATE TABLE Language (
	id INTEGER,
	name VARCHAR(30),
	short VARCHAR(5)
);

CREATE TABLE Says (
	userId INTEGER,
	languageId INTEGER
);

CREATE TABLE Word (
	id INTEGER,
	languageId INTEGER,
	text VARCHAR(30) /* dane slovo */
	/* pocet_vyskytov INTEGER */
);

CREATE TABLE Knows (
	userId INTEGER,
	wordId INTEGER
);

CREATE TABLE Film (
	id INTEGER,
	/* zadal INTEGER, --meno ID zadavatela-osoby */
	name VARCHAR(50)
);

CREATE TABLE Titles (
	id INTEGER,
	filmId INTEGER,
	fileName VARCHAR(50),
	languageId INTEGER
);

CREATE TABLE Sentence (
	id INTEGER,
	titlesId INTEGER,
	poradove_cislo INTEGER,
	timeFrom VARCHAR(30),
	timeTo VARCHAR(30),	/* '17:51:04,777' */
	text TEXT
);

CREATE TABLE Contains (
	wordId INTEGER,
	sentenceId INTEGER
);

CREATE TABLE Pair (
	sentence1 INTEGER,
	sentence2 INTEGER
);

CREATE TABLE Combined (
	titles1 INTEGER,
	titles2 INTEGER
);

INSERT INTO User VALUES (1, "meno", "heslo");
INSERT INTO Language VALUES (1, "slovensky", "sk"), (2, "cesky", "cz"), (3, "english", "en");
INSERT INTO Knows VALUES (1, 1), (1, 2);

INSERT INTO Film VALUES (1, "Dirty dancing");
INSERT INTO Titles VALUES (1, 1, "dirty.dancing-cze.srt", 2), (2, 1, "dirty-dancing-english.srt", 3);
