package cn.gedobu.some.game;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class CardInfoTable extends Table {
	@Override
	protected void checkSubclass() {
		
	}

	public CardInfoTable(Composite parent, int style) {
		super(parent, style);
		String[] titles = {
			"键",
			"值"
		};
		this.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		for(String title : titles) {
			TableColumn column = new TableColumn(this, SWT.NONE);
			column.setText(title);
		}
		
		this.setHeaderVisible(true);
		this.setLinesVisible(true);
		
		String[][] items = {
			{
				"稀有度", "N"
			},
			{
				"名称", "无"
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
				"技能1", "无"
			},
			{
				"技能2", "无"
			},
			{
				"技能3", "无"
			}
		};
		
		for(String[] field : items) {
			TableItem item = new TableItem(this, SWT.NONE);
			item.setText(field);
		}
		
		for (int i=0; i<titles.length; i++) {
			this.getColumn(i).pack();
		}
		
		this.pack();
	}

}
