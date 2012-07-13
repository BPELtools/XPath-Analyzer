/**
 * @author Bastian Schurr
 * 
 * Institut für Architektur von Anwendungssystemen
 * Universität Stuttgart
 * Universitätsstraße 38
 * D–70569 Stuttgart
 * 
 */
package control;

import java.util.Vector;

import org.jdom.Element;
import org.jdom.Namespace;


public class BPELReader {
	
	
	/**
	 * Gibt zurück, ob die eingegebene Variable das eingegebene Attribut besitzt.
	 * @param varName
	 * @param attributName
	 * @return true, falls die angegebene Variable ein solches Attribut besitzt, sonst false
	 */	
	public boolean hasVariableAttribute(Element root, String varName, String attributName){
		Namespace ns = root.getNamespace();
		
		Element variables  = root.getChild("variables", ns);
		boolean exists = false;
		
		for (int i=0; i < variables.getChildren().size(); i++){
			Element variable  = (Element)variables.getChildren().get(i);
						
			if (variable.getAttributeValue("name").equals(varName)){
				if (variable.getAttribute(attributName) != null){
					exists = true;
				}
			}
		}
		return exists;
	}

	/**
	 * Gibt den Wert des eingegebenen Attributs der eingegebenen Variablen zurück.
	 * @param varName
	 * @param attributName
	 * @return value
	 */
	public String getVariableAttribute(Element root, String varName, String attributName){
		Namespace ns = root.getNamespace();
		
		Element variables  = root.getChild("variables", ns);
		String value = null;
		
		for (int i=0; i < variables.getChildren().size(); i++){
			Element variable  = (Element)variables.getChildren().get(i);
						
			if (variable.getAttributeValue("name").equals(varName)){
				value = variable.getAttributeValue(attributName);
				value = removeNamespace(value);
			}
		}
		return value;
	}
	
	/**
	 * Gibt den eingegebenen String ohne seinen NameSpace zurück, falls der String ein NameSpace
	 * Prefix besitzt.
	 * @param string
	 * @return string ohne NameSpace Prefix
	 */
	private String removeNamespace(String string){
		if (string.contains(":")){
			return string.substring(string.indexOf(":")+1);
		}else{
			return string;
		}
	}
	
	/**
	 * Gibt einen Vector mit den sources Elementen eines XML-Dokuments zurück. 
	 * @return Vector mit den sources Elementen
	 */
	public Vector getSources(Element root){
		Vector vect = new Vector();
		searchElement(root, "sources", vect);
		return vect;
	}
	
	/**
	 * Gibt die transitionCondition des eingegebenen transitionCondition Elements zurück.
	 * @param tcElement das transitionCondition Element
	 * @return transitionCondition als String, falls es sich bei der Eingabe um ein 
	 * transitionCondition Element handelt, sonst null
	 */
	public String getTransitionCondition(Element tcElement){	
		if (tcElement.getName().equals("transitionCondition")){
			return tcElement.getText();
		}else{
			return null;
		}
	}
	
	/**
	 * Gibt den Namen des Links auf dem die transitionCondition des eingegebenen 
	 * transitionCondition Elements definiert ist zurück. 
	 * @param tcElement das transitionCondition Element
	 * @return Name des Links, auf dem die transitionCondition definiert ist, falls
	 * es sich bei der Eingabe um ein transitionCondition Element handelt, sonst null
	 */
	public String getLinkName(Element tcElement){	
		if (tcElement.getName().equals("transitioncondition")){
			Element source = (Element)tcElement.getParentElement();
			return source.getAttributeValue("linkName");
		}else{
			return null;
		}
	}
	
	/**
	 * Sucht unter den Kindern des eingegebenen Elements nach Elementen mit dem eingegebenen
	 * Namen und schreibt diese in den Vector
	 * @param elementName Name des zu suchenden Elements
	 * @param element Startelement der Suche
	 * @param vect Vector, in den die Ergebnisse eingetragen werden
	 */
	public void searchElement(Element root, String elementName, Vector vect){
		
		for (int i=0; i < root.getChildren().size(); i++){
			Element child  = (Element)root.getChildren().get(i);
			
			if (child.getName().equals(elementName)){
				vect.addElement(child);
			}else{
				searchElement(child, elementName, vect);
			}			
		}
	}
}
