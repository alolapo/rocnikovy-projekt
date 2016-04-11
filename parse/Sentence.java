package parse;

/*tu sa pod sentence mysli jedna replika: cislo, cas zobrazenia a jej text*/
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
		text = correctText(_text);
	}

	// stringy su obalene takymito " uvodzovkami,
	// preto vsetko vnutri je len s takymito '
	private String correctText(String text) {
		StringBuffer sb = new StringBuffer("");

		for (int i = 0; i < text.length(); i++) {
			if (text.charAt(i) == '\"') {
				sb.append('\'');
			} else {
				sb.append(text.charAt(i));
			}
		}

		return sb.toString();
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
