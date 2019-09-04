package cn.gedobu.some.embeded.browser.util;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

public class FileUtil {
	public static String getActiveFileName() {
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
