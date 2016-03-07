package parse;

import java.util.ArrayList;

class SimpleCombine implements CombineStrategy {

	@Override
	public ArrayList<Dvojica> combine(Titulky t1, Titulky t2) {
		ArrayList<Dvojica> tuples = new ArrayList<>();
		
		int length = Math.min(t1.repliky.size(), t2.repliky.size());
		for (int i=0; i<length; i++){
			tuples.add(new Dvojica(i, i));
		}
		
		return tuples;
	}

}
