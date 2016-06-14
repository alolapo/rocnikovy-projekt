// Copyright (c) 2014, the Dart project authors.  Please see the AUTHORS file
// for details. All rights reserved. Use of this source code is governed by a
// BSD-style license that can be found in the LICENSE file.

// Server to basic_writer_client.dart.
// Receives JSON encoded data in a POST request and writes it to
// the file specified in the URI.

import 'dart:io';
import 'dart:convert';
import 'package:sqljocky/sqljocky.dart';
import 'dart:async';

final HOST = InternetAddress.LOOPBACK_IP_V4;
final PORT = 4049;

void main () {
  HttpServer.bind(HOST, PORT).then(gotMessage, onError: printError);
}

void gotMessage(_server) {
  _server.listen((HttpRequest request) {
    print('got Message');
    switch (request.method) {
      case 'POST':
        handlePost(request);
        break;
      case 'OPTIONS':
        handleOptions(request);
        break;
      default: defaultHandler(request);
    }
  },
      onError: printError); // Listen failed.
  print('Listening for GET and POST on http://$HOST:$PORT');
}

void handleOptions(HttpRequest req) {
  HttpResponse res = req.response;
  addCorsHeaders(res);
  print('${req.method}: ${req.uri.path}');
  res.statusCode = HttpStatus.NO_CONTENT;
  res.close();
}

void addCorsHeaders(HttpResponse res) {
  res.headers.add('Access-Control-Allow-Origin', '*');
  res.headers.add('Access-Control-Allow-Methods', 'POST, OPTIONS');
  res.headers.add('Access-Control-Allow-Headers',
      'Origin, X-Requested-With, Content-Type, Accept');
}

handlePost(HttpRequest req) async {

  try {
    var jsonString = await req.transform(UTF8.decoder).join();
    Map jsonData = JSON.decode(jsonString);
    print("data:");
    print(jsonData);

    // switch na jsonData['type']
    switch (jsonData['type']){
      case 'login':
        await login(req, jsonData);
        break;
      case 'register':
        await register(req, jsonData);
        break;
      case 'changePassw':
        await changePassw(req, jsonData);
        break;
      case 'getMyLanguages':
        await getMyLanguages(req, jsonData);
        break;
      case 'changeLanguages':
        await changeLanguages(req, jsonData);
        break;
      case 'createOffer':
        await createOffer(req, jsonData);
        break;
      case 'showMe':
        await showMe(req, jsonData);
        break;
      case 'getTextOfSentence':
        await getTextOfSentence(req, jsonData);
        break;
      case 'something':
        await something(req, jsonData);
        break;
      default:
        await printTable(req, jsonData);
        break;
    }

  } catch (e) {
    req.response..statusCode = HttpStatus.INTERNAL_SERVER_ERROR
      ..write("Exception during file I/O: $e.")
      ..close();
  }

}


Future printTable(HttpRequest req, Map jsonData) async {
  HttpResponse res = req.response;
  addCorsHeaders(res);

  String query = 'SELECT * from LANGUAGE WHERE userId="'+jsonData['name']+'"';

  print(query);
  Map answer = await dbConnect(query);
  print(answer);



  res.write(JSON.encode(answer));
  res.close();
}

Future login(HttpRequest req, Map jsonData) async {
  HttpResponse res = req.response;
  addCorsHeaders(res);

  String query = "SELECT * FROM User WHERE name='"+jsonData['name']+"' AND pasw='"+jsonData['passw']+"'";

  print(query);
  Map answer = await dbConnect(query);
  print("answer: ");
  print(answer);

  Map object = new Map();
  if (answer.length == 1){
    object['answer'] = "yes";
    object['userId'] = answer["0"]["col0"];
  } else {
    object['answer'] = "no";
  }

  res.write(JSON.encode(object));
  res.close();
}

Future register(HttpRequest req, Map jsonData) async {
  HttpResponse res = req.response;
  addCorsHeaders(res);

  String query = "SELECT * FROM User WHERE name='"+jsonData['name']+"' AND pasw='"+jsonData['passw']+"'";

  print(query);
  Map answer = await dbConnect(query);
  print("answer: ");
  print(answer);

  Map object = new Map();
  if (answer.length == 0){
    object['answer'] = "yes";
    query = "INSERT INTO User (name, pasw) VALUES ('"+jsonData['name']+"', '"+jsonData['passw']+"')";
    print(query);
    answer = await dbConnect(query);
    print("answer: ");
    print(answer);

  } else {
    object['answer'] = "no";
    object['info'] = "Uzivatel s tvojim menom uz je zaregistrovany. Zvol ine meno a skus znova.";
  }

  res.write(JSON.encode(object));
  res.close();
}

Future changePassw(HttpRequest req, Map jsonData) async {
  HttpResponse res = req.response;
  addCorsHeaders(res);

  String query = "UPDATE User SET pasw='"+jsonData['passw']+"' WHERE name='"+jsonData['name']+"'";

  print(query);
  Map answer = await dbConnect(query);
  print("answer: ");
  print(answer);

  // TODO zistit ci boli nejake riadky zmenene, teraz vyhlasi OK vzdy
  Map object = new Map();
  if (answer.length == 0){
    object['answer'] = "yes";

  } else {
    object['answer'] = "no";
    object['info'] = "Nieco sa bohuzial pokazilo.";
  }

  res.write(JSON.encode(object));
  res.close();
}

Future getMyLanguages(HttpRequest req, Map jsonData) async {
  print("called 'getMyLanguages'");
  HttpResponse res = req.response;
  addCorsHeaders(res);

  String query = 'SELECT * FROM Says WHERE userId="'+jsonData['userId'].toString()+'"';

  print("query is :");

  print(query);
  Map answer = await dbConnect(query);
  print("answer of My Languages: ");
  print(answer);

  query = 'SELECT * FROM Language';

  print("query is :");
  print(query);
  Map answer2 = await dbConnect(query);
  print("answer of all Languages: ");
  print(answer2);

  for (var line in answer.keys){
    int languageId = answer[line]["col1"];
    for(var l in answer2.keys){
      if(languageId == answer2[l]["col0"]){
        answer2[l]["checked"] = true;
      }
    }
  }

  print(answer2);
  res.write(JSON.encode(answer2));
  res.close();
}

Future changeLanguages(HttpRequest req, Map jsonData) async {
  HttpResponse res = req.response;
  addCorsHeaders(res);

  Map myData = jsonData["checkedList"];
  int id = jsonData["userId"];

  StringBuffer insertQuery = new StringBuffer("INSERT INTO Says VALUES ");
  StringBuffer deleteQuery = new StringBuffer("DELETE FROM Says WHERE ");
  int toInsert = 0, toDelete = 0;

  for (var item in myData.keys){
    print(item);
    if (myData[item] == true){
      if (toInsert != 0){
        insertQuery.write(",");
      }
      toInsert++;
      insertQuery.write("(${id},${item})");
    } else {
      if (toDelete != 0){
        deleteQuery.write(" OR ");
      }
      toDelete++;
      deleteQuery.write("(userId='${id}' AND languageId='${item}')");
    }
  }

  Map answer = null, answer2 = null;
  // TODO neviem ako zistit ci bol dotaz do DB uspesny alebo nie - snad nejaky try-catch blok?
  Map object = new Map();

  if (toInsert > 0){
    print(insertQuery.toString());
    answer = await dbConnect(insertQuery.toString());
  }

  if (toDelete > 0){
    print(deleteQuery.toString());
    answer2 = await dbConnect(deleteQuery.toString());
  }

  if ((answer2==null || answer2.length == 0) && (answer==null || answer.length == 0)){
    object['answer'] = "yes";
  } else {
    object['answer'] = "no";
  }

  res.write(JSON.encode(object));
  res.close();
}

//SELECT Film.name AS film, Titles.id AS titlesId, Titles.languageId AS languageId FROM Film,Titles WHERE Film.id=Titles.filmId;
Future createOffer(HttpRequest req, Map jsonData) async {
  HttpResponse res = req.response;
  addCorsHeaders(res);

  String query = "SELECT Film.name AS film, Titles.languageId AS languageId, Titles.id AS titlesId FROM Film,Titles WHERE Film.id=Titles.filmId ORDER BY Film.name, Titles.languageId, Titles.id DESC";

  print(query);
  Map answer = await dbConnect(query);
  print("answer: ");
  print(answer);

  Map object = new Map();
  String filmName = "";
  Map film = new Map();

  /*
  for(int key in answer.keys){
    print (key);
    Map row = answer[key];
    print (row);
    print (row["col0"] != filmName);
    if (row["col0"] != filmName){
      print("kluc ${row["col0"]} este nie je v objekte");
      print (key>0);
      if (key > 0){
        object[filmName] = film;
        film = new Map();
      }
      print ("created new Map 'film'");
    }
    if (film.containsKey(row["col1"])){
      film[row["col1"]][film[row["col1"]].length] = row["col2"];
    } else {
      print("create new key in film ${row["col1"]}");
      film[row["col1"]] = new List();
      film[row["col1"]][0] = row["col2"];
    }
    print(film);
  }
*/
  object[filmName] = film;
  print(object);

  /*
  if (answer.length == 1){
    object['answer'] = "yes";
    object['userId'] = answer["0"]["col0"];
  } else {
    object['answer'] = "no";
  }
  res.write(JSON.encode(object));
  res.close();
*/
}

//SELECT Word.id, Word.text, COUNT(Contains.sentenceId), Contains.sentenceId FROM Contains, Word WHERE Contains.wordId=Word.id AND Word.languageId=3 GROUP BY Word.id, Word.text HAVING COUNT(Contains.sentenceId)>15 ORDER BY COUNT(Contains.sentenceId) DESC;
/*
SELECT Word.id, Word.text FROM Contains, Word WHERE Contains.wordId=Word.id AND Word.languageId=3 AND NOT EXISTS (SELECT * FROM Knows WHERE Knows.userId=1 AND Knows.wordId=Word.id) GROUP BY Word.id, Word.text ORDER BY COUNT(Contains.sentenceId) DESC LIMIT 30;
dva stlpce - id slova a text slova, zoradene podla poctu vyskytov
- mozno treba pridat ku kazdemu slovu v db ze v akom filme je
 */

Future showMe(HttpRequest req, Map jsonData) async {
  HttpResponse res = req.response;
  addCorsHeaders(res);

  // co ak sa chcem ucit aj ked nie som prihlasena?
  String query = "SELECT Word.id, Word.text "+
      "FROM Contains, Word "+
      "WHERE Contains.wordId=Word.id AND Word.languageId="+jsonData['titles1'].toString()+
      " AND NOT EXISTS (SELECT * FROM Knows WHERE Knows.userId="+jsonData['userId'].toString()+
      " AND Knows.wordId=Word.id)"+
      " GROUP BY Word.id, Word.text"+
      " ORDER BY COUNT(Contains.sentenceId) DESC"+
      " LIMIT 30";
  print(query);
  Map answer = await dbConnect(query);
  print("answer: ");
  print(answer);

  Map object = new Map();

  for (var line in answer.keys){
    int wordId = answer[line]["col0"];
    int wordText = answer[line]["col1"];

    object['${wordId}'] = new Map();
    int i = 0;
    object['${wordId}']['${i++}'] = wordText;

    String q2 = "SELECT sentenceId FROM Contains WHERE wordId="+wordId.toString();
    Map answ2 = await dbConnect(q2);

    for(var l in answ2.keys){
      object['${wordId}']['${i++}']=answ2[l]["col0"];
    }
  }

  // KONIEC SPRACOVANIA ANSWER, zahrnut niekde, ci mam pridat aj "prekladove" titulky

  res.write(JSON.encode(object));
  res.close();
}

Future getTextOfSentence(HttpRequest req, Map jsonData) async {
  HttpResponse res = req.response;
  addCorsHeaders(res);

  String query = "SELECT text FROM Sentence WHERE id="+jsonData['id'].toString();
  print(query);
  Map answer = await dbConnect(query);
  print("answer: ");
  print(answer);

  Map object = new Map();
  object['answer'] = '"'+answer['0']["col0"].toString()+'"';
  print("object['answer']= ${object['answer']}");

  if (jsonData.containsKey('secondLanguage')){
    int titlesId = jsonData['secondLanguage'];

    //       SELECT Sentence.text FROM Pair, Sentence WHERE (((Pair.sentence2=Sentence.id AND Pair.sentence1=762) OR (Pair.sentence1=Sentence.id AND Pair.sentence2=762)) AND (Sentence.titlesId=1));
    query = "SELECT Sentence.text FROM Pair, Sentence WHERE (((Pair.sentence2=Sentence.id AND Pair.sentence1="
        +jsonData['id'].toString()
        +") OR (Pair.sentence1=Sentence.id AND Pair.sentence2="+jsonData['id'].toString()+")))";
        //+")) AND (Sentence.titlesId="+jsonData['secondLanguage'].toString()+"))";
    print(query);
    Map answer = await dbConnect(query);
    print("answer: ");
    print(answer);
    object['second'] = '"'+answer['0']["col0"].toString()+'"';

    //object['second'] = "prelozena veta";
  }

  print(object);

  res.write(JSON.encode(object));
  print("close");
  res.close();
}

void defaultHandler(HttpRequest req) {
  HttpResponse res = req.response;
  addCorsHeaders(res);
  res.statusCode = HttpStatus.NOT_FOUND;
  res.write('Not found: ${req.method}, ${req.uri.path}');
  res.close();
}

void printError(error) => print(error);


Future dbConnect(String query) async {
  Map data = new Map();
  print('called dbConnect');
  var pool = new ConnectionPool(
      host: 'localhost',
      port: 3306,
      user: "root",
      password: null,
      db: 'project',
      max: 5);
  print('connection created');
  var results = await pool.query(query);
  int index = 0;
  print('gonna create data from db result');
  await results.forEach( (row){
    Map r = new Map();
    for (int i = 0; i < row.length; i++){
      r['col${i}'] = row[i];
    }

    data.putIfAbsent('${index}', () => r);
    index++;
  });
  return data;
}

Future something(HttpRequest req, Map jsonData) async {
  HttpResponse res = req.response;
  addCorsHeaders(res);

  var pool = new ConnectionPool(
      host: 'localhost',
      port: 3306,
      user: "root",
      password: null,
      db: 'project',
      max: 5);
  print('connection created');

  var query = await pool.prepare('SELECT * from User WHERE name=?');
  var result = await query.execute(['meno']);

  print(query.toString());

  Map answer = new Map();
  int index = 0;
  await result.forEach( (row){
    Map r = new Map();
    for (int i = 0; i < row.length; i++){
      r['col${i}'] = row[i];
      print(row[i]);
    }
    answer.putIfAbsent('${index}', () => r);
    index++;
  });

  print(answer);

  res.write(JSON.encode(answer));
  res.close();
}

/*
 * Encode/Decode functions for Dart
 *
 * Copyright 2011 Google Inc.
 * Neil Fraser (fraser@google.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

//library('URI Encode Decode');

/**
 * Implementation of JavaScript's encodeURI function.
 * [text] is the string to escape.
 * Returns the escaped string.
 */
String encodeURI(text) {
  StringBuffer encodedText = new StringBuffer();
  final String whiteList = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ' +
      'abcdefghijklmnopqrstuvwxyz' +
      '0123456789-_.!~*\'()#;,/?:@&=+\$';
  final String hexDigits = '0123456789ABCDEF';
  for (int i = 0; i < text.length; i++) {
    if (whiteList.indexOf(text[i]) != -1) {
      // This character doesn't need encoding.
      encodedText.write(text[i]);
      continue;
    }
    int charCode = text.charCodeAt(i);
    List<int> byteList = [];
    if (charCode < 0x80) {
      // One-byte code.
      // 0xxxxxxx
      byteList.add(charCode);
    } else if (charCode < 0x800) {
      // Two-byte code.
      // 110xxxxx 10xxxxxx
      byteList.add(charCode >> 6 | 0xC0);
      byteList.add(charCode & 0x3F | 0x80);
    } else if (0xD800 <= charCode && charCode < 0xDC00) {
      // Low surrogate.  Next char must be a high surrogate.
      int nextCharCode = text.length == i + 1 ? 0 : text.charCodeAt(i + 1);
      if (0xDC00 <= nextCharCode && nextCharCode < 0xE000) {
        // Four-byte surrogate pair.
        // 11110xxx 10xxxxxx 10xxyyyy 10yyyyyy
        // Where xxxxxxxxxxx is offset by 1000000 (0x40)
        charCode += 0x40;
        byteList.add(charCode >> 8 & 0x7 | 0xF0);
        byteList.add(charCode >> 2 & 0x3F | 0x80);
        byteList.add(((charCode & 0x3) << 4) |
        (nextCharCode >> 6 & 0xF) | 0x80);
        byteList.add(nextCharCode & 0x3F | 0x80);
      } else {
        throw new
        Exception('URI malformed: Orphaned low surrogate.');
      }
      // Skip next character.
      i++;
    } else if (0xDC00 <= charCode && charCode < 0xE000) {
      throw new
      Exception('URI malformed: Orphaned high surrogate.');
    } else if (charCode < 0x10000) {
      // Three-byte code.
      // 1110xxxx 10xxxxxx 10xxxxxx
      byteList.add(charCode >> 12 | 0xE0);
      byteList.add(charCode >> 6 & 0x3F | 0x80);
      byteList.add(charCode & 0x3F | 0x80);
    }
    for (int i = 0; i < byteList.length; i++) {
      encodedText.write('%');
      encodedText.write(hexDigits[byteList[i] >> 4]);
      encodedText.write(hexDigits[byteList[i] & 0xF]);
    }
  }
  return encodedText.toString();
}

/**
 * Implementation of JavaScript's encodeURIComponent function.
 * [text] is the string to escape.
 * Return the escaped string.
 */
String encodeURIComponent(text) {
  // This is the same as encodeURI except the following are also escaped:
  // #;,/?:@&=+$  -> %23%3B%2C%2F%3F%3A%40%26%3D%2B%24
  text = encodeURI(text);
  return text.replaceAll('#', '%23')
      .replaceAll(';', '%3B')
      .replaceAll(',', '%2C')
      .replaceAll('/', '%2F')
      .replaceAll('?', '%3F')
      .replaceAll(':', '%3A')
      .replaceAll('@', '%40')
      .replaceAll('&', '%26')
      .replaceAll('=', '%3D')
      .replaceAll('+', '%2B')
      .replaceAll('\$', '%24');
}

/**
 * Implementation of JavaScript's decodeURI function.
 * [text] is the string to unescape.
 * Returns the unescaped string.
 */
String decodeURI(text) {
  final String hexDigits = '0123456789ABCDEF';
  // First, break up the text into parts.
  List<String> parts = text.split('%');
  int state = 0;
  int multiByte;  // Temp register for assembling a multi-byte value.
  bool surrogate = false;
  // Skip the first element, it's guaranteed to be a (possibly empty) string.
  for (int i = 1; i < parts.length; i++) {
    String part = parts[i];
    if (part.length < 2) {
      throw new Exception('URI malformed: Missing digits.');
    }
    int hex1 = hexDigits.indexOf(part[0].toUpperCase());
    int hex2 = hexDigits.indexOf(part[1].toUpperCase());
    parts[i] = part.substring(2);
    if (hex1 == -1 || hex2 == -1) {
      throw new Exception('URI malformed: Invalid digits.');
    }
    int charCode = hex1 * 16 + hex2;
    if (state == 0) {
      if (charCode < 0x80) {
        // One-byte code.
        // 0xxxxxxx
        multiByte = charCode;
        state = 0;
      } else if ((charCode & 0xE0) == 0xC0) {
        // Two-byte code.
        // 110xxxxx 10xxxxxx
        multiByte = charCode & 0x1F;
        state = 1;
      } else if ((charCode & 0xF0) == 0xE0) {
        // Three-byte code.
        // 1110xxxx 10xxxxxx 10xxxxxx
        multiByte = charCode & 0xF;
        state = 2;
      } else if ((charCode & 0xF8) == 0xF0) {
        // Four-byte surrogate pair.
        // 11110xxx 10xxxxxx 10xxyyyy 10yyyyyy
        multiByte = charCode & 0x7;
        state = 3;
        surrogate = true;
      } else {
        throw new Exception('URI malformed: Unknown Unicode.');
      }
    } else {
      // All continuation bytes are in the form 10xxxxxx.
      if ((charCode & 0xC0) != 0x80) {
        throw new Exception('URI malformed: Expect 10xxxxxx.');
      }
      multiByte = (multiByte << 6) | (charCode & 0x3F);
      state--;
    }
    if (state == 0) {
      // Character is fully assembled.  Add to string.
      if (surrogate) {
        surrogate = false;
        // Insert surrogate pair.
        // xxxxxxxxxxxyyyyyyyyyy (21 bits)
        // Where xxxxxxxxxxx is offset by 1000000 (0x40)
        int x = (multiByte >> 10) - 0x40 + 0xD800;
        int y = (multiByte & 0x3FF) + 0xDC00;
        if (x >= 0xDC00 || y >= 0xE000) {
          throw new
          Exception('URI malformed: Invalid surrogate.');
        }
        parts.insert(i, new String.fromCharCodes([x, y]));
      } else {
        // Insert a single character.
        parts.insert(i, new String.fromCharCodes([multiByte]));
      }
      // Skip the element we just inserted.
      i++;
    } else {
      // This code must be directly followed by another code.
      if (!parts[i].isEmpty) {
        throw new Exception('URI malformed: Incomplete code.');
      }
    }
  }
  if (state != 0) {
    throw new Exception('URI malformed: Truncated code.');
  }
  StringBuffer answ = new StringBuffer();
  answ.writeAll(parts);
  return answ.toString();
}

/**
 * Implementation of JavaScript's decodeURIComponent function.
 * [text] is the string to unescape.
 * Returns the unescaped string.
 */
String decodeURIComponent(text) {
  return decodeURI(text);
}