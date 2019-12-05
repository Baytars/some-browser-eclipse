package cn.gedobu.some.embeded.browser.toolbar;

import java.io.File;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMWindow;
import org.mozilla.interfaces.nsIWebBrowser;
import org.osgi.service.prefs.BackingStoreException;

import cn.gedobu.some.embeded.browser.DOMEditor;
import cn.gedobu.some.embeded.browser.live.LiveBrowser;
import cn.gedobu.some.embeded.browser.util.FileUtil;
import cn.gedobu.some.embeded.browser.util.StringUtil;

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
	
	IEclipsePreferences preferences = InstanceScope.INSTANCE.getNode("cn.gedobu.some.eclipse.prefernces.template");
	
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
					File fileToOpen = new File(StringUtil.startTrim(activeURL, "file:"));
					FileUtil.openInDefaultEditor(fileToOpen);
					break;
				case "ε":
					String selected = openTemplateSelectionDialog();
					if ( selected != null ) {
						openCloneSaveDialog(selected);
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
	
	String openTemplateSelectionDialog() {
		final FileDialog dlg = new FileDialog(getParent().getShell(), SWT.OPEN);
		dlg.setText("Select Template");
		dlg.setFilterExtensions ( new String[] { "*.xml", "*html", "*.*" } );
		String lastSelectedLocation = preferences.node("last").get("file","");
		if ( ! lastSelectedLocation.isEmpty() ) {
			final File lastFile = new File(lastSelectedLocation);
			System.out.println(String.format("Last used template is %s", lastFile.getParent()));;
			dlg.setFilterPath(lastFile.getParent());
		}
		final String selected = dlg.open ();
		if ( selected != null ) {
			System.out.println(String.format("Selected file is %s", selected));
			preferences.node("last").put("file", selected);
			try {
				preferences.flush();
			} catch (BackingStoreException e) {
				System.out.println("Save last template location failed.");
			}
		}
		return selected;
	}
	
	void openCloneSaveDialog(String clonePath) {
		FileDialog fileRenameDlg = new FileDialog(getParent().getShell(), SWT.SAVE);
		String lastSavedDir = preferences.node("last").get("dir", "");
		if ( ! lastSavedDir.isEmpty() ) {
			System.out.println(String.format("Last saved file is %s", lastSavedDir));
			File lastSavedFile = new File(lastSavedDir);
			File ptFile = lastSavedFile.getParentFile();
			if ( lastSavedFile.exists() ) {
				fileRenameDlg.setFilterPath(ptFile.getAbsolutePath());
				
				// set next number, in addition
				String fileName = lastSavedFile.getName();
				String[] fileNameParts = fileName.split("\\.");
				System.out.println(String.format("File name is %s, split length is %s", fileName, fileNameParts.length));
				try {
					String fileNameNoExt = fileNameParts[0];
					String fileNameExt = fileNameParts[1];
					int fileNumber = Integer.valueOf(fileNameNoExt) + 1;
					fileRenameDlg.setFileName(String.format("%s.%s", fileNumber, fileNameExt));
				}
				catch (Exception e) {
					System.out.println(e.toString());
				}
			}
			else {
				if ( ptFile.exists() ) {
					fileRenameDlg.setFilterPath(ptFile.getAbsolutePath());
				}
				else {
					// set filter to the active project path
					fileRenameDlg.setFilterPath(FileUtil.getActiveFileParentPath());
				}
			}
		}
		String pathChild = fileRenameDlg.open();
		if ( pathChild != null ) {
			FileUtil.save(pathChild, clonePath);
			preferences.node("last").put("dir", new File(pathChild).getAbsolutePath());
			try {
				preferences.flush();
			} catch (BackingStoreException e) {
				System.out.println("Save last saved location failed.");
			}
		}
		else {
			System.out.println("Child path is null.");
		}
	}
}
