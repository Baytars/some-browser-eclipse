package cn.gedobu.some.editor.med.grammar;

import java.io.IOException;
import java.io.InputStream;
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
	
	public static String[] getTerms() {
		if ( terms == null ) {
			URL url = null;
			try {
				url = new URL("platform:/plugin/SoMeEmbededBrowser/data/terms.xml");
			}
			catch (Exception e) {
				// TODO: handle exception
			}
			InputStream inputStream = null;
			try {
				inputStream = url.openConnection().getInputStream();
//				BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
//				String inputLine;
//				 
//				while ((inputLine = in.readLine()) != null) {
//				System.out.println(inputLine);
//				}
			} catch (IOException e1) {
				e1.printStackTrace();
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
