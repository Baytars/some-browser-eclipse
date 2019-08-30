package cn.gedobu.some.embeded.browser.parts;

import org.eclipse.swt.widgets.Composite;

import java.net.URI;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.FileStoreEditorInput;

import cn.gedobu.some.embeded.browser.live.CEFBrowser;
import cn.gedobu.some.embeded.browser.live.LiveBrowser;
import cn.gedobu.some.embeded.browser.live.SWTBrowser;

public class LiveBrowserWindow {
	private boolean isLocked = false;
	
	public LiveBrowserWindow(Composite parent) {
		this.establish(parent);
	}
	
	private void decorateParent(Composite parent) {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		parent.setLayout(gridLayout);
	}
	
	private void establish(Composite parent) {
		decorateParent(parent);
		ToolBar toolbar = new ToolBar(parent, SWT.NONE);
		ToolItem itemBack = new ToolItem(toolbar, SWT.PUSH);
		itemBack.setText("Back");
		ToolItem itemForward = new ToolItem(toolbar, SWT.PUSH);
		itemForward.setText("Forward");
		ToolItem itemStop = new ToolItem(toolbar, SWT.PUSH);
		itemStop.setText("Stop");
		ToolItem itemRefresh = new ToolItem(toolbar, SWT.PUSH);
		itemRefresh.setText("Refresh");
		ToolItem itemGo = new ToolItem(toolbar, SWT.PUSH);
		itemGo.setText("Go");
		ToolItem itemLock = new ToolItem(toolbar, SWT.PUSH);
		itemLock.setText("Lock Off");

		GridData data = new GridData();
		data.horizontalSpan = 3;
		toolbar.setLayoutData(data);

		Label labelAddress = new Label(parent, SWT.NONE);
		labelAddress.setText("Address");

		final Text location = new Text(parent, SWT.BORDER);
		data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.horizontalSpan = 2;
		data.grabExcessHorizontalSpace = true;
		location.setLayoutData(data);

		LiveBrowser browser = establishBrowserIn(parent);

		final Label status = new Label(parent, SWT.NONE);
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		status.setLayoutData(data);

		final ProgressBar progressBar = new ProgressBar(parent, SWT.NONE);
		data = new GridData();
		data.horizontalAlignment = GridData.END;
		progressBar.setLayoutData(data);

		/* event handling */
		Listener listener = event -> {
			ToolItem item = (ToolItem) event.widget;
			String string = item.getText();
			switch (string) {
				case "Back":
					browser.back();
					break;
				case "Forward":
					browser.forward();
					break;
				case "Stop":
					browser.stop();
					break;
				case "Refresh":
					browser.refresh();
					break;
				case "Lock On":
					isLocked = false;
					itemLock.setText("Lock Off");
					break;
				case "Lock Off":
					isLocked = true;
					itemLock.setText("Lock On");
					break;
				case "Go":
					browser.setUrl(location.getText());
					System.out.println(getActiveFileName());
					break;
				default:
					break;
			}
		};
		browser.addProgressListener(new ProgressListener() {
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
		browser.addStatusTextListener(event -> status.setText(event.text));
		browser.addLocationListener(new LocationListener() {
			
			@Override
			public void changing(LocationEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void changed(LocationEvent event) {
				if (event.top) location.setText(event.location);
			}
		});
		itemBack.addListener(SWT.Selection, listener);
		itemForward.addListener(SWT.Selection, listener);
		itemStop.addListener(SWT.Selection, listener);
		itemRefresh.addListener(SWT.Selection, listener);
		itemGo.addListener(SWT.Selection, listener);
		itemLock.addListener(SWT.Selection, listener);
		location.addListener(SWT.DefaultSelection, e -> browser.setUrl(location.getText()));

		browser.setUrl("about:blank");
		
		bindEditorChangeTo(browser);
	}
	
	private LiveBrowser establishBrowserIn(Composite parent) {
		try {
			LiveBrowser browser = new CEFBrowser(parent);
			return browser;
		}
		catch (Exception e) {
			LiveBrowser browser = new SWTBrowser(parent);
			return browser;
		}
	}
	
	private IPartListener syncPageToBrowser(IWorkbenchPage page, LiveBrowser browser) {
		IPartListener pageListener = new IPartListener() {
			
			@Override
			public void partOpened(IWorkbenchPart arg0) {
				System.out.println("partOpened: "+getActiveFileName()+"\n");
			}
			
			@Override
			public void partDeactivated(IWorkbenchPart arg0) {
				System.out.println("partDeactivated: "+getActiveFileName()+"\n");
			}
			
			@Override
			public void partClosed(IWorkbenchPart arg0) {
				System.out.println("partClosed: "+getActiveFileName()+"\n");
			}
			
			@Override
			public void partBroughtToTop(IWorkbenchPart arg0) {
				System.out.println("partBroughtToTop: "+getActiveFileName()+"\n");
			}
			
			@Override
			public void partActivated(IWorkbenchPart activePart) {
				System.out.println("Active part is: " + activePart.getClass().getName());
				System.out.println("Text editor class name: " + page.getActiveEditor().getClass().getName());
				System.out.println("partActivated: "+getActiveFileName()+"\n");
//				String workspaceRoot= Platform.getInstanceLocation().getURL().toString();
//				System.out.println(workspaceRoot);
				IEditorInput input = page.getActiveEditor().getEditorInput();
				String pathWithProtocol = "file://"+getFileAbsolutePath(input);
//				System.out.println("location text: " + location.getText());
				System.out.println("pathWithProtocol: " + pathWithProtocol);
				System.out.println("browser url: " + browser.getUrl());
				if ( activePart.getClass().getName().equals(page.getActiveEditor().getClass().getName()) ) {
					if ( ! browser.getUrl().equals(pathWithProtocol) ) {
						if ( pathWithProtocol.endsWith(".html") ) {
							if ( ! isLocked ) {
								browser.setUrl(pathWithProtocol);
							}
						}
					}
				}
			}
		};
		page.addPartListener(pageListener);
		return pageListener;
	}
	
	private IResourceChangeListener syncResourceChangeToBrowser(LiveBrowser browser) {
		IResourceChangeListener resListener = new IResourceChangeListener() {
			
			@Override
			public void resourceChanged(IResourceChangeEvent event) {
				System.out.println("Resource has changed: "+event.toString());
				browser.refresh();
			}
		};
		ResourcesPlugin.getWorkspace().addResourceChangeListener(resListener);
		return resListener;
	}
	
	private void bindEditorChangeTo(LiveBrowser browser) {
		System.out.println("Adding listener to editor ...");
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage page = window.getActivePage();
		
		IPartListener pageListener = syncPageToBrowser(page, browser);
		IResourceChangeListener resListener = syncResourceChangeToBrowser(browser);
		
		browser.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent event) {
				page.removePartListener(pageListener);
				ResourcesPlugin.getWorkspace().removeResourceChangeListener(resListener);
			}
		});
	}
	
	private String getFileAbsolutePath(IEditorInput input) {
		String absPath = "about:blank";
		if (input instanceof FileStoreEditorInput) {
			URI file = ((FileStoreEditorInput)input).getURI();
			absPath = file.getPath();
		}
		else if(input instanceof IFileEditorInput){
			URI file = ((IFileEditorInput)input).getFile().getLocationURI();
//			System.out.println(file);
//			System.out.println(file.toString());
//			IProject project = file.getProject(); 
//			System.out.println(project.getFullPath().makeAbsolute().toOSString());;
//			String relaFilePath = file.getFullPath().makeAbsolute().toOSString();
//			String fullStr = "file://"+workspaceRoot+relaFilePath;
//			System.out.println(fullStr);
			absPath = file.getRawPath();
		}
		return absPath;
	}
	
	private String getActiveFileName() {
		try {
//			System.out.println("Getting active file name");
			IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
			String activeFileName = editor.getTitle();
//			System.out.println("The active file name is: "+activeFileName);
			return activeFileName;
		}
		catch (Exception e) {
			System.out.println("Error occurred when getting active file name! "+e.toString());
			return "about:blank";
		}
		
	}
}
