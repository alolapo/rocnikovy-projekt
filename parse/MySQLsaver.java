package parse;

import java.sql.*;
import java.util.*;

class MySQLsaver {
	static Connection conn = null;
	final int NumOfRows = 100;

	boolean save() {

		try {
			// create our mysql database connection
			String myDriver = "org.gjt.mm.mysql.Driver";
			String myUrl = "jdbc:mysql://localhost/project";
			Class.forName(myDriver);
			conn = DriverManager.getConnection(myUrl, "root", "");

			// our SQL SELECT query.
			// if you only need a few columns, specify them by name instead of
			// using "*"
			String query = "SELECT * FROM User";

			// create the java statement
			Statement st = conn.createStatement();

			// execute the query, and get a java resultset
			ResultSet rs = st.executeQuery(query);

			// iterate through the java resultset
			while (rs.next()) {
				int id = rs.getInt("id");
				String meno = rs.getString("name");
				String heslo = rs.getString("pasw");

				// print the results
				System.out.format("%s, %s, %s\n", id, meno, heslo);
			}
			st.close();
			return true;
		} catch (Exception e) {
			System.err.println("Got an exception! ");
			System.err.println(e.getMessage());
			return false;
		}
	}

	// ulozi vety a vrati offset, odkial su ukladane v db
	public int saveSentences(ArrayList<Sentence> sentences, int titlesId) {
		// TODO Auto-generated method stub
		String myDriver = "org.gjt.mm.mysql.Driver";
		String myUrl = "jdbc:mysql://localhost/project";

		// find out max index
		int offset = 0;

		try {
			Class.forName(myDriver);
			conn = DriverManager.getConnection(myUrl, "root", "");

			String query = "SELECT max(id) AS id FROM Sentence";
			// create the java statement
			Statement st = conn.createStatement();
			// execute the query, and get a java resultset
			ResultSet rs = st.executeQuery(query);

			// iterate through the java resultset
			while (rs.next()) {
				int id = rs.getInt("id");
				offset = id + 1;

				// print the results
				// System.out.format("%s\n", id);
			}
			st.close();
			// return true;
		} catch (Exception e) {
			System.err.println("Got an exception! ");
			System.err.println(e.getMessage());
			return -1;
		}
		;

		try {
			Class.forName(myDriver);
			conn = DriverManager.getConnection(myUrl, "root", "");

			// create the java statement
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

					String q = query.toString();
					// System.out.println(q);
					st.executeUpdate(q);
					query = new StringBuilder("INSERT INTO Sentence VALUES");

				}

			}
			query.append(";");

			String q = query.toString();
			// System.out.println(q);

			// execute the query
			st.executeUpdate(q);

			st.close();
			return offset;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
	}

	// ulozi pary podla ich id v db
	// return 0 uspesne, inak return -1 -> je toto OK?
	public int saveTuples(ArrayList<Pair> tuples) {
		// TODO Auto-generated method stub
		String myDriver = "org.gjt.mm.mysql.Driver";
		String myUrl = "jdbc:mysql://localhost/project";

		try {
			Class.forName(myDriver);
			conn = DriverManager.getConnection(myUrl, "root", "");

			// create the java statement
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

					String q = query.toString();
					// System.out.println(q);
					st.executeUpdate(q);
					query = new StringBuilder("INSERT INTO Pair VALUES");

				}

			}
			query.append(";");

			String q = query.toString();
			// System.out.println(q);

			// execute the query
			st.executeUpdate(q);

			st.close();
			return 0;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
	}

	// ulozi slova v danom jazyku
	// toto nefunguje dobre, lebo do db nahadze duplicitne zaznamy
	// mozno by bolo fajn v prvom try bloku vymazat vsetky kluce z words
	// ktore su uz v db
	public int saveWords(ArrayList<Sentence> sentences, int language) {
		String myDriver = "org.gjt.mm.mysql.Driver";
		String myUrl = "jdbc:mysql://localhost/project";

		// find out max index
		int offset = 0;

		// tu by bolo asi fajn zistit ze ake slova sa tam nachadzaju a spocitat
		// ich
		Map<String, Integer> words = countWords(sentences);

		try {
			Class.forName(myDriver);
			conn = DriverManager.getConnection(myUrl, "root", "");

			String query = "SELECT max(id) AS id FROM Word";
			// create the java statement
			Statement st = conn.createStatement();
			// execute the query, and get a java resultset
			ResultSet rs = st.executeQuery(query);

			// iterate through the java resultset
			while (rs.next()) {
				int id = rs.getInt("id");
				offset = id + 1;

				// print the results
				// System.out.format("%s\n", id);
			}
			st.close();
			// return true;
		} catch (Exception e) {
			System.err.println("Got an exception! ");
			System.err.println(e.getMessage());
			return -1;
		}
		;

		try {
			Class.forName(myDriver);
			conn = DriverManager.getConnection(myUrl, "root", "");

			// create the java statement
			Statement st = conn.createStatement();

			StringBuilder query = new StringBuilder("INSERT INTO Word VALUES");
			int i = 0;
			for (String word : words.keySet()) {
				int value = words.get(word);
				// mozno sa oplati tam vkladat slova s rozumnym poctom vyskytov,
				// napr aspon 5?
				// ak ano, nebude to robit problem, ked budem to slovo hladat v
				// db?
				if (i % NumOfRows > 0)
					query.append(", ");
				query.append("(");

				query.append((i + offset) + "," + language + ",\"" + word
						+ "\"");

				query.append(")");

				// priebezne ulozenie
				if (i % NumOfRows == NumOfRows - 1) {
					query.append(";");

					String q = query.toString();
					// System.out.println(q);
					st.executeUpdate(q);
					query = new StringBuilder("INSERT INTO Word VALUES");
				}
				i++;
			}
			query.append(";");

			String q = query.toString();
			// System.out.println(q);

			// execute the query
			st.executeUpdate(q);

			st.close();
			return 0;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
	}

	// spocita, kolkokrat sa tam ktore slovo nachadza
	// pre jednoduchost je slovo oddelene medzerami
	private Map<String, Integer> countWords(ArrayList<Sentence> sentences) {
		Map<String, Integer> words = new HashMap<>();

		for (Sentence s : sentences) {
			String[] w = s.text.split(" ");
			for (int i = 0; i < w.length; i++) {
				if (words.containsKey(w[i])) {
					int n = words.get(w[i]);
					words.remove(w[i]);
					words.put(w[i], n + 1);
				} else {
					words.put(w[i], 1);
				}
			}
		}

		return words;
	}

}
