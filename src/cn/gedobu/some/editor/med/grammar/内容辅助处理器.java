package cn.gedobu.some.editor.med.grammar;

import java.util.ArrayList;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

public class 内容辅助处理器 implements IContentAssistProcessor {

	@Override
	public ICompletionProposal[] computeCompletionProposals(ITextViewer 视图, int 偏移) {
		IDocument 文件 = 视图.getDocument();
		try {
			int 偏移所在行 = 文件.getLineOfOffset(偏移);
			int 行头偏移 = 文件.getLineOffset(偏移所在行);
//			int 当前行文本长度 = 文件.getLineLength(偏移所在行)-1;
			// System.out.println(String.format("computeCompletionProposals: %s", 当前行文本长度));
//			String 当前行文本 = 文件.get(行头偏移, 当前行文本长度).toLowerCase();
			String 行首至光标文本 = 文件.get(行头偏移, 偏移-行头偏移);
			
			ArrayList<CompletionProposal> proposals = new ArrayList<>();
			
			for ( String term : Grammar.getTerms() ) {
				int endCharIndex = 行首至光标文本.lastIndexOf(term.charAt(0));
				int overlapLength = 行首至光标文本.length() - endCharIndex;
				if ( endCharIndex >= 0 & term.length() >= overlapLength ) {
					if (  行首至光标文本.charAt(endCharIndex) == term.charAt(0) ) {
						String endStr = 行首至光标文本.substring(endCharIndex);
						String startStr = term.substring(0, overlapLength);
						// System.out.println(endStr+" "+startStr);
						if ( endStr.equals(startStr) ) {
							proposals.add(new CompletionProposal(term, 偏移-overlapLength, overlapLength, term.length()));
						}
					}
				}
				
//				More comprehensible version
//				for ( int i=0; i<行首至光标文本.length(); i++ ) {
//					int endCharIndex = 行首至光标文本.length()-1-i;
//					if ( endCharIndex >= 0 & term.length() > i ) {
//						if (  行首至光标文本.charAt(endCharIndex) == term.charAt(0) ) {
//							// dock
//							String endStr = 行首至光标文本.substring(endCharIndex);
//							int overlapLength = i+1;
//							String startStr = term.substring(0, overlapLength);
//							// System.out.println(endStr+" "+startStr);
//							if ( endStr.equals(startStr) ) {
//								proposals.add(new CompletionProposal(term, 偏移-overlapLength, overlapLength, term.length()));
//							}
//						}
//					}
//					else {
//						break;
//					}
//				}
			}
			
			// System.out.println(String.format("替换长度：%s", 当前行文本长度-(偏移-行头偏移)));
			return proposals.stream().toArray(ICompletionProposal[]::new);
			
//			return Arrays.asList(Grammar.terms).stream()
//				.filter(建议 ->  containRepeatChar(建议, 当前行文本))
//				.map(建议 -> new CompletionProposal(建议, 偏移, 0, 建议.length()))
//				.toArray(ICompletionProposal[]::new);
		}
		catch (BadLocationException e) {
			e.printStackTrace();
		}
		return new ICompletionProposal[0];
	}
	
//	private boolean containRepeatChar(String str1, String str2) {
//		boolean result = false;
//		String shorterStr;
//		String longerStr;
//		if ( str1.length() < str2.length() ) {
//			shorterStr = str1;
//			longerStr = str2;
//		}
//		else {
//			shorterStr = str2;
//			longerStr = str1;
//		}
//		for ( Character word : shorterStr.toCharArray() ) {
//			if ( longerStr.contains(word.toString()) ) {
//				return true;
//			}
//		}
//		return result;
//	}
	
	@Override
	public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset) {
		return null;
	}
	
	@Override
	public char[] getCompletionProposalAutoActivationCharacters() {
		return Grammar.getTermsPrefixes();
	}
	
	@Override
	public char[] getContextInformationAutoActivationCharacters() {
		String keys = "";
		return keys.toCharArray();
	}
	
	@Override
	public String getErrorMessage() {
		return null;
	}
	
	@Override
	public IContextInformationValidator getContextInformationValidator() {
		return null;
	}

}
