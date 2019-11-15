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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.ide.IDE;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMWindow;
import org.mozilla.interfaces.nsIWebBrowser;

import cn.gedobu.some.embeded.browser.DOMEditor;
import cn.gedobu.some.embeded.browser.live.LiveBrowser;
import cn.gedobu.some.embeded.browser.util.FileUtil;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class Toolbar extends ToolBar {
	ToolItem itemOpen = new ToolItem(this, SWT.PUSH);
	ToolItem itemBack = new ToolItem(this, SWT.PUSH);
	ToolItem itemStop = new ToolItem(this, SWT.PUSH);
	ToolItem itemForward = new ToolItem(this, SWT.PUSH);
	ToolItem itemRefresh = new ToolItem(this, SWT.PUSH);
	ToolItem itemLock = new ToolItem(this, SWT.PUSH);
	public boolean isLocked = false;
	public boolean isSpringDevMode = false;
	ToolItem itemTemplate = new ToolItem(this, SWT.PUSH);
	ToolItem itemGo = new ToolItem(this, SWT.PUSH);
	ToolItem itemInspect = new ToolItem(this, SWT.PUSH);
	public ToolItem itemSpring = new ToolItem(this, SWT.PUSH);
	
	Text location;
	
	@Override
	protected void checkSubclass() {
		// Just leave it nothing to suppress org.eclipse.swt.SWTException: Subclassing not allowed
	}

	public Toolbar(Composite parent, int style) {
		super(parent, style);
		itemBack.setText("◀️");
		itemForward.setText("▶️");
		itemStop.setText("⏹️");
		itemRefresh.setText("♻️");
		itemGo.setText("⎋");
		itemGo.setToolTipText("Go");
		itemLock.setText("🔓");
		itemOpen.setText("Open");
		itemTemplate.setText("ε");
		itemTemplate.setToolTipText("Create from Template");
		itemInspect.setText("🔍");
		itemInspect.setToolTipText("Inspect Elements");
		itemSpring.setText("♤");
		itemSpring.setToolTipText("Spring Dev Mode");
		
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
				case "◀️":
					browser.back();
					break;
				case "▶️":
					browser.forward();
					break;
				case "⏹️":
					browser.stop();
					break;
				case "♻️":
					browser.refresh();
					break;
				case "🔒":
					isLocked = false;
					this.itemLock.setText("🔓");
					break;
				case "🔓":
					isLocked = true;
					this.itemLock.setText("🔒");
					break;
				case "⎋":
					browser.setUrl(location.getText());
					System.out.println(FileUtil.getActiveFileName());
					break;
				case "Open":
					System.out.println("Opening file");
					String activeURL = browser.getUrl();
					File fileToOpen = new File(StringStartTrim(activeURL, "file:"));
					openFileInDefaultEditor(fileToOpen);
					break;
				case "ε":
					final FileDialog dlg = new FileDialog(getParent().getShell(), SWT.OPEN);
					dlg.setText("Select Template");
					dlg.setFilterExtensions ( new String[] { "*.xml", "*html", "*.*" } );
					final String selected = dlg.open ();
					if ( selected != null ) {
						System.out.println(selected);
						SaveAsDialog fileRenameDlg = new SaveAsDialog(getParent().getShell());
						fileRenameDlg.open();
						IPath pathChild = fileRenameDlg.getResult ();
						if ( pathChild != null ) {
							saveFile(pathChild, selected);
						}
						else {
							System.out.println("Child path is null.");
						}
					}
					else {
						System.out.println("No file selected.");
					}
					break;
				case "🔍":
					nsIWebBrowser webBrowser = (nsIWebBrowser)browser.getWebBrowser();
					nsIDOMWindow domWindow = webBrowser.getContentDOMWindow ();
					nsIDOMDocument document = domWindow.getDocument ();
					nsIDOMElement documentElement = document.getDocumentElement ();
					
					final Display display = new Display ();
					final Shell shell = new Shell (display);
					DOMEditor domEditor = new DOMEditor (shell);
					domEditor.populate (documentElement);
					
					shell.open ();
					while (!shell.isDisposed ()) {
						if (!display.readAndDispatch ()) display.sleep ();
					}
					display.dispose ();
					break;
				case "♤":
					isSpringDevMode = true;
					itemSpring.setText("♠");
					break;
				case "♠":
					isSpringDevMode = false;
					itemSpring.setText("♤");
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
		itemInspect.addListener(SWT.Selection, listener);
		itemSpring.addListener(SWT.Selection, listener);
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
										MessageDialog.openError(shell, "File Already Exists", "You cannot override an existing file!");
									}
								});
							}
							else {
								defaultDisplay.syncExec(new Runnable() {
									@Override
									public void run() {
										try {
											iFileChild.create(new FileInputStream(fileMother), true, monitor);
											MessageDialog.openConfirm(shell, "Success", String.format("File %s generated from %s saved successfully!", pathChild.toOSString(), pathStringMother ));
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
