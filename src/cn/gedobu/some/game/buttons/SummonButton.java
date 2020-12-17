package cn.gedobu.some.game.buttons;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;
import org.osgi.framework.Bundle;

import cn.gedobu.some.game.CardInfoTable;

public class SummonButton extends ButtonBase {
	
	public SummonButton(Composite parent) {
		super(parent);
		this.setText("抽卡");
		
		SelectionListener listener = new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				System.out.println("抽卡中……\n");
				System.out.println(String.format("Current workspace:\n%s", Platform.getLocation()));
				Connection c = null;
				Statement stmt = null;
				try {
					Class.forName("org.sqlite.JDBC");
					// c = DriverManager.getConnection(String.format("jdbc:sqlite:%s", Platform.getLocation().toString()+"/SoGa/SoGa.db"));
					String urlStr = "data/SoGa.db";
					Bundle bundle = Platform.getBundle("some.embedded.browser");
					c = DriverManager.getConnection(String.format("jdbc:sqlite:%s", FileLocator.toFileURL(bundle.getResource(urlStr))));
					c.setAutoCommit(false);
					System.out.println("Opened database successfully!");
					
					stmt = c.createStatement();
					ResultSet rs = stmt.executeQuery("select * from card;");
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
		this.addSelectionListener(listener);
	}
}
