package parse;

import java.util.ArrayList;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Titulky t1 = new Titulky();
		t1.meno_suboru = "dirty.dancing-cze.srt";
		
		Titulky t2 = new Titulky();
		t2.meno_suboru = "dirty-dancing-english.srt";
		
		// teraz natvrdo SRT, casom akysi case
		ParseStrategy parse = new ParseSRT();
		
		t1.repliky = parse.parseFile(t1.meno_suboru);
		System.out.println(t1.repliky.get(8).toString());
		
		t2.repliky = parse.parseFile(t2.meno_suboru);
		System.out.println(t2.repliky.get(6).toString());
		
		// teraz natvrdo simple- teda podla cisel
		CombineStrategy combine = new SimpleCombine();
		
		ArrayList<Dvojica> tuples = combine.combine(t1, t2);
		System.out.println( t1.repliky.get( tuples.get(40).replika1 ).text );
		System.out.println( t2.repliky.get( tuples.get(40).replika2 ).text );
		
		/*
		 * tu treba ulozit vsetky info do db:
		 * titulky → t1, t2
		 * Replika → t1.repliky, t2.repliky
		 * Dvojica → tuples
		 * Slovo - replika ...toto este nikde nemam spracovane
		 */
		
		MySQLsaver saver = new MySQLsaver();
		saver.save();
		
	}

}
