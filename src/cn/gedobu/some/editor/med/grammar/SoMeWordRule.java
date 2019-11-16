package cn.gedobu.some.editor.med.grammar;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public class SoMeWordRule extends WordRule {
	private static final Color 深紫红 = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_MAGENTA);

	public SoMeWordRule() {
		super(new 探测器());
		
		for (String 词 : Grammar.keywords) {
			addWord(词, new Token(new TextAttribute(深紫红, null, SWT.BOLD)));
		}
	}
}
