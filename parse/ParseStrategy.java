package parse;

import java.util.*;

interface ParseStrategy {
	List<Sentence> parseFile(String fileName);
}
