package cn.gedobu.some.embeded.browser.parts;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Text;

import cn.gedobu.some.embeded.browser.live.LiveBrowser;
import cn.gedobu.some.embeded.browser.toolbar.Toolbar;


public class LiveBrowserWindow {
	Toolbar toolbar;
	
	public LiveBrowserWindow(Composite parent) {
		this.decorateParent(parent);
		this.establish(parent);
	}
	
	void decorateParent(Composite parent) {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		parent.setLayout(gridLayout);
	}
	
	void establish(Composite parent) {
		toolbar = new Toolbar(parent, SWT.NONE);

		establishAddressLabel(parent);

		Text location = establishLocationBar(parent);
		toolbar.bindsWith(location);
		
		LiveBrowser browser = new LiveBrowser(parent, SWT.NONE);
		browser.bindEditorChange();
		browser.bindsWith(location);
		toolbar.bindsWith(browser);

		Label status = establishStatusLabel(parent);
		browser.bindsWith(status);

		ProgressBar progressBar = establishProgressBar(parent);
		browser.bindsWith(progressBar);
		
		browser.setUrl("about:blank");
	}
	
	Label establishAddressLabel(Composite parent) {
		Label labelAddress = new Label(parent, SWT.NONE);
		labelAddress.setText("Address");
		return labelAddress;
	}
	
	Label establishStatusLabel(Composite parent) {
		final Label status = new Label(parent, SWT.NONE);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		status.setLayoutData(data);
		return status;
	}
	
	ProgressBar establishProgressBar(Composite parent) {
		final ProgressBar progressBar = new ProgressBar(parent, SWT.NONE);
		GridData data = new GridData();
		data.horizontalAlignment = GridData.END;
		progressBar.setLayoutData(data);
		return progressBar;
	}
	
	Text establishLocationBar(Composite parent) {
		final Text location = new Text(parent, SWT.BORDER);
		GridData data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.horizontalSpan = 2;
		data.grabExcessHorizontalSpace = true;
		location.setLayoutData(data);
		return location;
	}
}
