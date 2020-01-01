package cn.gedobu.some.editor.med.contentassist;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.wst.sse.ui.contentassist.CompletionProposalInvocationContext;

import cn.gedobu.some.editor.med.grammar.Grammar;

public class XMLTemplatesCompletionProposalComputer extends org.eclipse.wst.xml.ui.internal.contentassist.XMLTemplatesCompletionProposalComputer {
	@Override
	public List computeCompletionProposals(CompletionProposalInvocationContext context,
			IProgressMonitor monitor) {
		System.out.println("The plugin is computing proposals");
		IDocument 文件 = context.getDocument();
		int 偏移 = context.getInvocationOffset();
		
		try {
			int 偏移所在行 = 文件.getLineOfOffset(偏移);
			int 行头偏移 = 文件.getLineOffset(偏移所在行);
			String 行首至光标文本 = 文件.get(行头偏移, 偏移-行头偏移);
			
			List<CompletionProposal> proposals = new ArrayList<>();
			
			for ( String term : Grammar.getTerms() ) {
				int endCharIndex = 行首至光标文本.lastIndexOf(term.charAt(0));
				int overlapLength = 行首至光标文本.length() - endCharIndex;
				if ( endCharIndex >= 0 & term.length() >= overlapLength ) {
					if (  行首至光标文本.charAt(endCharIndex) == term.charAt(0) ) {
						String endStr = 行首至光标文本.substring(endCharIndex, 行首至光标文本.length()-1);
						System.out.println(endStr);
						String startStr = term.substring(0, overlapLength-1);
						System.out.println(startStr);
						if ( endStr.equals(startStr) ) {
							proposals.add(new CompletionProposal(term, 偏移-overlapLength, overlapLength, term.length()));
						}
					}
				}
			}
			return proposals;
		}
		catch (BadLocationException e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}
}
