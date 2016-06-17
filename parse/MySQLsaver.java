package parse;

import java.sql.*;
import java.util.*;

class MySQLsaver {
	static Connection conn = null;
	static String myDriver, myUrl;
	final int NumOfRows = 500;

	/**
	 * Creates default connection Creates default connection to project database
	 * with name and password to the database on the server. If true, connection
	 * is created.
	 * 
	 * @return true if connection is successful
	 */
	boolean createConnection() {
		try {
			// create our mysql database connection
			myDriver = "org.gjt.mm.mysql.Driver";
			myUrl = "jdbc:mysql://localhost/project?useUnicode=true&characterEncoding=UTF-8";
			Class.forName(myDriver);
			conn = DriverManager.getConnection(myUrl, "root", "");

			setEncoding();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	// SET CHARACTER SET 'utf8'
	boolean setEncoding() {
		try {
			Statement st = conn.createStatement();
			StringBuilder query = new StringBuilder("SET CHARACTER SET 'utf8'");
			st.executeUpdate(query.toString());
			st.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	boolean closeConnection() {
		try {
			conn.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Saves sentences to the database. Takes sentences and id of titles, saves
	 * sentences to the database. Returns offset of first item of given list.
	 * 
	 * @param sentences
	 * @param titlesId
	 * @return index of first item of given list in database
	 */
	public int saveSentences(List<Sentence> sentences, int titlesId) {
		System.out.println("save sentences");
		// find out max index
		int offset = 0;

		try {
			String query = "SELECT max(id) AS id FROM Sentence";
			PreparedStatement st = conn.prepareStatement(query);
			ResultSet rs = st.executeQuery();

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

		System.out.println("got offset for sentences: "+offset);
		
		try {
			String query = "INSERT INTO Sentence VALUES (?, ?, ?, ?, ?, ?)";
			PreparedStatement st = conn.prepareStatement(query);

			int i = 0;
			for (Sentence s : sentences) {
				st.setInt(1, (offset + i));
				st.setInt(2, titlesId);
				st.setInt(3, s.serialNum);
				st.setString(4, s.from);
				st.setString(5, s.to);
				st.setString(6, s.text);

				st.addBatch();
				i++;

				// priebezne ulozenie
				if (i % NumOfRows == 0 || i == sentences.size()) {
					st.executeBatch();
					System.out.println("save part of sentences by the time");
				}
			}
			st.close();
			return offset;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	/**
	 * Saves given pairs of tuples to the table Pair
	 * 
	 * @param tuples
	 * @return 0 on success, -1 on fail
	 */
	public int saveTuples(List<Pair> tuples) {
		System.out.println("save pairs sentence1-sentence2");
		try {
			String query = "INSERT INTO Pair VALUES (?, ?)";
			PreparedStatement st = conn.prepareStatement(query);

			int i = 0;
			for (Pair p : tuples) {
				st.setInt(1, p.sentence1);
				st.setInt(2, p.sentence2);
				st.addBatch();
				i++;

				st.setInt(1, p.sentence2);
				st.setInt(2, p.sentence1);
				st.addBatch();
				i++;

				// priebezne ulozenie
				if (i % NumOfRows == 0 || i % NumOfRows == 1
						|| i == tuples.size()) {
					st.executeBatch();
					System.out.println("save part of pairs by the time");
				}

			}
			st.close();
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	public int saveCombined(int t1, int t2) {
		System.out.println("save combined");
		try {
			String query = "INSERT INTO Combined VALUES (?, ?)";
			PreparedStatement st = conn.prepareStatement(query);

			st.setInt(1, t1);
			st.setInt(2, t2);
			st.addBatch();

			st.setInt(1, t2);
			st.setInt(2, t1);
			st.addBatch();

			st.executeBatch();
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
	 * 
	 * @param sentences
	 * @param language
	 * @return
	 */
	public int saveWords(List<Sentence> sentences, int sentenceOffset,
			int language) {
		System.out.println("save words");
		// find out max index
		int offset = 0;

		Map<String, List<Integer>> words = countWords(sentences, sentenceOffset);
		// nie je to pair sentence-sentence ale word-sentence
		List<Pair> contains = new ArrayList<>();

		try {
			String query = "SELECT max(id) AS id FROM Word";
			PreparedStatement st = conn.prepareStatement(query);
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

		System.out.println("got offset for words: "+offset);
		
		try {
			String query = "INSERT INTO Word VALUES (?, ?, ?)";
			PreparedStatement st = conn.prepareStatement(query);

			int i = 0;
			for (String word : words.keySet()) {
				// int value = words.get(word);

				st.setInt(1, (i + offset));
				st.setInt(2, language);
				st.setString(3, word);
				st.addBatch();

				for (int x : words.get(word)) {
					contains.add(new Pair(i + offset, x));
				}
				i++;
				
				// priebezne ulozenie
				if (i % NumOfRows == 0 || i == words.size()) {
					System.out.println("start executing");
					st.executeBatch();
					System.out.println("saved part of words by the time");
				}
			}
			st.close();
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}

		return saveContains(contains);
	}

	public int saveContains(List<Pair> contains){
		System.out.println("save contains");
		
		String q = null;
		// uloz "contains" do db - contains nie je uplne vhodne zvolena
		// struktura ako pair
		try {
			String query = "INSERT INTO Contains VALUES (?, ?)";
			PreparedStatement st = conn.prepareStatement(query);
			q = st.toString();

			int i = 0;
			for (Pair p : contains) {
				st.setInt(1, p.sentence1);
				st.setInt(2, p.sentence2);
				st.addBatch();
				i++;

				// priebezne ulozenie
				if (i % NumOfRows == 0 || i == contains.size()) {
					//System.out.println(i+" from "+contains.size());
					System.out.println("start executing");
					st.executeBatch();
					System.out.println("saved part of contains by the time");
				}
			}
			st.close();
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Query: "+q);
			return -1;
		}
	}
	
	/**
	 * Counts number of each word in list of given sentences This function takes
	 * "word" as text between two spaces
	 * 
	 * @param sentences
	 * @return map of values {word: number}, where number is number of words in
	 *         whole list
	 */
	private Map<String, List<Integer>> countWords(List<Sentence> sentences,
			int offset) {
		System.out.println("count words");
		Map<String, List<Integer>> words = new HashMap<>();

		for (int j = 0; j < sentences.size(); j++) {
			Sentence s = sentences.get(j);
			String[] w = s.text.split(" ");
			for (int i = 0; i < w.length; i++) {
				String word = simplify(w[i]);
				if (!words.containsKey(word)) {
					ArrayList<Integer> a = new ArrayList<>();
					words.put(word, a);
				}
				words.get(word).add(j + offset);
			}
		}

		for (String w : words.keySet()) {
			// tu si skontrolujem ci mi to vypisalo dobre hodnoty
			if (words.get(w).size() > 5) {
				System.out.print(w + ", ");
			}
		}
		
		System.out.println();
		System.out.println("words counted");
		return words;
	}

	String simplify(String s) {
		if (s.length() <= 0)
			return s;
		StringBuffer sb = new StringBuffer(s.toLowerCase());
		if (sb.length() > 0 && sb.charAt(sb.length() - 1) == '?'
				|| sb.charAt(sb.length() - 1) == '.'
				|| sb.charAt(sb.length() - 1) == ',') {
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}

}
