package cn.gedobu.some.game;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TabItem;

public class TabInfo extends TabItem {
	@Override
	protected void checkSubclass() {
		
	}

	public TabInfo(CardTabs parent, int style) {
		super(parent, style);
		this.setText("情报");
		CardInfoTable cardInfoTable = new CardInfoTable(parent, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL);
		this.setControl(cardInfoTable);
		parent.cardInfoTable = cardInfoTable;
	}

}
