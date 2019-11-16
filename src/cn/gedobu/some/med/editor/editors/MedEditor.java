package cn.gedobu.some.med.editor.editors;

import org.eclipse.ui.editors.text.TextEditor;

public class MedEditor extends TextEditor {

	private ColorManager colorManager;

	public MedEditor() {
		super();
		colorManager = new ColorManager();
		setSourceViewerConfiguration(new XMLConfiguration(colorManager));
		setDocumentProvider(new XMLDocumentProvider());
	}
	@Override
	public void dispose() {
		colorManager.dispose();
		super.dispose();
	}

}
