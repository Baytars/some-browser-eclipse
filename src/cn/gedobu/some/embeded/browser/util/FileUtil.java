package cn.gedobu.some.embeded.browser.util;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.ide.IDE;

public class FileUtil {
	public static IWorkbenchPage getActivePage() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
	}
	
	public static String getActiveFileName() {
		try {
//			System.out.println("Getting active file name");
			IEditorPart editor = getActivePage().getActiveEditor();
			String activeFileName = editor.getTitle();
//			System.out.println("The active file name is: "+activeFileName);
			return activeFileName;
		}
		catch (Exception e) {
			System.out.println("Error occurred when getting active file name! "+e.toString());
			return "about:blank";
		}
	}
	
	public static void openInDefaultEditor(File fileToOpen) {
		if ( fileToOpen.exists() && fileToOpen.isFile() ) {
			IFileStore fileStore = EFS.getLocalFileSystem().getStore(fileToOpen.toURI());
			System.out.println(String.format("File to open URI: %s", fileToOpen.toURI().toString()));
			try {
				IDE.openEditorOnFileStore(getActivePage(), fileStore);
			}
			catch (Exception e) {
				System.out.println("OpenEditor Error: "+e.toString());
			}
		}
		else {
			System.out.println("File does not exist!");
		}
	}
	
	public static IFile toIFile(String filePath) {
		URI fileURI = new File(filePath).toURI();
		IFile[] ifiles = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocationURI(fileURI);
		IFile oIFile = null;
		for ( IFile ifile : ifiles ) {
			if ( ifile.getLocationURI().equals(fileURI) ) {
				System.out.println(String.format("File match! %s VS %s", ifile.getLocationURI().toString(), fileURI.toString()));
				oIFile = ifile;
			}
			else {
				System.out.println(String.format("%s VS %s", ifile.getLocationURI().toString(), fileURI.toString()));
			}
		}
		return oIFile;
	}
	
	public static void save(String pathChild, String pathMother) {
		final IFile iFileChild = toIFile(pathChild);
		final File fileMother = new File(pathMother);
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
											MessageDialog.openConfirm(shell, "Success", String.format("File %s generated from %s saved successfully!", pathChild, pathMother ));
											FileUtil.openInDefaultEditor(iFileChild.getLocation().toFile());
										} catch (Exception e) {
											MessageDialog.openConfirm(shell, "Failure", e.toString());
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
	
	public static String getFileAbsolutePath(IEditorInput input) {
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
	
	public static String getActiveFileAbsolutePath() {
		return getFileAbsolutePath(getActivePage().getActiveEditor().getEditorInput());
	}
	
	public static String getActiveFileParentPath() {
		return new File(getActiveFileAbsolutePath()).getParentFile().getAbsolutePath();
	}
}
