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

import org.jaxen.JaxenHandler;
import org.jaxen.expr.Expr;
import org.jaxen.expr.XPathExpr;
import org.jaxen.saxpath.SAXPathException;
import org.jaxen.saxpath.XPathReader;
import org.jaxen.saxpath.helpers.XPathReaderFactory;


public class XPathParser {

	/**
	 * Parst den eingegebenen XPath-Ausdruck
	 * @param xpathExpression
	 * @return Expr geparster XPath-Ausdruck
	 */
	public Expr parseExpression(String xpathExpression){
		try {
			JaxenHandler handler = new JaxenHandler();
			
			XPathReader reader = XPathReaderFactory.createReader();
			reader.setXPathHandler(handler);
            //Parse an XPath expression, and send event callbacks to an XPathHandler.
			reader.parse(xpathExpression); 

			XPathExpr xpath = handler.getXPathExpr();
			return xpath.getRootExpr();

		} catch (SAXPathException e) {
			e.printStackTrace();
			return null;
		}
	}
}
