/**
 * @author Bastian Schurr
 * 
 * Institut für Architektur von Anwendungssystemen
 * Universität Stuttgart
 * Universitätsstraße 38
 * D–70569 Stuttgart
 * 
 */
package parser;

import java.io.File;
import java.io.IOException;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;


public class XMLParser {

	/**
	 * Parst die eingegebene XML-Datei und gibt deren Rootelement zurück.
	 * @param xmlFileName zu parsende XML-Datei
	 * @return Rootelement der eingegebenen XML-Datei
	 */
	public Element parseXMLFile(String xmlFileName){
		try{
			SAXBuilder builder = new SAXBuilder();
			Document doc = builder.build(new File(xmlFileName));
		
			return doc.getRootElement();
		}catch (IOException e) {
			System.err.println(e);
			return null;
		}catch (JDOMException e) {
			System.err.println(e);
			return null;
		}	
	}
}
