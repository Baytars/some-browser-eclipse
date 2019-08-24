package cn.gedobu.some.embeded.browser.parts;

import javax.annotation.PostConstruct;
import org.eclipse.swt.widgets.Composite;

public class SoMeBrowserView {
	@PostConstruct
	public void createPartControl(Composite parent) {
		new LiveBrowserWindow(parent);
	}
}
