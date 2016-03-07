package parse;

import java.util.ArrayList;

interface ParseStrategy {
	ArrayList<Replika> parseFile(String meno_suboru);
}
