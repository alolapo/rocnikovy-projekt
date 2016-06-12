package parse;

import java.util.*;

class SimpleCombine implements CombineStrategy {

	@Override
	public List<Pair> combine(Titles t1, int offset1, Titles t2,
			int offset2) {
		ArrayList<Pair> tuples = new ArrayList<>();

		int length = Math.min(t1.sentences.size(), t2.sentences.size());
		for (int i = 0; i < length; i++) {
			tuples.add(new Pair(i + offset1, i + offset2));
		}

		return tuples;
	}

}
