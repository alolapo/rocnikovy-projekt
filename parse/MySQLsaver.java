package parse;

import java.sql.*;

class MySQLsaver {
	boolean save() {
		Connection conn = null;

		try {
			// create our mysql database connection
			String myDriver = "org.gjt.mm.mysql.Driver";
			String myUrl = "jdbc:mysql://localhost/projekt";
			Class.forName(myDriver);
			conn = DriverManager.getConnection(myUrl, "root", "");

			// our SQL SELECT query.
			// if you only need a few columns, specify them by name instead of
			// using "*"
			String query = "SELECT * FROM Osoba";

			// create the java statement
			Statement st = conn.createStatement();

			// execute the query, and get a java resultset
			ResultSet rs = st.executeQuery(query);

			// iterate through the java resultset
			while (rs.next()) {
				int id = rs.getInt("id");
				String meno = rs.getString("meno");
				String heslo = rs.getString("heslo");

				// print the results
				System.out.format("%s, %s, %s\n", id, meno, heslo);
			}
			st.close();
		} catch (Exception e) {
			System.err.println("Got an exception! ");
			System.err.println(e.getMessage());
		}

		return false;
	}
}
