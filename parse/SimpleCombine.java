package parse;

import java.util.ArrayList;

class SimpleCombine implements CombineStrategy {

	@Override
	public ArrayList<Pair> combine(Titles t1, Titles t2) {
		ArrayList<Pair> tuples = new ArrayList<>();

		int length = Math.min(t1.repliky.size(), t2.repliky.size());
		for (int i = 0; i < length; i++) {
			tuples.add(new Pair(i, i));
		}

		return tuples;
	}

}
