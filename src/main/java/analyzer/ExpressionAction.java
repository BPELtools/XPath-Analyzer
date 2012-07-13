/**
 * @author Bastian Schurr
 * 
 * Institut für Architektur von Anwendungssystemen
 * Universität Stuttgart
 * Universitätsstraße 38
 * D–70569 Stuttgart
 * 
 */
package analyzer;


public class ExpressionAction {
	
	/**
	 * Gibt den Namen der Variablen eines Pfadausdrucks zurück.
	 * @param xpathExpression der Pfadausdruck
	 * @return Name der Variablen des Pfadausdrucks
	 */
	public String getVariableName(String xpathExpression){
		if (xpathExpression.contains("/")){
			String varMsg = xpathExpression.substring(0, xpathExpression.indexOf("/"));
			
			if (varMsg.contains(".")){
				return varMsg.substring(1, varMsg.indexOf("."));
			}else{
				return varMsg.substring(1);
			}
		}else{
			if (xpathExpression.contains(".")){
				return xpathExpression.substring(1, xpathExpression.indexOf("."));
			}else{
				return xpathExpression.substring(1);
			}
		}
	}
	
	/**
	 * Überprüft, ob der Pfadausdruck einen PartName enthält.
	 * @param xpathExpression der Pfadausdruck
	 * @return true, falls ein PartName enthalten ist, sonst false
	 */
	public boolean hasPartName(String xpathExpression){
		if (xpathExpression.contains("/")){
			String varPart = xpathExpression.substring(0, xpathExpression.indexOf("/"));
			return varPart.contains(".");
		}else{
			return xpathExpression.contains(".");
		}
	}
	
	/**
	 * Gibt den PartName eines Pfadausdrucks zurück.
	 * @param xpathExpression der Pfadausdruck
	 * @return
	 */
	public String getPartName(String xpathExpression){
		if (xpathExpression.contains("/")){
			String varPart = xpathExpression.substring(0, xpathExpression.indexOf("/"));
			return varPart.substring(varPart.indexOf(".")+1);
		}else{
			return xpathExpression.substring(xpathExpression.indexOf(".")+1);
		}
	}
	
	/**
	 * Überprüft ob der Pfadausdruck einen Lokalisierungspfad enthält.
	 * @param xpathExpression der Pfadausdruck
	 * @return xpathExpression
	 */
	public boolean hasLocationpath(String xpathExpression){
		if (xpathExpression.contains("/")){
			return true;			
		}else{
			return false;
		}
	}
	
	/**
	 * Gibt den Lokalisierungspfad eines Pfadausdrucks zurück.
	 * @param xpathExpression der Pfadausdruck
	 * @return locationpath
	 */
	public String getLocationpath(String xpathExpression){
		if (xpathExpression.contains("/")){
			int pos = xpathExpression.indexOf("/");	
			
			return xpathExpression.substring(pos);			
		}else{
			return xpathExpression;
		}
	}
	
	/**
	 * Ersetzt "descendant-or-self::node()/child::" durch "descendant-or-self::", falls der
	 * eingegebene Lokalisierungspfad "descendant-or-self::node()/child::" enthält.
	 * @param locationpath der Lokalisierungspfad
	 * @return locationpath
	 */
	public String removeDescendantNode(String locationpath){
		boolean flag = true;
		
		while (flag){
			if (locationpath.contains("descendant-or-self::node()/child::")){	
				locationpath = locationpath.replace("descendant-or-self::node()/child::", "descendant-or-self::");			
			}else{
				flag = false;
			}
		}
		return locationpath;
	}
	
	//wird nicht benutzt
	/**
	 * Testet, ob innerhalb eines Lokalisierungspfads eckige Klammern auftreten
	 * @param locationpath der Lokalisierungspfad
	 * @return true, falls enthalten, sonst false
	 */
	public boolean containsPredicate(String locationpath){
		return locationpath.contains("[");
	}
	
	//zum unterscheiden ob MuSatSolver eingesetzt werden kann oder nicht
	/**
	 * Testet, ob attribute:: oder @ in einem Lokalisierungspfad enthalten ist
	 * @param locationpath der Lokalisierungspfad
	 * @return true, falls enthalten, sonst false
	 */
	public boolean containsAttribute(String locationpath){
		if (locationpath.contains("@")  || locationpath.contains("attribute::")){
			return true;
		}else{
			return false;
		}
	}
	
	//zum unterscheiden ob MuSatSolver eingesetzt werden kann oder nicht
	/**
	 * Testet, ob =, < oder > in einem Lokalisierungspfad enthalten ist
	 * @param locationpath der Lokalisierungspfad
	 * @return true, falls enthalten, sonst false
	 */
	public boolean containsRelOp(String locationpath){
		if (locationpath.contains("=") || locationpath.contains("<") || locationpath.contains(">")){
			return true;
		}else{
			return false;
		}
	}
}