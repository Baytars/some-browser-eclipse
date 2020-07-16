package cn.gedobu.some.game;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class SoGaWindow {
	public SoGaWindow(Composite parent) {
		GridLayout gridLayout = new GridLayout();
		parent.setLayout(gridLayout);
		
		Button btnButton = new Button(parent, SWT.PUSH);
		btnButton.setText("抽卡");
		
		final TabFolder tabFolder = new TabFolder(parent, SWT.BORDER);
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("式神情报");
		
		Table tb = new Table(tabFolder, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL);
		String[] titles = {
			"键",
			"值"
		};
		
		GridData data = new GridData(GridData.FILL_BOTH);
		tabFolder.setLayoutData(data);
		tb.setLayoutData(data);
		
		for(String title : titles) {
			TableColumn column = new TableColumn(tb, SWT.NONE);
			column.setText(title);
		}
		
		tb.setHeaderVisible(true);
		tb.setLinesVisible(true);
		
		String[][] items = {
			{
				"稀有度", "N"
			},
			{
				"名称", "null"
			},
			{
				"等级", "1"
			},
			{
				"星级", "2"
			},
			{
				"经验", "0"
			},
			{
				"攻击", "0"
			},
			{
				"生命", "0"
			},
			{
				"防御", "0"
			},
			{
				"速度", "0"
			},
			{
				"暴击", "0%"
			},
			{
				"暴击伤害", "0%"
			},
			{
				"效果命中", "0%"
			},
			{
				"效果抵抗", "0%"
			},
			{
				"技能1", "null"
			},
			{
				"技能2", "null"
			},
			{
				"技能3", "null"
			}
		};
		
		for(String[] field : items) {
			TableItem item = new TableItem(tb, SWT.NONE);
			item.setText(field);
		}
		
		for (int i=0; i<titles.length; i++) {
			tb.getColumn(i).pack();
		}
		
		tb.pack();
		tabItem.setControl(tb);
		tabFolder.pack();
	}
}
