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

import org.jdom.Element;

import parser.XMLParser;

import control.BPELReader;


public class PathAnalyzer {
	private String literal1;
	private String literal2;
	private String varLoc1;
	private String varLoc2;
	private String bpelFileName;
	private boolean boolean1;
	private boolean boolean2;
	private float num1;
	private float num2;
	
	/**
	 * Überprüft, ob zwei XPath-Ausdrücke gleichzeitig zutreffen können
	 * @param pathExpression1 Pfadausdruck der ersten Bedingung
	 * @param pathExpression2 Pfadausdruck der zweiten Bedingung
	 * @param operator1 Operator der ersten Bedingung
	 * @param operator2 Operator der zweiten Bedingung
	 * @param type1 Typ der ersten Bedingung
	 * @param type2 Typ der zweiten Bedingung
	 * @return true, falls die XPath-Ausdrücke gleichzeitig zutreffen können, sonst false
	 */
	public boolean isConcurrent(String pathExpression1, String pathExpression2, String operator1, String operator2, String type1, String type2){
		ExpressionAction exprAction = new ExpressionAction();
		//Auslesen des VariablenName
		String varName1 = exprAction.getVariableName(pathExpression1);
		String varName2 = exprAction.getVariableName(pathExpression2);
		
		/*
		 * 1.1 Variable.MsgPart
		 * 1.1.1.1 Variable.MsgPart/Locationpath
		 * 1 Variable
		 * 1.2.1 Variable/Locationpath
		 */
		
		//1 Falls die Variablen gleich sind
		if (varName1.equals(varName2)){
			LocationpathAnalyzer locAnalyzer = new LocationpathAnalyzer();
			
			//1.1 Falls beide Pfadausdrücke einen Part besitzen
			if (exprAction.hasPartName(pathExpression1) && exprAction.hasPartName(pathExpression2)){
				//Auslesen des PartNamens
				String partName1 = exprAction.getPartName(pathExpression1);
				String partName2 = exprAction.getPartName(pathExpression2);
				
				//1.1.1 Falls die Parts gleich sind
				if (partName1.equals(partName2)){
					//1.1.1.1 Falls beide Pfadausdrücke je einen Lokalisierungspfad besitzen
					if (exprAction.hasLocationpath(pathExpression1) && exprAction.hasLocationpath(pathExpression2)){
						//Auslesen des Lokalisierungspfades und Bereinigen desselben
						String locationpath1 = exprAction.getLocationpath(pathExpression1);
						locationpath1 = exprAction.removeDescendantNode(locationpath1);
						String locationpath2 = exprAction.getLocationpath(pathExpression2);
						locationpath2 = exprAction.removeDescendantNode(locationpath2);
						
						//1.1.1.1.1 Falls die Lokalisierungspfade keine Attribute und keine relationalen Operatoren beinhalten
						if (!exprAction.containsAttribute(locationpath1) && !exprAction.containsAttribute(locationpath2) && !exprAction.containsRelOp(locationpath1) && !exprAction.containsRelOp(locationpath2)){
							
							//1.1.1.1.1.1 Falls die Typen der beiden Bedingungen identisch sind
							if (type1.equals(type2)){
								XMLParser xmlParser = new XMLParser();
								Element bpelRoot = xmlParser.parseXMLFile(bpelFileName);
								
								//Falls die Variable einen part besitzt, besitzt die Variable automatisch ein messageType Attribut
								BPELReader bpelReader = new BPELReader();
								String msgName = bpelReader.getVariableAttribute(bpelRoot, varName1, "messageType");
//@WSDL-BEGIN								
								//aus bpelFile Name des entsprechenden wsdlFiles holen 
									
								//mit WSDL4J auf message und part in wsdlFile zugreifen mit msgName und partName1
								//xsd aus type-Attribut (immer complexType, da locationpath existiert) bzw. element-Attribut holen
								//Da der MuSatSolver noch nicht vollsändig vorliegt kann das XML-Schema nicht zum
								//Schnitttest herangezogen werden. Daher wird an dieser Stelle nur eine Zeichenkette übergeben. 
								String xsdFileName = msgName.concat(partName1).concat(".xsd");
//@WSDL-END							
								boolean existsIntersect = locAnalyzer.existsIntersection(locationpath1, locationpath2, xsdFileName);
									
								//Falls sich die Lokalisierungspfade schneiden
								if (existsIntersect){
									return mutuallyExclusive(operator1, operator2, type1, type2);
										
								//Falls die Lokalisierungspfade verschieden sind (sie sich nicht schneiden)
								} else {
									return true;
								}
							//1.1.1.1.1.2 Falls die Typen der beiden Bedingungen verschieden sind
							} else {
								System.err.println("Die Datentypen in den Bedingungen sind von unterschiedlichem Typ. Die Forderung, dass nur dieselben Typen verglichen werden koennen, wird verletzt!");
								System.exit(0);
								return false;
							}
						//1.1.1.1.2 Falls die Lokalisierungspfade Attribute oder relationale Operatoren enthalten
						} else if ((exprAction.containsAttribute(locationpath1) && exprAction.containsAttribute(locationpath2)) ||  exprAction.containsRelOp(locationpath1) && exprAction.containsRelOp(locationpath2)){
							
							//1.1.1.1.2.1 Falls die Typen der beiden Bedingungen identisch sind
							if (type1.equals(type2)){
								boolean expressionsEqual = locAnalyzer.expressionsEqual(locationpath1, locationpath2);
								
								//1.1.1.1.2.1.1 Falls die Lokalisierungspfade äquivalent sind
								if (expressionsEqual){
									return mutuallyExclusive(operator1, operator2, type1, type2);
									
								//1.1.1.1.2.1.2 Falls die Lokalisierungspfade verschieden sind
								} else {
									return true;
								}
							//1.1.1.1.2.2 Falls die Typen der beiden Bedingungen verschieden sind
							} else {
								System.err.println("Die Datentypen in den Bedingungen sind von unterschiedlichem Typ. Die Forderung, dass nur dieselben Typen verglichen werden koennen, wird verletzt!");
								System.exit(0);
								return false;
							}						
						//1.1.1.1.3 Falls nur ein Lokalsierungspfad Attribute enthält 	
						} else {
							System.err.println("Lokalisierungspfade "+locationpath1+ " und "+locationpath2+" koennen nicht analysiert werden. Entweder enthalten beide Lokalisierungspfade Attribute oder keiner von beiden!");
							System.exit(0);
							return false;
						}
					//1.1.1.2 Falls beide Pfadausdrücke keinen Lokalisierungspfad besitzen
					} else if (!exprAction.hasLocationpath(pathExpression1) && !exprAction.hasLocationpath(pathExpression2)){
					
						//1.1.1.2.1 Falls die Typen der beiden Bedingungen identisch sind
						if (type1.equals(type2)){
							return mutuallyExclusive(operator1, operator2, type1, type2);
							
						//1.1.1.2.2 Falls die Typen der beiden Bedingungen verschieden sind
						} else {
							System.err.println("Die Datentypen in den Bedingungen sind von unterschiedlichem Typ. Die Forderung, dass nur dieselben Typen verglichen werden koennen, wird verletzt!");
							System.exit(0);
							return false;
						}					
					//1.1.1.3 Falls nur einer der Pfadausdrücke einen Lokalisierungspfad besitzt 
					} else {
						System.err.println("Pfadausdruecke "+ pathExpression1+ " und " +pathExpression2+" nicht zulaessig, entweder besitzen beide Pfadausdrücke einen Lokalisierungspfad oder keiner von beiden!");
						System.exit(0);
						return false;
					}
				//1.1.2 Falls die Parts verschieden sind	
				} else {
					return true;
				}
			//1.2 Falls beide Pfadausdrücke keinen Part besitzen
			} else if (!exprAction.hasPartName(pathExpression1) && !exprAction.hasPartName(pathExpression2)){
				//1.2.1 Falls beide Pfadausdrücke einen Lokalisierungspfad besitzen
				if (exprAction.hasLocationpath(pathExpression1) && exprAction.hasLocationpath(pathExpression2)){
					//Auslesen des Lokalisierungspfades und Bereinigen desselben
					String locationpath1 = exprAction.getLocationpath(pathExpression1);
					locationpath1 = exprAction.removeDescendantNode(locationpath1);
					String locationpath2 = exprAction.getLocationpath(pathExpression2);
					locationpath2 = exprAction.removeDescendantNode(locationpath2);
					
					//1.2.1.1 Falls die Lokalisierungspfade keine Attribute enthalten und keine relationalen Operatoren beinhalten
					if (!exprAction.containsAttribute(locationpath1) && !exprAction.containsAttribute(locationpath2) && !exprAction.containsRelOp(locationpath1) && !exprAction.containsRelOp(locationpath2)){						
						
						//1.2.1.1.1 Falls die Typen der beiden Bedingungen identisch sind
						if (type1.equals(type2)){
							XMLParser xmlParser = new XMLParser();
							Element bpelRoot = xmlParser.parseXMLFile(bpelFileName);
							
							//aus bpelFile Name des entsprechenden wsdlFiles holen
							BPELReader bpelReader = new BPELReader();
							String xsdFileName = null;
//@WSDL-BEGIN						
							//Falls die Variable ein element-Attribut besitzt
							if (bpelReader.hasVariableAttribute(bpelRoot, varName1, "element")){
								String elementName = bpelReader.getVariableAttribute(bpelRoot, varName1, "element");
								
								//mit WSDL4J xsd über element-attribut holen
								xsdFileName = elementName+".xsd";
						
							//Falls die Variable ein type-Attribut besitzt (muss sie dann)
							} else {
								String typeName = bpelReader.getVariableAttribute(bpelRoot, varName1, "type");
								
								//mit WSDL4J xsd über type-Attribut (immer complexType, da locationpath existiert) holen
								xsdFileName = typeName+".xsd";
							}
//@WSDL-END						
							//Da der MuSatSolver noch nicht vollsändig vorliegt kann das XML-Schema nicht zum
							//Schnitttest herangezogen werden. Daher wird an dieser Stelle nur eine Zeichenkette übergeben.
							boolean existsIntersect = locAnalyzer.existsIntersection(locationpath1, locationpath2, xsdFileName);
							
							//Falls sich die Lokalisierungspfade schneiden
							if (existsIntersect){
								return mutuallyExclusive(operator1, operator2, type1, type2);
								
							//Falls die Lokalisierungspfade verschieden sind
							} else {
								return true;
							}
							//1.2.1.1.2 Falls die Typen der beiden Bedingungen verschieden sind
						} else {
							System.err.println("Die Datentypen in den Bedingungen sind von unterschiedlichem Typ. Die Forderung, dass nur dieselben Typen verglichen werden koennen, wird verletzt!");
							System.exit(0);
							return false;
						}
					//1.2.1.2 Falls die Lokalisierungspfade Attribute oder relationale Operatoren enthalten
					} else if ((exprAction.containsAttribute(locationpath1) && exprAction.containsAttribute(locationpath2)) ||  exprAction.containsRelOp(locationpath1) && exprAction.containsRelOp(locationpath2)){
						
						//1.2.1.2.1 Falls die Typen der beiden Bedingungen identisch sind
						if (type1.equals(type2)){
							boolean expressionsEqual = locAnalyzer.expressionsEqual(locationpath1, locationpath2);
							
							//1.2.1.2.1.1 Falls die Lokalisierungspfade äquivalent sind
							if (expressionsEqual){
								return mutuallyExclusive(operator1, operator2, type1, type2);

							//1.2.1.2.1.2 Falls die Lokalisierungspfade verschieden sind
							} else {
								return true;
							}
						//1.2.1.2.2 Falls die Typen der beiden Bedingungen verschieden sind
						} else {
							System.err.println("Die Datentypen in den Bedingungen sind von unterschiedlichem Typ. Die Forderung, dass nur dieselben Typen verglichen werden koennen, wird verletzt!");
							System.exit(0);
							return false;
						}
					//1.2.1.2 Falls nur ein Lokalsierungspfad Attribute enthält
					} else {
						System.err.println("Lokalisierungspfade "+locationpath1+ " und "+locationpath2+" koennen nicht analysiert werden. Entweder enthalten beide Lokalisierungspfade Attribute oder keiner von beiden!");
						System.exit(0);
						return false;
					}
				//1.2.2 Falls keiner der Pfadausdrücke einen Lokalisierungspfad besitzt
				} else if (!exprAction.hasLocationpath(pathExpression1) && !exprAction.hasLocationpath(pathExpression2)){
					
					//1.2.2.1 Falls die Typen der beiden Bedingungen identisch sind
					if (type1.equals(type2)){
						return mutuallyExclusive(operator1, operator2, type1, type2);
						
					//1.2.2.2 Falls die Typen der beiden Bedingungen verschieden sind
					} else {
						System.err.println("Die Datentypen in den Bedingungen sind von unterschiedlichem Typ. Die Forderung, dass nur dieselben Typen verglichen werden koennen, wird verletzt!");
						System.exit(0);
						return false;
					}
				//1.2.3 Falls nur einer der Pfadausdrücke einen Lokalisierungspfad besitzt
				} else {
					System.err.println("Pfadausdruecke "+ pathExpression1+ " und " +pathExpression2+" nicht zulaessig, entweder besitzen beide Pfadausdruecke einen Lokalisierungspfad oder keiner von beiden!");
					System.exit(0);
					return false;
				}
			//1.3 Falls nur ein Pfadausdruck einen Part besitzt
			} else {
				System.err.println("Pfadausdruecke "+ pathExpression1+ " und " +pathExpression2+" nicht zulaessig, entweder besitzen beide Pfadausdruecke einen Part oder keiner von beiden!");
				System.exit(0);
				return false;
			}
		//1 Falls die Variablen verschieden sind
		} else {
			return true;
		}
	}

	//Für den Test zweier direkt eingegebener XPath-Ausdrücke
	/**
	 * Überprüft, ob zwei XPath-Ausdrücke (unabhängig von einem BPEL-File) gleichzeitig zutreffen können
	 * @param pathExpression1 Pfadausdruck der ersten Bedingung
	 * @param pathExpression2 Pfadausdruck der zweiten Bedingung
	 * @param operator1 Operator der ersten Bedingung
	 * @param operator2 Operator der zweiten Bedingung
	 * @param type1 Typ der ersten Bedingung
	 * @param type2 Typ der zweiten Bedingung
	 * @return true, falls die XPath-Ausdrücke gleichzeitig zutreffen können, sonst false
	 */
	public boolean simpleIsConcurrent(String pathExpression1, String pathExpression2, String operator1, String operator2, String type1, String type2){
		if (pathExpression1.equals(pathExpression2)){
			if (type1.equals(type2)){
				return mutuallyExclusive(operator1, operator2, type1, type2);
			} else {
				System.err.println("Die Datentypen in den Bedingungen sind von unterschiedlichem Typ. Die Forderung, dass nur dieselben Typen verglichen werden koennen, wird verletzt!");
				System.exit(0);
				return false;
			}
		} else {
			return true;
		}
	}
	
	/**
	 * Gibt zurück, ob sich die beiden Bedingungen gegenseitig ausschließen.
	 * @param operator1
	 * @param operator2
	 * @param type1
	 * @param type2
	 * @return
	 */
	private boolean mutuallyExclusive(String operator1, String operator2, String type1, String type2){
		ConditionAnalyzer conditionAnalyzer = new ConditionAnalyzer();
		
		//Falls die Bedingungen vom Typ Boolean sind
		if (type1.equals("Boolean")){
			return conditionAnalyzer.typeBoolean(operator1, boolean1, operator2, boolean2);
		
		//Falls die Bedingungen vom Typ String sind
		} else if (type1.equals("String")){
			return conditionAnalyzer.typeString(operator1, literal1, operator2, literal2);
		
		//Falls die Bedingungen vom Typ Number sind
		} else if (type1.equals("Number")){
			return conditionAnalyzer.typeNumber(operator1, num1, operator2, num2);
		
		//Falls die Bedingungen vom Typ PathExpression sind
		} else if (type1.equals("PathExpression")){
			return conditionAnalyzer.typeVarLoc(operator1, varLoc1, operator2, varLoc2);
		
		//Dieser Fall kann eigentlich nie auftreten, da die Typen selbst generiert wurden
		} else {
			System.err.println("Typ der Variablen existiert nicht!");
			System.exit(0);
			return false;
		}
	} 
	
	/**
	 * @param  boolean1
	 * @uml.property  name="boolean1"
	 */
	public void setBoolean1(boolean boolean1) {
		this.boolean1 = boolean1;
	}

	/**
	 * @param  boolean2
	 * @uml.property  name="boolean2"
	 */
	public void setBoolean2(boolean boolean2) {
		this.boolean2 = boolean2;
	}

	/**
	 * @param  literal1
	 * @uml.property  name="literal1"
	 */
	public void setLiteral1(String literal1) {
		this.literal1 = literal1;
	}

	/**
	 * @param  literal2
	 * @uml.property  name="literal2"
	 */
	public void setLiteral2(String literal2) {
		this.literal2 = literal2;
	}

	/**
	 * @param  num1
	 * @uml.property  name="num1"
	 */
	public void setNum1(float num1) {
		this.num1 = num1;
	}

	/**
	 * @param  num2
	 * @uml.property  name="num2"
	 */
	public void setNum2(float num2) {
		this.num2 = num2;
	}

	/**
	 * @param  varLoc1
	 * @uml.property  name="varLoc1"
	 */
	public void setVarLoc1(String varLoc1) {
		this.varLoc1 = varLoc1;
	}

	/**
	 * @param  varLoc2
	 * @uml.property  name="varLoc2"
	 */
	public void setVarLoc2(String varLoc2) {
		this.varLoc2 = varLoc2;
	}

	/**
	 * @param bpelFileName
	 */
	public void setBPELFileName(String FileName) {
		this.bpelFileName = bpelFileName;
	}
}
