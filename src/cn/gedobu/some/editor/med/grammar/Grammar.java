package cn.gedobu.some.editor.med.grammar;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;

public class Grammar {
	public static final String[] keywords = new String [] {
		"每当",
		"如果",
		"否则",
		"类别",
		"定义"
	};
	
	private static String[] terms;
	
	private static char[] termsPrefixes;
	
	static URL getRepoURL() {
		String urlStr = "https://dev.tencent.com/u/Baytars/p/MedicalContentAssist/git/raw/master/terms.xml";
		try {
			return new URL(urlStr);
		} catch (MalformedURLException repoException) {
			System.out.println(String.format("%s: %s", repoException.toString(), urlStr));
			return null;
		}
	}
	
	static URL getBuiltInURL() {
		String urlStr = "platform:/plugin/SoMeEmbededBrowser/data/terms.xml";
		try {
			return new URL(urlStr);
		} catch (MalformedURLException builtInException) {
			System.out.println(String.format("%s: %s", builtInException.toString(), urlStr));
			return null;
		}
	}
	
	static void requestTerms() {
		InputStream inputStream = null;
		try {
			System.out.println("Requesting from Repo");
			inputStream = getRepoURL().openConnection().getInputStream();
		} catch (Exception repoException) {
			System.out.println(String.format("%s: Request from Repo failed.", repoException.toString()));
			try {
				System.out.println("Requesting from built in file");
				inputStream = getRepoURL().openConnection().getInputStream();
			} catch (Exception builtInException) {
				System.out.println(String.format("%s: Request from built in file failed.", builtInException.toString()));
			}
		}
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(inputStream);
			NodeList nl = doc.getElementsByTagName("term");
			terms = new String[nl.getLength()];
			for ( int i=0; i<nl.getLength(); i++ ) {
				terms[i] = innerXml(doc.getElementsByTagName("text").item(i));
				// System.out.println(terms[i]);
			}
			inputStream.close();
		} catch (ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String[] getTerms() {
		if ( terms == null ) {
			requestTerms();
		}
		return terms;
	}
	
	public static String innerXml(Node node) {
		DOMImplementationLS lsImpl = (DOMImplementationLS)node.getOwnerDocument().getImplementation().getFeature("LS", "3.0");
		LSSerializer lsSerializer = lsImpl.createLSSerializer();
		lsSerializer.getDomConfig().setParameter("xml-declaration", false);
		NodeList childNodes = node.getChildNodes();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < childNodes.getLength(); i++) {
			sb.append(lsSerializer.writeToString(childNodes.item(i)));
		}
		return sb.toString(); 
	}
	
	public static char[] getTermsPrefixes() {
		if ( termsPrefixes == null ) {
			termsPrefixes = new char[getTerms().length];
			for ( int i=0; i < getTerms().length; i++ ) {
				// System.out.println(String.format("terms length: %s", getTerms().length));
				termsPrefixes[i] = getTerms()[i].charAt(0);
			}
		}
		return termsPrefixes;
	}
}
