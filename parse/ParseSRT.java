package parse;

import java.io.*;
import java.util.*;

class ParseSRT implements ParseStrategy {

	@Override
	public List<Sentence> parseFile(String fileName) {
		List<Sentence> sentences = new ArrayList<>();
		BufferedReader in = null;
		//InputStreamReader in = null;

		try {
			//in = new BufferedReader(new FileReader("subs/" + fileName));
			in = new BufferedReader( new InputStreamReader(new FileInputStream("subs/" + fileName), "windows-1250"));

			while (true) {
				String line;
				// nacitame riadok do retazca, malo by to byt cislo repliky
				line = in.readLine();
				
				// skoncime, ked uzivatel zada prazdny riadok alebo ked prideme
				// na koniec vstupu (null)
				if (line == null || line.equals("")) {
					break;
				}

				int serialNum = Integer.parseInt(line);
				line = in.readLine();
				String[] times = line.split(" ");
				String from = times[0];
				String to = times[2];

				StringBuffer text = new StringBuffer("");
				while (true) {
					line = in.readLine();
					//System.out.println(line);
					// skoncime, ked sa objavi prazdny riadok alebo ked prideme
					// na koniec vstupu (null)
					if (line.equals("") && text.length() == 0) {
						line = in.readLine();
					}
					if (line == null || line.equals("")) {
						break;
					}
					// System.out.println(line);
					text.append(line);
					text.append(" ");
				}

				sentences
						.add(new Sentence(serialNum, from, to, text.toString()));

			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return sentences;
	}

}
