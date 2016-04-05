package parse;

class Sentence {
	int serialNum;
	String from;
	String to;
	String text;

	// konstruktor
	Sentence(int _serialNum, String _from, String _to, String _text) {
		serialNum = _serialNum;
		from = _from;
		to = _to;
		text = _text;
	}

	// vypis
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(serialNum);
		sb.append(": ");
		sb.append(from);
		sb.append(" --> ");
		sb.append(to);
		sb.append(" ");
		sb.append(text);

		return sb.toString();
	}
}
