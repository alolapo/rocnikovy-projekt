package parse;

import java.util.ArrayList;

interface ParseStrategy {
	ArrayList<Sentence> parseFile(String fileName);
}
