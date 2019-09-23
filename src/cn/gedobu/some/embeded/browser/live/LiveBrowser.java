package cn.gedobu.some.embeded.browser.live;

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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.FileStoreEditorInput;

import cn.gedobu.some.embeded.browser.toolbar.Toolbar;
import cn.gedobu.some.embeded.browser.util.FileUtil;

public class LiveBrowser extends Browser {
	IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
	IWorkbenchPage page = window.getActivePage();
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
					activeEditor = page.getActiveEditor();
					System.out.println("Text editor class name: " + activeEditor.getClass().getName());
					IEditorInput input = page.getActiveEditor().getEditorInput();
					pathWithProtocol = "file://"+getFileAbsolutePath(input);
					if ( activePart.getClass().getName().equals(activeEditor.getClass().getName()) ) {
						if ( ! browser.getUrl().equals(pathWithProtocol) ) {
							if ( pathWithProtocol.endsWith(".html") ) {
								if ( ! toolbar.isLocked ) {
									browser.setUrl(pathWithProtocol);
								}
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
		page.addPartListener(pageListener);
		return pageListener;
	}
	
	String getFileAbsolutePath(IEditorInput input) {
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
	
	public void bindEditorChange() {
		System.out.println("Adding listener to editor ...");
		
		IPartListener pageListener = syncPageToBrowser(this);
		IResourceChangeListener resListener = syncResourceChangeToBrowser(this);
		
		this.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent event) {
				page.removePartListener(pageListener);
				ResourcesPlugin.getWorkspace().removeResourceChangeListener(resListener);
			}
		});
	}
	
	IResourceChangeListener syncResourceChangeToBrowser(Browser browser) {
		IResourceChangeListener resListener = new IResourceChangeListener() {
			
			@Override
			public void resourceChanged(IResourceChangeEvent event) {
				System.out.println(String.format("Resource has changed: %s", event.getType()));
				browser.refresh();
			}
		};
		ResourcesPlugin.getWorkspace().addResourceChangeListener(resListener);
		return resListener;
	}
}
