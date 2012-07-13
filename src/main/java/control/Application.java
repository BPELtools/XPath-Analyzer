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


import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.jaxen.expr.EqualityExpr;
import org.jaxen.expr.Expr;
import org.jaxen.expr.FunctionCallExpr;
import org.jaxen.expr.LiteralExpr;
import org.jaxen.expr.LogicalExpr;
import org.jaxen.expr.NumberExpr;
import org.jaxen.expr.RelationalExpr;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.xml.sax.SAXException;

import parser.XMLParser;
import parser.XPathParser;

import analyzer.PathAnalyzer;


public class Application {
	private String bpelFileName;
	private boolean bpelTest = true;
	
	public Application()throws JDOMException, IOException, SAXException{}
	
	/**
	 * Die Methode help gibt auf die Eingabe  "/?" oder "--help" Beispiele für XPath-Ausdrücke aus.
	 */
	public void help(){
		System.out.println("Es sind nur XPath-Ausdruecke wie in den folgenden Beispielen erlaubt. Diese Ausdruecke muessen als String-Argumente uebergeben werden.");
		System.out.println("(a): ($X = 5.0 or $X < 5.0) and $Y < 8.9");
		System.out.println("(b): $X > 5.0 and $Y > 5.0");
		System.out.println("Das Ergebnis fuer (a)||(b) ist false!");
		System.out.println("");
		System.out.println("(c): ($X <= 5.0 and $Y > 7.0) or $Z = 8.9");
		System.out.println("(d): $X = 5.0 and $Y = 8.0");
		System.out.println("Das Ergebnis fuer (c)||(d) ist true!");
		System.out.println("");
		System.out.println("Falls nur ein Argument uebergeben wird, muss es sich um eine BPEL-Datei handeln, z. B. \"loanApproval.bpel\".");
		System.out.println("Die XPath-Ausdruecke innerhalb der BPEL-Datei muessen alle die obige Form besitzen.");
	}
	
	/**
	 * Testet, ob zwei einzugebende XPath-Ausdrücke gleichzeitig zutreffen können.
	 * @param xpathExpr1
	 * @param xpathExpr2
	 */
	public void analyzeXPathExpressions(String xpathExpr1, String xpathExpr2){
		XPathParser xpathParser = new XPathParser();
		Expr transitionExpr1 = xpathParser.parseExpression(xpathExpr1);
		Expr transitionExpr2 = xpathParser.parseExpression(xpathExpr2);
		
		//zeigt an, dass hier kein BPEL-Prozess analysiert wird
		bpelTest = false;
		
		boolean result = handleFirstExpression(transitionExpr1, transitionExpr2);
		
		//Ausgabe
		System.out.println("XPathExpr1: "+transitionExpr1.getText());
		System.out.println("XPathExpr2: "+transitionExpr2.getText());
		System.out.println("");
		if (result){
			System.out.println("XPathExpr1 || XPathExpr2 = "+result);
			System.out.println("Die XPath-Ausdruecke koennen gleichzeitig zutreffen.");
		}else{
			System.out.println("XPathExpr1 || XPathExpr2 = "+result);
			System.out.println("Die XPath-Ausdruecke koennen nicht gleichzeitig zutreffen.");
		}
	}
	
	/**
	 * Testet für jede Aktivität des Prozesses deren ausgehende Links, ob diese paarweise gleichzeitig zutreffen können.
	 */
	public void analyzeBPELProcess(){
		//zeigt an, dass hier ein BPEL-Prozess analysiert wird
		bpelTest = true;
		
		XMLParser xmlParser = new XMLParser();
		Element bpelRoot = xmlParser.parseXMLFile(bpelFileName);
		
		Namespace ns = bpelRoot.getNamespace();

		BPELReader bpelReader = new BPELReader();
		Vector sources = bpelReader.getSources(bpelRoot);
		
		Vector simultaneousLinks = new Vector();
		
		//Analysiert die ausgehenden Links jeder Aktivität
		for (int i=0; i < sources.size(); i++){
			Element singleSources = (Element)sources.get(i);
			List sourcesChildren = singleSources.getChildren();
			
			if (sourcesChildren.size() > 1){
				analyzeLinks(sourcesChildren, simultaneousLinks, ns);
			}
		}
		
		//Ausgabe
		if (simultaneousLinks.size() == 0){
			System.out.println("In der Datei "+bpelFileName+" sind keine Links vorhanden.");
		} else {
			System.out.println("Die XPath-Analyse ergab, dass die folgenden Paare von Links gleichzeitig zutreffen koennen:");
			for (int i=0; i < simultaneousLinks.size(); i++){
				Vector resultVect = (Vector)simultaneousLinks.get(i);
				String result = resultVect.get(0).toString();
				
				if (result.equals("sim")){
					System.out.println("  "+resultVect.get(1).toString()+" || "+resultVect.get(2).toString());
				}
			}
			System.out.println("");
			
			System.out.println("Die XPath-Analyse ergab, dass die folgenden Paare von Links nicht gleichzeitig zutreffen koennen:");
			for (int i=0; i < simultaneousLinks.size(); i++){
				Vector resultVect = (Vector)simultaneousLinks.get(i);
				String result = resultVect.get(0).toString();
				
				if (result.equals("notsim")){
					System.out.println("  "+resultVect.get(1).toString()+" not|| "+resultVect.get(2).toString());
				}
			}
		}
	}
	
	/**
	 * Überprüft, ob je zwei ausgehende Links einer Aktivität gleichzeitig zutreffen können und 
	 * trägt das Ergebnis in den übergebenen Vector ein.
	 * @param sourcesChildren Liste der ausgehenden Links einer Aktivität
	 * @param simultaneousLinks Ergebnisvector
	 */
	public void analyzeLinks(List sourcesChildren, Vector simultaneousLinks, Namespace ns){
		if (sourcesChildren.size() > 1){
			Element source1 = (Element)sourcesChildren.get(0);
			String linkName1 = source1.getAttributeValue("linkName");
			
			//Falls der Link eine transitionCondition besitzt
			if (source1.getChild("transitionCondition", ns) != null){	
				String tc1 = source1.getChild("transitionCondition", ns).getText();
				
				for (int i=1; i < sourcesChildren.size(); i++){
					Element source2 = (Element)sourcesChildren.get(i);
					String linkName2 = source2.getAttributeValue("linkName");
					
					Vector vect = new Vector();
					simultaneousLinks.addElement(vect);
					
					//Falls der Link eine transitionCondition besitzt
					if (source2.getChild("transitionCondition", ns) != null){
						String tc2 = source2.getChild("transitionCondition", ns).getText();
						
						XPathParser xpathParser = new XPathParser();
						Expr transitionExpr1 = xpathParser.parseExpression(tc1);
						Expr transitionExpr2 = xpathParser.parseExpression(tc2);
						
						boolean result = handleFirstExpression(transitionExpr1, transitionExpr2);
						
						if (result){
							vect.addElement("sim");
							vect.addElement(linkName1);
							vect.addElement(linkName2);
						} else {
							vect.addElement("notsim");
							vect.addElement(linkName1);
							vect.addElement(linkName2);
						}
					//Falls der Link keine transtionCondition besitzt
					} else {						
						vect.addElement("sim");
						vect.addElement(linkName1);
						vect.addElement(linkName2);
					}
				}
			//Falls der Link keine transtionCondition besitzt
			} else {
				for (int i=1; i < sourcesChildren.size(); i++){
					Element source2 = (Element)sourcesChildren.get(i);
					String linkName2 = source2.getAttributeValue("linkName");
					
					Vector vect = new Vector();
					simultaneousLinks.addElement(vect);
					
					vect.addElement("sim");
					vect.addElement(linkName1);
					vect.addElement(linkName2);
				}
			}
			sourcesChildren.remove(0);
			analyzeLinks(sourcesChildren, simultaneousLinks, ns);
		}		
	}
	
	/**
	 * Überprüft, ob die beiden XPath-Ausdrücke gleichzeitig zutreffen können.
	 * @param expr1 der vollständige erste XPath-Ausdruck, dieser wird in einzelne relationale
	 * Bedingungen zerlegt und diese dann zusammen mit expr2 überprüft, ob sie gleichzeitig
	 * zutreffen können
	 * @param expr2 der vollständige erste XPath-Ausdruck
	 * @return true, falls beide Ausdrücke gleichzeitig zutreffen können, andernfalls false
	 */
	public boolean handleFirstExpression(Expr expr1, Expr expr2){
		if (expr1 instanceof LogicalExpr){
			Expr lhs1 = ((LogicalExpr) expr1).getLHS();
			Expr rhs1 = ((LogicalExpr) expr1).getRHS();
			String op1 = ((LogicalExpr) expr1).getOperator();
			
			if (op1.equals("and")){
				return handleFirstExpression(lhs1, expr2) && handleFirstExpression(rhs1, expr2);
			}else{ //op.equals("or")
				return handleFirstExpression(lhs1, expr2) || handleFirstExpression(rhs1, expr2);
			}
		}else{ //if (expr1 instanceof EqualityExpr) oder (expr1 instanceof RelationalExpr)
			return handleSecondExpression(expr2, expr1);
		}
	}
	
	/**
	 * Überprüft, ob ein vollständiger XPath-Ausdruck und eine einzelne Bedingung gleichzeitig 
	 * zutreffen können. 
	 * @param expr2 der vollständige zweite XPath-Ausdruck
	 * @param expr1 eine einzelne relationale Bedingung
	 * @return true, falls die Bedingung und der XPath-Ausdruck gleichzeitig zutreffen können, sonst false
	 */
	private boolean handleSecondExpression(Expr expr2, Expr expr1){
		if (expr2 instanceof LogicalExpr){
			Expr lhs2 = ((LogicalExpr) expr2).getLHS();
			Expr rhs2 = ((LogicalExpr) expr2).getRHS();
			String op2 = ((LogicalExpr) expr2).getOperator();
			
			if (op2.equals("and")){
				return handleSecondExpression(lhs2, expr1) && handleSecondExpression(rhs2, expr1);
			}else{ //op2.equals("or")
				return handleSecondExpression(lhs2, expr1) || handleSecondExpression(rhs2, expr1);
			}
		} else if (expr2 instanceof RelationalExpr){
			PathAnalyzer xpathAnalyzer = new PathAnalyzer();
			xpathAnalyzer.setBPELFileName(bpelFileName);
			
			Expr lhs2 = ((RelationalExpr) expr2).getLHS();
			Expr rhs2 = ((RelationalExpr) expr2).getRHS();
			String op2 = ((RelationalExpr) expr2).getOperator();
			String type2 = getExpressionType(rhs2);
			
			Expr lhs1, rhs1;
			String op1, type1;
			if (expr1 instanceof RelationalExpr){
				lhs1 = ((RelationalExpr) expr1).getLHS();
				rhs1 = ((RelationalExpr) expr1).getRHS();
				op1 = ((RelationalExpr) expr1).getOperator();
				type1 = getExpressionType(rhs1);
				
			}else{ //if (expr1 instanceof EqualityExpr){
				lhs1 = ((EqualityExpr) expr1).getLHS();
				rhs1 = ((EqualityExpr) expr1).getRHS();
				op1 = ((EqualityExpr) expr1).getOperator();
				type1 = getExpressionType(rhs1);
			}
			
			//Füttern von xpathAnalyzer für bestimmten type1
			if (type1.equals("Number")){
				Number num = ((NumberExpr) rhs1).getNumber();
				float numValue = num.floatValue();
				xpathAnalyzer.setNum1(numValue);
				
			}else if(type1.equals("String")){
				String literal = ((LiteralExpr) rhs1).getLiteral();
				xpathAnalyzer.setLiteral1(literal);
				
			}else if(type1.equals("Boolean")){
				String fktName = ((FunctionCallExpr) rhs1).getFunctionName();
				boolean booleanValue = fktName.equals("true()");
				xpathAnalyzer.setBoolean1(booleanValue);
				
			}else{ //if(type1.equals("PathExpression")){
				String expression = ((Expr) rhs1).getText();
				xpathAnalyzer.setVarLoc1(expression);
			}
			
			//Füttern von xpathAnalyzer für bestimmten type2
			if (type2.equals("Number")){
				Number num = ((NumberExpr) rhs2).getNumber();
				float numValue = num.floatValue();
				xpathAnalyzer.setNum2(numValue);
				
			}else if(type2.equals("String")){
				String literal = ((LiteralExpr) rhs2).getLiteral();
				xpathAnalyzer.setLiteral2(literal);
				
			}else if(type2.equals("Boolean")){
				String fktName = ((FunctionCallExpr) rhs2).getFunctionName();
				boolean booleanValue = fktName.equals("true()");
				xpathAnalyzer.setBoolean2(booleanValue);
				
			}else{ //if(type2.equals("PathExpression")){
				String expression = ((Expr) rhs2).getText();
				xpathAnalyzer.setVarLoc2(expression);
			}
			
			//Aufruf von xpathAnalyzer
			if (bpelTest){
				return xpathAnalyzer.isConcurrent(lhs1.getText(), lhs2.getText(), op1, op2, type1, type2);
			} else {
				return xpathAnalyzer.simpleIsConcurrent(lhs1.getText(), lhs2.getText(), op1, op2, type1, type2);
			}
		}else{ //if (expr2 instanceof EqualityExpr){			
			PathAnalyzer xpathAnalyzer = new PathAnalyzer();
			xpathAnalyzer.setBPELFileName(bpelFileName);
			
			Expr lhs2 = ((EqualityExpr) expr2).getLHS();
			Expr rhs2 = ((EqualityExpr) expr2).getRHS();
			String op2 = ((EqualityExpr) expr2).getOperator();
			String type2 = getExpressionType(rhs2);
			
			Expr lhs1, rhs1;
			String op1, type1;
			if (expr1 instanceof RelationalExpr){
				lhs1 = ((RelationalExpr) expr1).getLHS();
				rhs1 = ((RelationalExpr) expr1).getRHS();
				op1 = ((RelationalExpr) expr1).getOperator();
				type1 = getExpressionType(rhs1);
				
			}else{ //if (expr1 instanceof EqualityExpr){
				lhs1 = ((EqualityExpr) expr1).getLHS();
				rhs1 = ((EqualityExpr) expr1).getRHS();
				op1 = ((EqualityExpr) expr1).getOperator();
				type1 = getExpressionType(rhs1);
			}
			
			//Füttern von xpathAnalyzer für bestimmten type1
			if (type1.equals("Number")){
				Number num = ((NumberExpr) rhs1).getNumber();
				float numValue = num.floatValue();
				xpathAnalyzer.setNum1(numValue);
				
			}else if(type1.equals("String")){
				String literal = ((LiteralExpr) rhs1).getLiteral();
				xpathAnalyzer.setLiteral1(literal);
				
			}else if(type1.equals("Boolean")){
				String fktName = ((FunctionCallExpr) rhs1).getFunctionName();
				boolean booleanValue = fktName.equals("true()");
				xpathAnalyzer.setBoolean1(booleanValue);
				
			}else{ //if(type1.equals("PathExpression")){
				String expression = ((Expr) rhs1).getText();
				xpathAnalyzer.setVarLoc1(expression);
			}
			
			//Füttern von xpathAnalyzer für bestimmten type2
			if (type2.equals("Number")){
				Number num = ((NumberExpr) rhs2).getNumber();
				float numValue = num.floatValue();
				xpathAnalyzer.setNum2(numValue);
				
			}else if(type2.equals("String")){
				String literal = ((LiteralExpr) rhs2).getLiteral();
				xpathAnalyzer.setLiteral2(literal);
				
			}else if(type2.equals("Boolean")){
				String fktName = ((FunctionCallExpr) rhs2).getFunctionName();
				boolean booleanValue = fktName.equals("true()");
				xpathAnalyzer.setBoolean2(booleanValue);
				
			}else{ //if(type2.equals("PathExpression")){
				String expression = ((Expr) rhs2).getText();
				xpathAnalyzer.setVarLoc2(expression);
			}
			
			//Aufruf von xpathAnalyzer
			if (bpelTest){
				return xpathAnalyzer.isConcurrent(lhs1.getText(), lhs2.getText(), op1, op2, type1, type2);
			} else {
				return xpathAnalyzer.simpleIsConcurrent(lhs1.getText(), lhs2.getText(), op1, op2, type1, type2);
			}
		}
	}
	
	/**
	 * Gibt den Typ eines XPath-Ausdrucks zurück.
	 * @param expr XPath-Ausdruck
	 * @return Number, falls der Ausdruck ein Objekt vom Typ number ist
	 * 		   String, falls der Ausdruck ein Objekt vom Typ literal ist
	 * 		   Boolean, falls der Ausdruck ein Funktionsaufruf ist
	 * 		   andernfalls PathExpression
	 */
	private String getExpressionType(Expr expr){
		String type;
		if (expr instanceof NumberExpr){
			type = "Number";
		}else if (expr instanceof LiteralExpr){
			type = "String";
		}else if (expr instanceof FunctionCallExpr){
			String fktName = ((FunctionCallExpr) expr).getFunctionName();
			if (fktName.equals("true()") || fktName.equals("false()")){
				type = "Boolean";
			}else{
				type = "Unexpected Type";
				System.err.println("Die Funktion "+fktName+" ist nicht zugelassen. Es sind nur die Funktionen true() oder false() zugelassen.");
				System.exit(0);
			}
		}else{ //if (expr instanceof Expr) {
			type = "PathExpression";
		}
		return type;
	}
	
	/**
	 * @param bpelFileName
	 */
	public void setBPELFileName(String bpelFileName) {
		this.bpelFileName = bpelFileName;
	}

	/**
	 * Argumente für den Aufruf:
"($X <= 5.0 and $Y > 7.0) or $Z = 8.9"
"$X = 5.0 and $Y = 8.0"

"--help" oder "/?"

"loanApproval.bpel"
	 */
	public static void main(String[] args) {
		if (args.length > 2){
			System.err.println("Zu viele Argumente (entweder zwei Argumente oder eines).");
			System.exit(0);
		}else if (args.length == 2){			
			try{
				Application control = new Application();
				control.analyzeXPathExpressions(args[0], args[1]);
			}catch (IOException e) {
				System.err.println(e);
			}catch (JDOMException e) {
				System.err.println(e);
			}catch (SAXException e) {
				System.err.println(e);
			}
		}else if (args.length == 1){
			if (args[0].equals("/?") || args[0].equals("--help")){
				try{
					Application control = new Application();
					control.help();
				}catch (IOException e) {
					System.err.println(e);
				}catch (JDOMException e) {
					System.err.println(e);
				}catch (SAXException e) {
					System.err.println(e);
				}
			}else{
				try{
					Application control = new Application();
					control.setBPELFileName(args[0]);
					control.analyzeBPELProcess();
				}catch (IOException e) {
					System.err.println(e);
				}catch (JDOMException e) {
					System.err.println(e);
				}catch (SAXException e) {
					System.err.println(e);
				}
			}
		}else{
			System.err.println("Zu wenig Argumente.");
			System.exit(0);
		}
	}
}