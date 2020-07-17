package cn.gedobu.some.game;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.TableItem;

import java.sql.*;

public class SoGaWindow {
	Composite parent;
	CardTabs cardTabs;
	
	public SoGaWindow(Composite parent) {
		this.parent = parent;
		GridLayout gridLayout = new GridLayout();
		this.parent.setLayout(gridLayout);
		
		addButton();
		addTabFolder();
	}
	
	private void addButton() {
		Button btn = new Button(parent, SWT.PUSH);
		btn.setText("抽卡");
		
		SelectionListener listener = new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				System.out.println("抽卡中……\n");
				System.out.println(String.format("Current workspace:\n%s", Platform.getLocation()));
				Connection c = null;
				Statement stmt = null;
				try {
					Class.forName("org.sqlite.JDBC");
					c = DriverManager.getConnection(String.format("jdbc:sqlite:%s", Platform.getLocation().toString()+"/SoGa/SoGa.db"));
					c.setAutoCommit(false);
					System.out.println("Opened database successfully!");
					
					stmt = c.createStatement();
					ResultSet rs = stmt.executeQuery("select * from cards;");
					while (rs.next()) {
						String name = rs.getString("name");
						int value = rs.getInt("value");
						
						CardInfoTable tb = cardTabs.cardInfoTable;
						for (TableItem item : tb.getItems()) {
							
							switch (item.getText()) {
							case "稀有度":
								item.setText(1, Integer.toString(value));
								break;
							case "名称":
								item.setText(1, name);
								break;
							default:
								break;
							}
						}
					}
					
					c.close();
				}
				catch (Exception e) {
					System.err.println(e.getClass().getName()+": "+e.getMessage());
					System.exit(0);
				}
				System.out.println("Operation done successfully!");
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				System.out.println("widgetDefaultSelected");
			}
		};
		btn.addSelectionListener(listener);
	}
	
	private void addTabFolder() {
		this.cardTabs = new CardTabs(parent, SWT.BORDER);
	}
}
