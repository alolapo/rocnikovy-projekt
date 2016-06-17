package parse;

import java.nio.charset.Charset;
import java.util.*;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(Charset.defaultCharset());
		
		// TODO cases
		// titulky budu nacitane z db, zatial natvrdo vytvorene
		Titles t1 = new Titles();
		t1.fileName = "dirty.dancing-cze.srt";
		t1.language = 2; // teraz CZ
		t1.titlesId = 1;

		Titles t2 = new Titles();
		t2.fileName = "dirty-dancing-english.srt";
		t2.language = 3; // teraz EN
		t2.titlesId = 2;

		MySQLsaver saver = new MySQLsaver();
		saver.createConnection();
		
		// teraz natvrdo SRT, casom akysi case
		ParseStrategy parse = new ParseSRT();

		t1.sentences = parse.parseFile(t1.fileName);
		int offset1 = saver.saveSentences(t1.sentences, t1.titlesId);
		// ak je offset -1, neulozilo sa to spravne
		System.out.println(offset1);
		saver.saveWords(t1.sentences, offset1, t1.language);

		t2.sentences = parse.parseFile(t2.fileName);
		int offset2 = saver.saveSentences(t2.sentences, t2.titlesId);
		// ak je offset -1, neulozilo sa to spravne
		System.out.println(offset2);
		saver.saveWords(t2.sentences, offset2, t2.language);

		// teraz natvrdo simple- teda podla cisel
		CombineStrategy combine = new SimpleCombine();

		List<Pair> tuples = combine.combine(t1, offset1, t2, offset2);
		saver.saveTuples(tuples);
		saver.saveCombined(t1.titlesId, t2.titlesId);
		
		System.out.println();
		
		saver.closeConnection();	
	}

}
