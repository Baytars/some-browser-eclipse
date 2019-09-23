package cn.gedobu.some.embeded.browser;

import static org.eclipse.swt.events.SelectionListener.widgetDefaultSelectedAdapter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNamedNodeMap;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMNodeList;

public class DOMEditor {
	Tree tree;

	public DOMEditor (Shell parent) {
		super ();
		Shell shell = new Shell (parent, SWT.SHELL_TRIM);
		shell.setText ("DOM Editor");
		shell.setBounds (510,10,400,400);
		shell.setLayout (new FillLayout ());

		tree = new Tree (shell, SWT.NONE);
		shell.open ();
		final TreeItem[] lastItem = new TreeItem[1];
		final TreeEditor editor = new TreeEditor (tree);
		tree.addSelectionListener (widgetDefaultSelectedAdapter(e -> {
			final TreeItem item = (TreeItem)e.item;
			final nsIDOMNode node = (nsIDOMNode)item.getData ();
			if (node == null) return; 	/* not editable */
			if (item != null && item == lastItem[0]) {
				final Composite composite = new Composite (tree, SWT.NONE);
				final Text text = new Text (composite, SWT.NONE);
				final int inset = 1;
				composite.addListener (SWT.Resize, event -> {
					Rectangle rect = composite.getClientArea ();
					text.setBounds (rect.x + inset, rect.y + inset, rect.width - inset * 2, rect.height - inset * 2);
				});
				Listener textListener = new Listener () {
					@Override
					public void handleEvent (final Event e) {
						switch (e.type) {
							case SWT.FocusOut:
								String string = text.getText ();
								node.setNodeValue (string);
								item.setText ("Node Value: " + node.getNodeValue ());
								composite.dispose ();
								break;
							case SWT.Verify:
								String newText = text.getText ();
								String leftText = newText.substring (0, e.start);
								String rightText = newText.substring (e.end, newText.length ());
								GC gc = new GC (text);
								Point size = gc.textExtent (leftText + e.text + rightText);
								gc.dispose ();
								size = text.computeSize (size.x, SWT.DEFAULT);
								editor.horizontalAlignment = SWT.LEFT;
								Rectangle itemRect = item.getBounds (), rect = tree.getClientArea ();
								editor.minimumWidth = Math.max (size.x, itemRect.width) + inset * 2;
								int left = itemRect.x, right = rect.x + rect.width;
								editor.minimumWidth = Math.min (editor.minimumWidth, right - left);
								editor.minimumHeight = size.y + inset * 2;
								editor.layout ();
								break;
							case SWT.Traverse:
								switch (e.detail) {
									case SWT.TRAVERSE_RETURN:
										string = text.getText ();
										node.setNodeValue (string);
										item.setText ("Node Value: " + node.getNodeValue ());
										//FALL THROUGH
									case SWT.TRAVERSE_ESCAPE:
										composite.dispose ();
										e.doit = false;
								}
								break;
						}
					}
				};
				text.addListener (SWT.FocusOut, textListener);
				text.addListener (SWT.Traverse, textListener);
				text.addListener (SWT.Verify, textListener);
				editor.setEditor (composite, item);
				String nodeValue = node.getNodeValue ();
				text.setText (nodeValue == null ? "null" : nodeValue);
				text.selectAll ();
				text.setFocus ();
			}
			lastItem [0] = item;
		}));
	}

	public void populate (nsIDOMElement element) {
		tree.removeAll ();
		TreeItem root = new TreeItem (tree, SWT.NONE);
		root.setText ("Root: " + element.getTagName ());
		populate (root, element);
	}

	void populate (TreeItem parentItem, nsIDOMNode node) {
		String nodeName = node.getNodeName ();
		if (nodeName.length () > 0) {
			new TreeItem (parentItem, SWT.NONE).setText ("Node Name: " + nodeName);
		}
		String localName = node.getLocalName ();
		if (localName != null && localName.length () > 0) {
			new TreeItem (parentItem, SWT.NONE).setText ("Local Name: " + localName);			
		}

		TreeItem valueItem = new TreeItem (parentItem, SWT.NONE);
		String nodeValue = node.getNodeValue ();
		valueItem.setText ("Node Value: " + nodeValue);
		if (node != null) {
			valueItem.setData (node);
			Color red = parentItem.getDisplay ().getSystemColor (SWT.COLOR_RED);
			valueItem.setForeground (red);
		}

		String prefix = node.getPrefix ();
		if (prefix != null && prefix.length () > 0) {
			new TreeItem (parentItem, SWT.NONE).setText ("Prefix: " + prefix);			
		}
		String namespaceURI = node.getNamespaceURI ();
		if (namespaceURI != null && namespaceURI.length () > 0) {
			new TreeItem (parentItem, SWT.NONE).setText ("Namespace URI: " + namespaceURI);			
		}

		nsIDOMNamedNodeMap attributes = node.getAttributes ();
		if (attributes != null) {
			int count = (int)attributes.getLength ();
			if (count > 0) {
				for (int i = 0; i < count; i++) {
					TreeItem attributeItem = new TreeItem (parentItem, SWT.NONE);
					nsIDOMNode child = attributes.item (i);
					attributeItem.setText ("Attribute " + i + " (" + child.getNodeName () + ")");
					populate (attributeItem, child);
				}
			}
		}
		String typeString;
		switch (node.getNodeType ()) {
			case 1: typeString = "ELEMENT_NODE"; break;
			case 2: typeString = "ATTRIBUTE_NODE"; break;
			case 3: typeString = "TEXT_NODE"; break;
			case 4: typeString = "CDATA_SECTION_NODE"; break;
			case 5: typeString = "ENTITY_REFERENCE_NODE"; break;
			case 6: typeString = "ENTITY_NODE"; break;
			case 7: typeString = "PROCESSING_INSTRUCTION_NODE"; break;
			case 8: typeString = "COMMENT_NODE"; break;
			case 9: typeString = "DOCUMENT_NODE"; break;
			case 10: typeString = "DOCUMENT_TYPE_NODE"; break;
			case 11: typeString = "DOCUMENT_FRAGMENT_NODE"; break;
			case 12: typeString = "NOTATION_NODE"; break;
			default: typeString = "unknown?!?";
		}
		new TreeItem (parentItem, SWT.NONE).setText ("Type: " + typeString);

		nsIDOMNodeList children = node.getChildNodes ();
		int count = (int)children.getLength ();
		if (count > 0) {
			for (int i = 0; i < count; i++) {
				TreeItem childItem = new TreeItem (parentItem, SWT.NONE);
				nsIDOMNode child = children.item (i);
				childItem.setText ("Child " + i + " (" + child.getNodeName () + ")");
				populate (childItem, child);
			}
		}
	}
}
