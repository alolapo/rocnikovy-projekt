var user, userId;

var filmId = 1;
var filmName = "Dirty dancing";
var titles1 = 3; // anglicky
var titles1id = 2;
var titles2 = 2; // cesky
var titles2id = 1;
//var titles2 = null;
var currentlyDisplayed = null;

console.log("prilinkovany");
//createOffer();

function createOffer(){
	// zisti, ktore kombinacie mas k dispozicii
	var div = document.getElementById("offer");
	
	var objekt = {'type':'createOffer'};

	// sprav dotaz na server, ci existuje takyto uzivatel
	// ak ano, vypytaj si potrebne veci

  xhr = new XMLHttpRequest();
  var url = "http://localhost:4049";
  var method = "POST";
  xhr.open(method, url, true);
  xhr.onreadystatechange = function () { 
    if (xhr.readyState == 4 && xhr.status == 200) {
      var json = JSON.parse(xhr.responseText);
      //console.log(json);
      
      // TODO spracuj json
      
    }
  }

  var data = JSON.stringify(objekt);
  xhr.send(data);
}

function login(){
	var name = document.getElementById('name').value;
	var passw = document.getElementById('passw').value;
	var objekt = {'type':'login', 'name':name, 'passw':passw};

	// sprav dotaz na server, ci existuje takyto uzivatel
	// ak ano, vypytaj si potrebne veci

  xhr = new XMLHttpRequest();
  var url = "http://localhost:4049";
  var method = "POST";
  xhr.open(method, url, true);
  xhr.onreadystatechange = function () { 
    if (xhr.readyState == 4 && xhr.status == 200) {
      var json = JSON.parse(xhr.responseText);
      //console.log(json);
      
      // TODO spracuj json
      var answer = json["answer"];
      //console.log(answer);
      if (answer == "yes"){
      	console.log("spravne meno a heslo");
      	// "prihlasit" ma s tymto menom
      	user = name;
      	userId = json['userId'];
      	// zmenit zobrazenie stranok, ktore ma a ktore nema vidiet
      	showLogged();
      } else {
      	// TODO vypis, ze sa nepodarilo prihlasit
      	console.log("nespravne meno alebo heslo");
      }

    }
  }

  var data = JSON.stringify(objekt);
  xhr.send(data);
}

function register(){
	var name = document.getElementById('regName').value;
	var passw = document.getElementById('regPassw').value;
	var passw2 = document.getElementById('regPassw2').value;
	if (passw != passw2) {
		// TODO vypis ze heslo je nespravne
		console.log("hesla sa nezhoduju");
		return;
	}
	
	var objekt = {'type':'register', 'name':name, 'passw':passw};

	// overit ci uz existuje taky pouzivatel, ak ano, nedovolim to
	xhr = new XMLHttpRequest();
  var url = "http://localhost:4049";
  var method = "POST";
  xhr.open(method, url, true);
  xhr.onreadystatechange = function () { 
    if (xhr.readyState == 4 && xhr.status == 200) {
      var json = JSON.parse(xhr.responseText);
      //console.log(json);
      
      // TODO spracuj json
      var answer = json["answer"];
      //console.log(answer);
      if (answer == "yes"){
      	console.log("registracia prebehla uspesne");
      	// ked sa odhlasim, budem mat vyplnene vlastne meno a heslo
      	document.getElementById('name').value = document.getElementById('regName').value;
				document.getElementById('passw').value = document.getElementById('regPassw').value;
				document.getElementById('regName').value = "";
				document.getElementById('regPassw').value = "";
				document.getElementById('regPassw2').value = "";
	
				// "prihlasit" ma s tymto menom
      	login();
      } else {
      	// TODO vypis, ze sa nepodarilo prihlasit
      	console.log("registracia sa nepdarila");
      	if (json["info"] != null){
      		console.log(json["info"]);
      	}
      }

    }
  }

  var data = JSON.stringify(objekt);
  xhr.send(data);

}


function logout(){
	showUnlogged();
	user = null;
}

function changePassw(){
	var passw = document.getElementById('chgPassw').value;
	var passw2 = document.getElementById('chgPassw2').value;
	if (passw != passw2) {
		// TODO vypis ze heslo je nespravne
		console.log("hesla sa nezhoduju");
		return;
	}
	
	var objekt = {'type':'changePassw', 'name':user, 'passw':passw};

	xhr = new XMLHttpRequest();
  var url = "http://localhost:4049";
  var method = "POST";
  xhr.open(method, url, true);
  xhr.onreadystatechange = function () { 
    if (xhr.readyState == 4 && xhr.status == 200) {
      var json = JSON.parse(xhr.responseText);
      //console.log(json);
      
      // TODO spracuj json
      var answer = json["answer"];
      //console.log(answer);
      if (answer == "yes"){
      	// TODO vypis ze sa podarilo prihlasit
      	console.log("heslo bolo uspesne zmenene");
      } else {
      	// TODO vypis, ze sa nepodarilo prihlasit
      	console.log("niekde nastal problem");
      	if (json["info"] != null){
      		console.log(json["info"]);
      	}
      }
    }
  }

  var data = JSON.stringify(objekt);
  xhr.send(data);
}

function changeLanguages(){
	var objekt = {'type':'changeLanguages', 'userId':userId};
	var list = document.getElementsByClassName("checkboxListLanguages");
	var checkedList = {};
	for (var x = 0; x < list.length; x++){
		checkedList[list[x].getAttribute("languageId")] = list[x].checked;
	}
	objekt["checkedList"] = checkedList;
	//console.log(objekt);

	xhr = new XMLHttpRequest();
  var url = "http://localhost:4049";
  var method = "POST";
  xhr.open(method, url, true);
  xhr.onreadystatechange = function () { 
    if (xhr.readyState == 4 && xhr.status == 200) {
      var json = JSON.parse(xhr.responseText);
      //console.log(json);

			// TODO spracuj json
      var answer = json["answer"];
      //console.log(answer);
      if (answer == "yes"){
      	// TODO vypis ze sa podarilo prihlasit
      	console.log("jazyky boli uspesne zmenene");
      } else {
      	// TODO vypis, ze sa nepodarilo prihlasit
      	console.log("niekde nastal problem");
      	if (json["info"] != null){
      		console.log(json["info"]);
      	}
      }

    }
  }
  var data = JSON.stringify(objekt);
  xhr.send(data);
}

function showMe(){
	// TODO zobrazi top 15 najcastejsich slov z danych tituliek ktore este nepoznam
/*
var user, userId;
filmId = 1;
var filmName = "Dirty dancing";
var titles1 = 2; // anglicky
var titles2 = 1; // cesky
*/
	var objekt = {'type':'showMeTable', 'filmId':filmId, 
  'titles1':titles1, 'userId':userId, 'limit':15};

	xhr = new XMLHttpRequest();
  var url = "http://localhost:4049";
  var method = "POST";
  xhr.open(method, url, true);
  xhr.onreadystatechange = function () { 
    if (xhr.readyState == 4 && xhr.status == 200) {
      var json = JSON.parse(xhr.responseText);
      //console.log(json);
      
      // alert - ze sa spracuvava poziadavka
      currentlyDisplayed = json;
      fillTableFromJson(json);
      document.getElementById('filmName').innerHTML = filmName;
      document.getElementById('tabulkaSlovicok').style.display = "inline";
      
    }
  }

  var data = JSON.stringify(objekt);
  xhr.send(data);

}

function fillTableFromJson(json){
	var table = document.getElementById("wordsTable");

	// TODO vymaz vsetky deti, spracuj zaznacene zmeny	
	table.innerHTML = "";

  if ( json["answer"]!= null && json["answer"]['answer'] == "no"){
    console.log("nepodarilo sa nacitat");
    return;
  }

  var key, tr, td;
  for (key in json){
  	//console.log(key);
    if (key =='answer'){
      continue;
    }

  	tr = document.createElement('tr');

  	td = document.createElement('td');
  	td.innerHTML = json[key]['text'];
  	tr.appendChild(td);

  	//vybrat si nahodne ktory vyskyt chcem
    // <a id="myLink" title="Click to do something" href="#" onclick="MyFunction();return false;">link text</a>
  	var word=json[key]['text'];
  	var sentenceId = json[key]['0'];
    currentlyDisplayed[key]['actual'] = 0;
    td = document.createElement('td');
  	
    var link = document.createElement('a');
    link.setAttribute('sentenceId', sentenceId);
    link.setAttribute('id', pack(word));
    link.setAttribute('title', 'Click to see in separate place');
    link.setAttribute('href', '#');
    link.setAttribute('onclick', 'getTextOfSentence('+sentenceId+', \''+pack(word)+'\');return false;');
    link.innerHTML = json[key]['sentence'];
    td.appendChild(link);
  	tr.appendChild(td);

  	td = document.createElement('td');
  	td.innerHTML = "dorob checklist";
  	td.innerHTML = '<button type="button" class="btn btn-default" onclick="setAsKnown(\''+pack(word)+'\')">už poznám</button>';
  	tr.appendChild(td);

  	table.appendChild(tr);
  }
  console.log(currentlyDisplayed);
}

function setAsKnown(word){
  word = unpack(word);
  var objekt = {'type':'setAsKnown', 'userId':userId, 'word':word};
  
  xhr = new XMLHttpRequest();
  var url = "http://localhost:4049";
  var method = "POST";
  xhr.open(method, url, true);
  xhr.onreadystatechange = function () { 
    if (xhr.readyState == 4 && xhr.status == 200) {
      var json = JSON.parse(xhr.responseText);
      //console.log(json);
      
      // TODO spracuj json
      if (json["answer"] == "yes"){
        var link = document.getElementById(pack(word));
        var tr = link.parentElement.parentElement;
        var table = document.getElementById("wordsTable");
        console.log(tr);
        console.log(table);
        table.removeChild(tr);
      } else {
        console.log("nieco sa pokazilo pri zaznaceni slovicka 'uz viem'");
      }
    }
  }

  var data = JSON.stringify(objekt);
  xhr.send(data);

}

function getTextOfSentence(id, word){
  word = unpack(word);
	var objekt = {'type':'getTextOfSentence', 'id':id};

  if (titles2 != null){
    objekt['secondLanguage'] = titles2;
    document.getElementById('secondLanguage').style.display = "inline";
  } else {
    document.getElementById('secondLanguage').style.display = "none";
  }

  console.log(objekt);

	xhr = new XMLHttpRequest();
  var url = "http://localhost:4049";
  var method = "POST";
  xhr.open(method, url, true);
  xhr.onreadystatechange = function () { 
    if (xhr.readyState == 4 && xhr.status == 200) {
      var json = JSON.parse(xhr.responseText);
      //console.log(json);
      
      // TODO spracuj json
      var td = document.getElementById(pack(word));
      td.innerHTML = json['answer'];

      var manipulatorDiv = document.getElementById('displayWord');
      manipulatorDiv.innerHTML="";

      var slovo = document.createElement('h3');
      slovo.innerHTML = word;
      manipulatorDiv.appendChild(slovo);
      
      var buttonPoznam = document.createElement('button');
      buttonPoznam.setAttribute('onclick', 'setAsKnown(\''+pack(word)+'\')');
      buttonPoznam.innerHTML = "už poznám";
      buttonPoznam.setAttribute('type', 'button');
      buttonPoznam.setAttribute('class', 'btn btn-default');
      manipulatorDiv.appendChild(buttonPoznam);

      var buttonInyVyskyt = document.createElement('button');
      buttonInyVyskyt.setAttribute('onclick', 'nextSentence(\''+pack(word)+'\')');
      buttonInyVyskyt.innerHTML = "ďalší výskyt";
      buttonInyVyskyt.setAttribute('type', 'button');
      buttonInyVyskyt.setAttribute('class', 'btn btn-default');
      manipulatorDiv.appendChild(buttonInyVyskyt);

      var el = document.getElementById('newLanguageSentence');
      el.innerHTML = json['answer'];
      el.setAttribute('poradove_cislo', json['poradove_cislo']);
      if ( titles2 != null ){
        el = document.getElementById('knownLanguageSentence');
        el.innerHTML = json['second'];
        el.setAttribute('poradove_cislo', json['pc2']);
      }
      
    }
  }

  var data = JSON.stringify(objekt);
  xhr.send(data);
}

function nextSentence(word){
  word = unpack(word);
  // TODO - niekedy sa este zasekne
  var key = null;
  for (var k in currentlyDisplayed){
    if (currentlyDisplayed[k]['text'] == word){
      key = k;
      break;
    }
  }

  console.log("key - teda id slova je "+key);

  if (key == null){
    return false;
  }

  var index = currentlyDisplayed[key]['actual'];
  console.log("prave zobrazena je "+index+". veta");
  index++;
  if (index == currentlyDisplayed[key]['sum'])
    index = 0;//znamena to ze je posledny vyskyt
  var id = currentlyDisplayed[key][index];

  getTextOfSentence(id, word);

  currentlyDisplayed[key]['actual']++;
}

function getSentence(poradove_cislo, titlesId, elementId){
  var objekt = {'type':'getSentence', 'poradove_cislo':poradove_cislo, 'titlesId':titlesId};
  console.log("get sentence "+poradove_cislo);

  xhr = new XMLHttpRequest();
  var url = "http://localhost:4049";
  var method = "POST";
  xhr.open(method, url, true);
  xhr.onreadystatechange = function () { 
    if (xhr.readyState == 4 && xhr.status == 200) {
      var json = JSON.parse(xhr.responseText);
      //console.log(json);
      
      var answer = json["answer"];
      console.log(json);
      if (answer == "yes"){
        console.log(json['sentence']);
        document.getElementById(elementId).innerHTML = json['sentence'];
        return json['sentence'];
      } else {
        console.log("nenasiel taku vetu");
        return "NIEKDE NASTAL PROBLEM";
      }

    }
  }

  var data = JSON.stringify(objekt);
  xhr.send(data);
}

function prevSentenceOriginal(){
  var element = document.getElementById('newLanguageSentence');
  var sentenceId = element.getAttribute('sentenceId');
  var pc = element.getAttribute('poradove_cislo');
  if (pc < 1){
    return;
  }
  if(pc == 1){
    element.setAttribute('poradove_cislo', 0);
    element.innerHTML = "...zaciatok filmu...";
    return;
  }
  pc = parseInt(pc) - 1;
  element.setAttribute('poradove_cislo', pc);
  getSentence(pc, titles1id, "newLanguageSentence");
}

function nextSentenceOriginal(){
  var element = document.getElementById('newLanguageSentence');
  var sentenceId = element.getAttribute('sentenceId');
  var pc = element.getAttribute('poradove_cislo');
  pc = 1 + parseInt(pc);
  element.setAttribute('poradove_cislo', pc);
  getSentence(pc, titles1id, "newLanguageSentence");
}

function prevSentenceTranslate(){
  var element = document.getElementById('knownLanguageSentence');
  var sentenceId = element.getAttribute('sentenceId');
  var pc = element.getAttribute('poradove_cislo');
  if (pc < 1){
    return;
  }
  if(pc == 1){
    element.setAttribute('poradove_cislo', 0);
    element.innerHTML = "...zaciatok filmu...";
    return;
  }
  pc = parseInt(pc) - 1;
  element.setAttribute('poradove_cislo', pc);
  getSentence(pc, titles2id, 'knownLanguageSentence');
}

function nextSentenceTranslate(){
  var element = document.getElementById('knownLanguageSentence');
  var sentenceId = element.getAttribute('sentenceId');
  var pc = element.getAttribute('poradove_cislo');
  pc = 1 + parseInt(pc);
  element.setAttribute('poradove_cislo', pc);
  getSentence(pc, titles2id, 'knownLanguageSentence');
}

function showLogged(){
	// skry polia login, registracia
	document.getElementById("login/register").style.display = "none";
  document.getElementById("uvod").style.display = "none";
  // ukaz tie "osobne"
	document.getElementById("logout").style.display = "inline";
	document.getElementById("loggedAs").innerHTML = user;
	createLanguagesForm();
	document.getElementById("konto").style.display = "inline";
  document.getElementById("chcemSaUcit").style.display = "inline";
  document.getElementById("navod").style.display = "inline";
}

function showUnlogged(){
	// skry tie osobne
	document.getElementById("logout").style.display = "none";
	document.getElementById("konto").style.display = "none";
  document.getElementById('tabulkaSlovicok').style.display = "none";
  document.getElementById("chcemSaUcit").style.display = "none";
  document.getElementById("navod").style.display = "none";
  // ukaz prihlas, registruj
	document.getElementById("login/register").style.display = "inline";
  document.getElementById("uvod").style.display = "inline";
}

function createLanguagesForm(){
	var d = document.getElementById('languages');
	d.innerHTML = "";
	var label, input;

	// zisti ake existuju jazyky
	var objekt = {'type':'getMyLanguages', 'userId':userId};

	//console.log("objekt:", objekt);

	xhr = new XMLHttpRequest();
  var url = "http://localhost:4049";
  var method = "POST";
  xhr.open(method, url, true);
  xhr.onreadystatechange = function () { 
    if (xhr.readyState == 4 && xhr.status == 200) {
      var json = JSON.parse(xhr.responseText);
      //console.log(json);

	    for (line in json){
		    if (!json.hasOwnProperty(line)){
		      continue;
		    }
		    //console.log(line, json[line]);

		    label = document.createElement('label');
		    input = document.createElement('input');
		    input.type = "checkbox";
		    input.id = json[line]["col2"];
		    input.className = "checkboxListLanguages";
		    input.setAttribute("languageId", json[line]["col0"]);
		    label.appendChild(input);
				label.innerHTML = label.innerHTML + json[line]["col1"];
		    
		    d.appendChild(label);
		    if (json[line]["checked"] == true){
		    	document.getElementById(json[line]["col2"]).checked = true;
		    }
 		    d.appendChild(document.createElement('br'));
		  }
    }
  }

  var data = JSON.stringify(objekt);
  xhr.send(data);

}

function pack(word){
  word = word.replace("'", "_");
  return word;
}

function unpack(word){
  word = word.replace("_", "'");
  return word;  
}