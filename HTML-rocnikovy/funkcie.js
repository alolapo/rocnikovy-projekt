var user, userId;

var filmId = 1;
var filmName = "Dirty dancing";
var titles1 = 3; // anglicky
var titles2 = 2; // cesky
//var titles2 = null;

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
	var objekt = {'type':'showMe', 'filmId':filmId, 'titles1':titles1, 'titles2':titles2, 'userId':userId};

	xhr = new XMLHttpRequest();
  var url = "http://localhost:4049";
  var method = "POST";
  xhr.open(method, url, true);
  xhr.onreadystatechange = function () { 
    if (xhr.readyState == 4 && xhr.status == 200) {
      var json = JSON.parse(xhr.responseText);
      //console.log(json);
      
      // TODO spracuj json
      fillTableFromJson(json);
      
    }
  }

  var data = JSON.stringify(objekt);
  xhr.send(data);

}

function fillTableFromJson(json){
	var table = document.getElementById("wordsTable");

	// TODO vymaz vsetky deti, spracuj zaznacene zmeny	
	table.innerHTML = "";

  var key, tr, td;
  for (key in json){
  	console.log(key);
  	tr = document.createElement('tr');

  	td = document.createElement('td');
  	td.innerHTML = json[key]['0'];
  	tr.appendChild(td);

  	//vybrat si nahodne ktory vyskyt chcem
  	var word=json[key]['0'];
  	var sentenceId = json[key]['1'];
  	td = document.createElement('td');
  	td.setAttribute('id', word); 
  	//getTextOfSentence(sentenceId, word);
  	tr.appendChild(td);

  	td = document.createElement('td');
  	td.innerHTML = "dorob checklist";
  	td.innerHTML = '<button type="button" onclick="getTextOfSentence('+sentenceId+', \''+word+'\')">daj mi vetu</button>';
  	tr.appendChild(td);

  	table.appendChild(tr);
  }

}

function getTextOfSentence(id, word){
	var objekt = {'type':'getTextOfSentence', 'id':id, 'secondLanguage': titles2};
  if (titles2 != null){
    objekt['secondLanguage'] = titles2;
    document.getElementById('secondLanguage').style.display = "inline";
  } else {
    document.getElementById('secondLanguage').style.display = "none";
  }

	xhr = new XMLHttpRequest();
  var url = "http://localhost:4049";
  var method = "POST";
  xhr.open(method, url, true);
  xhr.onreadystatechange = function () { 
    if (xhr.readyState == 4 && xhr.status == 200) {
      var json = JSON.parse(xhr.responseText);
      //console.log(json);
      
      // TODO spracuj json
      var td = document.getElementById(word);
      td.innerHTML = json['answer'];
      document.getElementById('displayWord').innerHTML = word;
      document.getElementById('newLanguageSentence').innerHTML = json['answer'];
      if ( titles2 != null ){
        document.getElementById('knownLanguageSentence').innerHTML = json['second'];
      }
      
    }
  }

  var data = JSON.stringify(objekt);
  xhr.send(data);
  
  //var td = document.getElementById(word);
  //td.innerHTML = "nieco";
}

function giveMeSomething(){
  var objekt = {'type':"something"};
  xhr = new XMLHttpRequest();
  var url = "http://localhost:4049";
  var method = "POST";
  xhr.open(method, url, true);
  xhr.onreadystatechange = function () { 
    if (xhr.readyState == 4 && xhr.status == 200) {
      var json = JSON.parse(xhr.responseText);
      //console.log(json);
      
      // TODO spracuj json
      var td = document.getElementById("someAnswer");
      td.innerHTML = json['0']['col0']+" "+json['0']['col1']+" "+json['0']['col2'];
    }
  }

  var data = JSON.stringify(objekt);
  xhr.send(data);
}

function showLogged(){
	// TODO skry polia login, registracia
	document.getElementById("login").style.display = "none";
	document.getElementById("register").style.display = "none";
	// ukaz tie "osobne"
	document.getElementById("logout").style.display = "inline";
	document.getElementById("loggedAs").innerHTML = user;
	createLanguagesForm();
	document.getElementById("konto").style.display = "inline";
}

function showUnlogged(){
	// TODO skry tie osobne
	document.getElementById("logout").style.display = "none";
	document.getElementById("konto").style.display = "none";
	// ukaz prihlas, registruj
	document.getElementById("login").style.display = "inline";
	document.getElementById("register").style.display = "inline";
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
