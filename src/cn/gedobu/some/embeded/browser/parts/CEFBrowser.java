package cn.gedobu.some.embeded.browser.parts;

import org.eclipse.swt.widgets.Composite;
import java.net.URI;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.chromium.Browser;
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

public class CEFBrowser {
	public CEFBrowser(Composite parent) {
		this.establish(parent);
	}
	
	private void establish(Composite parent) {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		parent.setLayout(gridLayout);
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

		final Browser browser;
		try {
			browser = new Browser(parent, SWT.NONE);
		} catch (SWTError e) {
			System.out.println("Could not instantiate Browser: " + e.getMessage());
			parent.dispose();
			return;
		}
		data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		data.horizontalSpan = 3;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		browser.setLayoutData(data);
		System.out.println("Is Javascript enabled? "+browser.getJavascriptEnabled());;
		browser.setJavascriptEnabled(true);

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
			if (string.equals("Back"))
				browser.back();
			else if (string.equals("Forward"))
				browser.forward();
			else if (string.equals("Stop"))
				browser.stop();
			else if (string.equals("Refresh"))
				browser.refresh();
			else if (string.equals("Go"))
				browser.setUrl(location.getText());
				System.out.println(getActiveFileName());
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
		browser.addLocationListener(LocationListener.changedAdapter(event -> {
				if (event.top) location.setText(event.location);
			}
		));
		itemBack.addListener(SWT.Selection, listener);
		itemForward.addListener(SWT.Selection, listener);
		itemStop.addListener(SWT.Selection, listener);
		itemRefresh.addListener(SWT.Selection, listener);
		itemGo.addListener(SWT.Selection, listener);
		location.addListener(SWT.DefaultSelection, e -> browser.setUrl(location.getText()));

		browser.setUrl("microsoft.com");
		
		System.out.println("Adding listener to editor ...");
		try {
			IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			IWorkbenchPage page = window.getActivePage();
			page.addPartListener(new IPartListener() {
				
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
				public void partActivated(IWorkbenchPart arg0) {
					System.out.println("partActivated: "+getActiveFileName()+"\n");
//					String workspaceRoot= Platform.getInstanceLocation().getURL().toString();
//					System.out.println(workspaceRoot);
					IEditorInput input = page.getActiveEditor().getEditorInput();
					
					if (input instanceof FileStoreEditorInput) {
						URI file = ((FileStoreEditorInput)input).getURI();
						browser.setUrl("file://"+file.getPath());
					}
					else if(input instanceof IFileEditorInput){
						URI file = ((IFileEditorInput)input).getFile().getLocationURI();
//						System.out.println(file);
						System.out.println(file.getRawPath());
//						System.out.println(file.toString());
//						IProject project = file.getProject(); 
//						System.out.println(project.getFullPath().makeAbsolute().toOSString());;
//						String relaFilePath = file.getFullPath().makeAbsolute().toOSString();
//						String fullStr = "file://"+workspaceRoot+relaFilePath;
//						System.out.println(fullStr);
						browser.setUrl("file://"+file.getRawPath());
						
					}
				}
			});
		}
		catch (Exception e) {
			System.out.println("Error trying to add listener to page: "+e.getMessage());
		}
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
			System.out.println("Error occurred when getting active file name!");
			return e.getMessage();
		}
		
	}
}