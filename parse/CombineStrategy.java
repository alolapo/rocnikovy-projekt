package parse;

import java.util.*;

interface CombineStrategy {
	List<Pair> combine(Titles t1, int offset1, Titles t2, int offset2);
}
