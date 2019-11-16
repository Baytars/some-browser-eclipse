package cn.gedobu.some.editor.med.grammar;

public class Grammar {
	public static final String[] keywords = new String [] {
		"每当",
		"如果",
		"否则",
		"类别",
		"定义"
	};
	
	public static final String[] terms = new String[] {
		"一般情况",
		"主诉",
		"现病史",
		"既往史",
		"月经史",
		"系统回顾",
		"个人史",
		"家族史",
		"Na<sup>+</sup>",
		"K<sup>+</sup>",
		"Cl<sup>-</sup>"
	};
	
	public static char[] termsPrefixes;
	
	public static char[] getTermsPrefixes() {
		if ( termsPrefixes == null ) {
			termsPrefixes = new char[terms.length];
			for ( int i=0; i < terms.length; i++ ) {
				termsPrefixes[i] = terms[i].charAt(0);
			}
		}
		return termsPrefixes;
	}
}
