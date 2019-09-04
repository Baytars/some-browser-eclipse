package cn.gedobu.some.embeded.browser.live.legacy;

import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.browser.StatusTextListener;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

public abstract class LiveBrowser {
	public LiveBrowser(Composite parent) {
		this.establish(parent);
	}
	
	void establish(Composite parent) {
		this.establishBrowserIn(parent);
	}
	
	abstract void establishBrowserIn(Composite parent);
	
	GridData generateStyle() {
		GridData data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		data.horizontalSpan = 3;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		return data;
	}
	
	public abstract void back();
	public abstract void forward();
	public abstract void stop();
	public abstract void refresh();
	public abstract String getUrl();
	public abstract void setUrl(String url);
	public abstract void addProgressListener(ProgressListener progressListener);
	public abstract void addStatusTextListener(StatusTextListener statusTextListener);
	public abstract void addLocationListener(LocationListener locationListener);
	public abstract void addDisposeListener(DisposeListener disposeListener);
}
