package parse;

import java.util.ArrayList;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// titulky budu nacitane z db, zatial natvrdo vytvorene
		Titles t1 = new Titles();
		t1.fileName = "dirty.dancing-cze.srt";
		t1.language = 1; // teraz CZ

		Titles t2 = new Titles();
		t2.fileName = "dirty-dancing-english.srt";
		t2.language = 2; // teraz EN

		MySQLsaver saver = new MySQLsaver();
		// teraz natvrdo SRT, casom akysi case
		ParseStrategy parse = new ParseSRT();

		t1.sentences = parse.parseFile(t1.fileName);
		// System.out.println(t1.sentences.get(8).toString());
		int offset1 = saver.saveSentences(t1.sentences, t1.titlesId);
		// ak je offset -1, neulozilo sa to spravne
		System.out.println(offset1);
		saver.saveWords(t1.sentences, t1.language);

		t2.sentences = parse.parseFile(t2.fileName);
		// System.out.println(t2.sentences.get(6).toString());
		int offset2 = saver.saveSentences(t2.sentences, t2.titlesId);
		// ak je offset -1, neulozilo sa to spravne
		System.out.println(offset2);
		saver.saveWords(t2.sentences, t2.language);

		// teraz natvrdo simple- teda podla cisel
		CombineStrategy combine = new SimpleCombine();

		ArrayList<Pair> tuples = combine.combine(t1, offset1, t2, offset2);
		saver.saveTuples(tuples);
		/*
		 * System.out.println(t1.sentences.get(tuples.get(40).sentence1).text);
		 * System.out.println(t2.sentences.get(tuples.get(40).sentence2).text);
		 */

		/*
		 * tu treba ulozit vsetky info do db: titulky → t1, t2 Replika →
		 * t1.repliky, t2.repliky Dvojica → tuples Slovo - replika ...toto este
		 * nikde nemam spracovane
		 */

		saver.save();
	}

}
