package cn.gedobu.some.embeded.browser.live;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.browser.StatusTextListener;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

public class SWTBrowser extends LiveBrowser {
	private Browser browser;
	
	public SWTBrowser(Composite parent) {
		super(parent);
	}

	void establishBrowserIn(Composite parent) {
		browser = new Browser(parent, SWT.NONE);
		GridData data = this.generateStyle();
		browser.setLayoutData(data);
		System.out.println("Is Javascript enabled? "+browser.getJavascriptEnabled());
		browser.setJavascriptEnabled(true);
	}

	public void back() {
		browser.back();
	}
	
	public void forward() {
		browser.forward();
	}
	
	public void refresh() {
		browser.refresh();
	}
	
	public void stop() {
		browser.stop();
	}
	
	public void setUrl(String url) {
		browser.setUrl(url);
	}
	
	public void addStatusTextListener(StatusTextListener statusTextListener) {
		browser.addStatusTextListener(statusTextListener);
	}
	
	public void addProgressListener(ProgressListener progressListener) {
		browser.addProgressListener(progressListener);
	}
	
	public void addLocationListener(LocationListener locationListener) {
		browser.addLocationListener(locationListener);
	}
	
	public String getUrl() {
		return browser.getUrl();
	}
	
	public void addDisposeListener(DisposeListener disposeListener) {
		browser.addDisposeListener(disposeListener);
	}
}
