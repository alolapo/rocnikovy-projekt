package parse;

import java.util.ArrayList;

interface CombineStrategy {
	ArrayList<Pair> combine(Titles t1, Titles t2);
}
