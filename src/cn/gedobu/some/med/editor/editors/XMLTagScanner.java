package cn.gedobu.some.med.editor.editors;

import org.eclipse.jface.text.*;
import org.eclipse.jface.text.rules.*;

public class XMLTagScanner extends RuleBasedScanner {

	public XMLTagScanner(ColorManager manager) {
		// this is coloring rule with a starting tag
		
		IToken string = new Token(new TextAttribute(manager.getColor(IXMLColorConstants.STRING)));
		Token comment = new Token(new TextAttribute(manager.getColor(IXMLColorConstants.XML_COMMENT)));

		IRule[] rules = new IRule[] {
			// Add rule for double quotes
			new SingleLineRule("\"", "\"", string, '\\'),
			// Add a rule for single quotes
			new SingleLineRule("'", "'", comment, '\\'),
			new SingleLineRule("ma", "ce", comment, '\\'),
			new SingleLineRule("现病史", "诉", comment, '\\'),
			
			new PatternRule("//", null, comment, (char)0, true),
			new PatternRule("现病史", null, comment, (char) 0, true),
			// Add generic whitespace rule.
			new WhitespaceRule(new XMLWhitespaceDetector())
			
		};

		setRules(rules);
	}
}
