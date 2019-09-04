package cn.gedobu.some.embeded.browser.toolbar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.ide.IDE;

import cn.gedobu.some.embeded.browser.live.LiveBrowser;
import cn.gedobu.some.embeded.browser.util.FileUtil;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class Toolbar extends ToolBar {
	ToolItem itemBack = new ToolItem(this, SWT.PUSH);
	ToolItem itemForward = new ToolItem(this, SWT.PUSH);
	ToolItem itemStop = new ToolItem(this, SWT.PUSH);
	ToolItem itemRefresh = new ToolItem(this, SWT.PUSH);
	ToolItem itemGo = new ToolItem(this, SWT.PUSH);
	ToolItem itemLock = new ToolItem(this, SWT.PUSH);
	public boolean isLocked = false;
	ToolItem itemOpen = new ToolItem(this, SWT.PUSH);
	ToolItem itemTemplate = new ToolItem(this, SWT.PUSH);
	
	Text location;
	
	@Override
	protected void checkSubclass() {
		// Just leave it nothing to suppress org.eclipse.swt.SWTException: Subclassing not allowed
	}

	public Toolbar(Composite parent, int style) {
		super(parent, style);
		itemBack.setText("Back");
		itemForward.setText("Forward");
		itemStop.setText("Stop");
		itemRefresh.setText("Refresh");
		itemGo.setText("Go");
		itemLock.setText("Lock Off");
		itemOpen.setText("Open");
		itemTemplate.setText("Create from Template");
		
		GridData data = new GridData();
		data.horizontalSpan = 3;
		this.setLayoutData(data);
	}
	
	public void bindsWith(Text location) {
		this.location = location;
	}
	
	public void bindsWith(LiveBrowser browser) {
		browser.bindsWith(this);
		
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
					this.itemLock.setText("Lock Off");
					break;
				case "Lock Off":
					isLocked = true;
					this.itemLock.setText("Lock On");
					break;
				case "Go":
					browser.setUrl(location.getText());
					System.out.println(FileUtil.getActiveFileName());
					break;
				case "Open":
					System.out.println("Opening file");
					String activeURL = browser.getUrl();
					File fileToOpen = new File(StringStartTrim(activeURL, "file:"));
					openFileInDefaultEditor(fileToOpen);
					break;
				case "Create from Template":
					final FileDialog dlg = new FileDialog(getParent().getShell(), SWT.OPEN);
					dlg.setText("Select Template");
					dlg.setFilterExtensions ( new String[] { "*.html", "*.*" } );
					final String selected = dlg.open ();
					if ( selected != null ) {
						System.out.println(selected);
						SaveAsDialog fileRenameDlg = new SaveAsDialog(getParent().getShell());
						fileRenameDlg.open();
						IPath pathChild = fileRenameDlg.getResult ();
						if ( pathChild != null ) {
							if ( pathChild != null ) {
								saveFile(pathChild, selected);
							}
						}
					}
					else {
						System.out.println("No file selected.");
					}
					break;
				default:
					break;
			}
		};
		
		itemBack.addListener(SWT.Selection, listener);
		itemForward.addListener(SWT.Selection, listener);
		itemStop.addListener(SWT.Selection, listener);
		itemRefresh.addListener(SWT.Selection, listener);
		itemGo.addListener(SWT.Selection, listener);
		itemLock.addListener(SWT.Selection, listener);
		itemOpen.addListener(SWT.Selection, listener);
		itemTemplate.addListener(SWT.Selection, listener);
	}
	
	void saveFile(IPath pathChild, String pathStringMother) {
		final IFile iFileChild = ResourcesPlugin.getWorkspace().getRoot().getFile(pathChild);
		final File fileMother = new File(pathStringMother);
		if (iFileChild != null) {
			Display defaultDisplay = Display.getDefault();
			Shell shell = defaultDisplay.getActiveShell();
			ProgressMonitorDialog pm = new ProgressMonitorDialog(shell);
			try {
				pm.run(true, true, new IRunnableWithProgress() {
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
						try {
							if (iFileChild.exists()) {
								defaultDisplay.syncExec(new Runnable() {
									@Override
									public void run() {
										MessageDialog.openError(shell, "File Already Exists", "File Already Exists!");
									}
								});
							}
							else {
								defaultDisplay.syncExec(new Runnable() {
									@Override
									public void run() {
										MessageDialog.openConfirm(shell, "Success", "File save successfully!");
										try {
											iFileChild.create(new FileInputStream(fileMother), true, monitor);
											openFileInDefaultEditor(iFileChild.getLocation().toFile());
										} catch (FileNotFoundException
												| CoreException e) {
											e.printStackTrace();
										}
									}
								});
							}
						} catch (Throwable e) {
							throw new InvocationTargetException(e);
						} finally {
							monitor.done();
						}
					}

				});
			} catch (InvocationTargetException e) {
				System.out.println(e.getCause());
			} catch (InterruptedException e) {
				System.out.println(e);
			}
		}
	}
	
	void openFileInDefaultEditor(File fileToOpen) {
		System.out.println(fileToOpen.getPath());
		
		if ( fileToOpen.exists() && fileToOpen.isFile() ) {
			IFileStore fileStore = EFS.getLocalFileSystem().getStore(fileToOpen.toURI());
			try {
				IDE.openEditorOnFileStore(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), fileStore);
			}
			catch (Exception e) {
				System.out.println("OpenEditor Error: "+e.toString());
			}
		}
		else {
		    System.out.println("File does not exist!");
		}
	}
	
	/**
		* 去掉指定字符串的开头的指定字符
		* @param stream 原始字符串
		* @param trim 要删除的字符串
		* @return
	*/
	String StringStartTrim(String stream, String trim) {
		// null或者空字符串的时候不处理
		if (stream == null || stream.length() == 0 || trim == null || trim.length() == 0) {
			return stream;
		}
		// 要删除的字符串结束位置
		int end;
		// 正规表达式
		String regPattern = "[" + trim + "]*+";
		Pattern pattern = Pattern.compile(regPattern, Pattern.CASE_INSENSITIVE);
		// 去掉原始字符串开头位置的指定字符
		Matcher matcher = pattern.matcher(stream);
		if (matcher.lookingAt()) {
			end = matcher.end();
			stream = stream.substring(end);
		}
		// 返回处理后的字符串
		return stream;
	}
}
