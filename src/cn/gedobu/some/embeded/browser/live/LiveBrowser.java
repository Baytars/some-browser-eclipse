package cn.gedobu.some.embeded.browser.live;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import cn.gedobu.some.embeded.browser.toolbar.Toolbar;
import cn.gedobu.some.embeded.browser.util.FileUtil;

public class LiveBrowser extends Browser {
	Toolbar toolbar;

	public LiveBrowser(Composite parent, int style) {
		super(parent, style);
		GridData data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		data.horizontalSpan = 3;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		this.setLayoutData(data);
		
		System.out.println("Is Javascript enabled? "+this.getJavascriptEnabled());
		this.setJavascriptEnabled(true);
	}
	
	@Override
	protected void checkSubclass() {
		// Just leave it nothing to suppress org.eclipse.swt.SWTException: Subclassing not allowed
	}
	
	public void bindsWith(Toolbar toolbar) {
		this.toolbar = toolbar;
	}
	
	public void bindsWith(ProgressBar progressBar) {
		this.addProgressListener(new ProgressListener() {
			@Override
			public void changed(ProgressEvent event) {
					if (event.total == 0) return;
					int ratio = event.current * 100 / event.total;
					progressBar.setSelection(ratio);
			}
			@Override
			public void completed(ProgressEvent event) {
				progressBar.setSelection(0);
			}
		});
	}
	
	public void bindsWith(Label status) {
		this.addStatusTextListener(event -> status.setText(event.text));
	}
	
	public void bindsWith(Text location) {
		this.addLocationListener(new LocationListener() {
			
			@Override
			public void changing(LocationEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void changed(LocationEvent event) {
				if (event.top) location.setText(event.location);
			}
		});
		location.addListener(SWT.DefaultSelection, e -> this.setUrl(location.getText()));
	}

	IPartListener syncPageToBrowser(Browser browser) {
		IPartListener pageListener = new IPartListener() {
			
			@Override
			public void partOpened(IWorkbenchPart arg0) {
				System.out.println("partOpened: "+FileUtil.getActiveFileName()+"\n");
			}
			
			@Override
			public void partDeactivated(IWorkbenchPart arg0) {
				System.out.println("partDeactivated: "+FileUtil.getActiveFileName()+"\n");
			}
			
			@Override
			public void partClosed(IWorkbenchPart arg0) {
				System.out.println("partClosed: "+FileUtil.getActiveFileName()+"\n");
			}
			
			@Override
			public void partBroughtToTop(IWorkbenchPart arg0) {
				System.out.println("partBroughtToTop: "+FileUtil.getActiveFileName()+"\n");
			}
			
			@Override
			public void partActivated(IWorkbenchPart activePart) {
				System.out.println("partActivated: "+FileUtil.getActiveFileName()+"\n");
				System.out.println("Active part is: " + activePart.getClass().getName());
				IEditorPart activeEditor = null;
				String pathWithProtocol = "about:blank";
				try {
					activeEditor = FileUtil.getActivePage().getActiveEditor();
					System.out.println("Text editor class name: " + activeEditor.getClass().getName());
					IEditorInput input = FileUtil.getActivePage().getActiveEditor().getEditorInput();
					
					if ( toolbar.itemSpring.getText() == "♠" ) {
						pathWithProtocol = "http://localhost:8080/glossaries/spring/" + input.getName();
					}
					else {
						pathWithProtocol = "file://"+FileUtil.getFileAbsolutePath(input);
					}
					
					if ( activePart.getClass().getName().equals(activeEditor.getClass().getName()) ) {
						if ( ! browser.getUrl().equals(pathWithProtocol) ) {
							if ( pathWithProtocol.endsWith("ml") ) {
								if ( ! toolbar.isLocked ) {
									browser.setUrl(pathWithProtocol);
								}
							}
							else if ( pathWithProtocol.endsWith(".java") && input.getName().startsWith("_") ) {
								String pageNum = input.getName().replaceFirst("_", "").replace(".java", "");
								browser.setUrl(String.format("http://localhost:8080/glossaries/spring/%s.html", pageNum));
							}
						}
					}
				}
				catch (Exception e) {
					System.out.println("There's currently no active editor. No file is opened.");
				}
				
//				String workspaceRoot= Platform.getInstanceLocation().getURL().toString();
//				System.out.println(workspaceRoot);
//				System.out.println("location text: " + location.getText());
				System.out.println("pathWithProtocol: " + pathWithProtocol);
				System.out.println("browser url: " + browser.getUrl());
			}
		};
		FileUtil.getActivePage().addPartListener(pageListener);
		return pageListener;
	}
	
	public void bindEditorChange() {
		System.out.println("Adding listener to editor ...");
		
		IPartListener pageListener = syncPageToBrowser(this);
		IResourceChangeListener resListener = this.syncResourceChange();
		
		this.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent event) {
				FileUtil.getActivePage().removePartListener(pageListener);
				ResourcesPlugin.getWorkspace().removeResourceChangeListener(resListener);
			}
		});
	}
	
	LiveBrowser getSelf() {
		return this;
	}
	
	IResourceChangeListener syncResourceChange() {
		IResourceChangeListener resListener = new IResourceChangeListener() {
			
			@Override
			public void resourceChanged(IResourceChangeEvent event) {
				System.out.println(String.format("type: %s", event.getType()));
				System.out.println(String.format("flag: %s", event.getDelta().getFlags()));
				try {
					if ( ! toolbar.isLocked ) {
						getSelf().refresh();
					}
				}
				catch (SWTException e) {
					System.out.println(e.toString()+": The browser window don't seem to be the active window.");
				}
			}
		};
		ResourcesPlugin.getWorkspace().addResourceChangeListener(resListener);
		return resListener;
	}
}
