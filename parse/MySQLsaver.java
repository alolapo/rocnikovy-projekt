package parse;

import java.sql.*;
import java.util.*;

class MySQLsaver {
	static Connection conn = null;
	static String myDriver, myUrl;
	final int NumOfRows = 100;

/**
 * Creates default connection
 * Creates default connection to project database with name and password to the database on the server. If true, connection is created.
 * @return true if connection is successful
 */
	boolean createConnection(){
		try {
			// create our mysql database connection
			myDriver = "org.gjt.mm.mysql.Driver";
			myUrl = "jdbc:mysql://localhost/project";
			Class.forName(myDriver);
			conn = DriverManager.getConnection(myUrl, "root", "");
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	boolean closeConnection(){
		try {
			conn.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Saves sentences to the database.
	 * Takes sentences and id of titles, saves sentences to the database. Returns offset of first item of given list.
	 * @param sentences
	 * @param titlesId
	 * @return index of first item of given list in database
	 */
	public int saveSentences(List<Sentence> sentences, int titlesId) {
		// find out max index
		int offset = 0;

		try {
			String query = "SELECT max(id) AS id FROM Sentence";
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);

			// iterate through the java resultset
			while (rs.next()) {
				int id = rs.getInt("id");
				offset = id + 1;
			}
			st.close();
		} catch (Exception e) {
			System.err.println("Got an exception! ");
			System.err.println(e.getMessage());
			return -1;
		}
		;

		try {
			Statement st = conn.createStatement();

			StringBuilder query = new StringBuilder(
					"INSERT INTO Sentence VALUES");
			for (int i = 0; i < sentences.size(); i++) {
				Sentence s = sentences.get(i);
				if (i % NumOfRows > 0)
					query.append(", ");
				query.append("(");

				// stringy su obalene takymito " uvodzovkami,
				// preto vsetko vnutri je len s takymito '
				query.append((offset + i) + "," + titlesId + "," + s.serialNum
						+ ",\"" + s.from + "\",\"" + s.to + "\",\"" + s.text
						+ "\"");
				
				query.append(")");

				// priebezne ulozenie
				if (i % NumOfRows == NumOfRows - 1) {
					query.append(";");
					st.executeUpdate(query.toString());
					query = new StringBuilder("INSERT INTO Sentence VALUES");
				}

			}
			query.append(";");
			st.executeUpdate(query.toString());
			st.close();
			return offset;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	/**
	 * Saves given pairs of tuples to the table Pair
	 * @param tuples
	 * @return 0 on success, -1 on fail
	 */
	public int saveTuples(List<Pair> tuples) {
		try {
			Statement st = conn.createStatement();

			StringBuilder query = new StringBuilder("INSERT INTO Pair VALUES");
			for (int i = 0; i < tuples.size(); i++) {
				Pair p = tuples.get(i);
				if (i % NumOfRows > 0)
					query.append(", ");
				query.append("(");

				query.append(p.sentence1 + "," + p.sentence2);

				query.append(")");

				// priebezne ulozenie
				if (i % NumOfRows == NumOfRows - 1) {
					query.append(";");
					st.executeUpdate(query.toString());
					query = new StringBuilder("INSERT INTO Pair VALUES");

				}

			}
			query.append(";");
			st.executeUpdate(query.toString());
			st.close();
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	public int saveCombined(int t1, int t2) {
		try {
			Statement st = conn.createStatement();
			StringBuilder query = new StringBuilder("INSERT INTO Combined VALUES ("+t1+","+t2+"),("+t2+","+t1+");");
			st.executeUpdate(query.toString());
			st.close();
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	// ulozi slova v danom jazyku
	// toto nefunguje dobre, lebo do db nahadze duplicitne zaznamy
	// mozno by bolo fajn v prvom try bloku vymazat vsetky kluce z words
	// ktore su uz v db
    /**
     * Cuts every word from given list of sentences and saves to table Word
     * @param sentences
     * @param language
     * @return
     */
	public int saveWords(List<Sentence> sentences, int sentenceOffset, int language) {
		// find out max index
		int offset = 0;

		Map<String, List<Integer>> words = countWords(sentences, sentenceOffset);
		// nie je to pair sentence-sentence ale word-sentence
		List<Pair> contains = new ArrayList<>();

		try {
			String query = "SELECT max(id) AS id FROM Word";
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);

			// iterate through the java resultset
			while (rs.next()) {
				int id = rs.getInt("id");
				offset = id + 1;
			}
			st.close();
		} catch (Exception e) {
			System.err.println("Got an exception! ");
			System.err.println(e.getMessage());
			return -1;
		}
		;

		try {
			Statement st = conn.createStatement();

			StringBuilder query = new StringBuilder("INSERT INTO Word VALUES");
			int i = 0;
			for (String word : words.keySet()) {
				//int value = words.get(word);
				if (i % NumOfRows > 0)
					query.append(", ");
				query.append("(");

				query.append((i + offset) + "," + language + ",\"" + word
						+ "\"");

				query.append(")");

				// TODO skontroluj
				for (int x : words.get(word)){
					contains.add(new Pair(i + offset, x));
				}
				
				// priebezne ulozenie
				if (i % NumOfRows == NumOfRows - 1) {
					query.append(";");
					st.executeUpdate(query.toString());
					query = new StringBuilder("INSERT INTO Word VALUES");
				}
				i++;
			}
			query.append(";");
			st.executeUpdate(query.toString());
			st.close();
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
		
		// uloz "contains" do db
		try {
			Statement st = conn.createStatement();

			StringBuilder query = new StringBuilder("INSERT INTO Contains VALUES");
			
			int i = 0;
			for (Pair p : contains){
				if (i % NumOfRows > 0)
					query.append(", ");
				query.append("("+p.sentence1 + "," + p.sentence2+")");

				// priebezne ulozenie
				if (i % NumOfRows == NumOfRows - 1) {
					query.append(";");
					st.executeUpdate(query.toString());
					query = new StringBuilder("INSERT INTO Contains VALUES");
				}
				i++;
			}
			query.append(";");
			st.executeUpdate(query.toString()); // moze byt problem, ak tu bude "INSERT INTO Contains VALUES;"
			st.close();
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	/**
	 * Counts number of each word in list of given sentences
	 * This function takes "word" as text between two spaces
	 * @param sentences
	 * @return map of values {word: number}, where number is number of words in whole list
	 */
	private Map<String, List<Integer>> countWords(List<Sentence> sentences, int offset) {
		Map<String, List<Integer>> words = new HashMap<>();

		for (int j = 0; j < sentences.size(); j++) {
			Sentence s = sentences.get(j);
			String[] w = s.text.split(" ");
			for (int i = 0; i < w.length; i++) {
				String word = simplify(w[i]);
				if (! words.containsKey(word)) {
					ArrayList<Integer> a = new ArrayList<>();
					words.put(word, a);
				}
				words.get(word).add(j + offset);
			}
		}

		for (String w : words.keySet()){
			//tu si skontrolujem ci mi to vypisalo dobre hodnoty
			if (words.get(w).size() > 5){
				System.out.print(w + ", ");
			}
		}
		
		return words;
	}
	
	String simplify (String s){
		if (s.length()<=0)
			return s;
		StringBuffer sb = new StringBuffer(s.toLowerCase());
		if (sb.length() > 0 && sb.charAt(sb.length() - 1) == '?' || sb.charAt(sb.length() - 1) == '.' || sb.charAt(sb.length() - 1) == ','){
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}

}
