package parse;

import java.util.ArrayList;

interface CombineStrategy {
	ArrayList<Pair> combine(Titles t1, int offset1, Titles t2, int offset2);
}
