package cn.gedobu.some.game.buttons;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import cn.gedobu.some.game.CardTabs;

public class ButtonBase extends Button {
	CardTabs cardTabs;
	
	public ButtonBase(Composite parent) {
		super(parent, SWT.PUSH);
	}
	
	public void bind(CardTabs cardTabs) {
		this.cardTabs = cardTabs;
	}

	@Override
	protected void checkSubclass() {
		
	}
}
