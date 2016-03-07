package parse;

import java.util.ArrayList;

interface CombineStrategy {
	ArrayList<Dvojica> combine(Titulky t1, Titulky t2);
}
