package cn.gedobu.some.game;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public class CardTabs extends TabFolder {
	public CardInfoTable cardInfoTable;
	
	@Override
	protected void checkSubclass() {
		
	}

	public CardTabs(Composite parent, int style) {
		super(parent, style);
		new TabInfo(this, SWT.NONE);
		TabItem tabEquipment = new TabItem(this, SWT.NONE);
		tabEquipment.setText("御魂");
		TabItem tabArousal = new TabItem(this, SWT.NONE);
		tabArousal.setText("觉醒");
		TabItem tabStory = new TabItem(this, SWT.NONE);
		tabStory.setText("传记");
		
		this.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		this.pack();
	}

}
