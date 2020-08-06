package cn.gedobu.some.game;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import cn.gedobu.some.game.buttons.ButtonBase;
import cn.gedobu.some.game.buttons.SignInButton;
import cn.gedobu.some.game.buttons.SummonButton;

public class SoGaWindow {
	Composite parent;
	CardTabs cardTabs;
	ArrayList<ButtonBase> buttons = new ArrayList<>();
	
	public SoGaWindow(Composite parent) {
		this.parent = parent;
		GridLayout gridLayout = new GridLayout();
		this.parent.setLayout(gridLayout);
		
		addButton(new SignInButton(parent));
		addButton(new SummonButton(parent));
		addTabFolder();
	}
	
	private void addButton(ButtonBase btn) {
		buttons.add(btn);
	}
	
	private void addTabFolder() {
		this.cardTabs = new CardTabs(parent, SWT.BORDER);
		
		for (ButtonBase btn : buttons) {
			btn.bind(cardTabs);
		}
	}
}
