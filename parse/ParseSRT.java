package parse;

import java.io.*;
import java.util.ArrayList;

class ParseSRT implements ParseStrategy {

	@Override
	public ArrayList<Replika> parseFile(String meno_suboru) {
		ArrayList<Replika> repliky = new ArrayList<>();
		BufferedReader in = null;
		
		try {
			in = new BufferedReader(new FileReader("subs/"+meno_suboru));
			
			while (true) {
				String line;
				// nacitame riadok do retazca, malo by to byt cislo repliky
	            line = in.readLine();
	            // skoncime, ked uzivatel zada prazdny riadok alebo ked prideme na koniec vstupu (null)
	            if (line == null || line.equals("")) { 
	                break;
	            }
	            
	            int serialNum = Integer.parseInt(line);
	            line = in.readLine();
	            String[] times = line.split(" ");
	            String from = times[0];
	            String to = times[2];
	            
	            StringBuffer text = new StringBuffer("");
	            while(true){
	            	line = in.readLine();
		            // skoncime, ked sa objavi prazdny riadok alebo ked prideme na koniec vstupu (null)
		            if (line == null || line.equals("")) { 
		                break;
		            }
		            //System.out.println(line);
		            text.append(line);		            
	            }
				
	            repliky.add( new Replika(serialNum, from, to, text.toString()) );
				
	            
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
        
		return repliky;
	}

}
