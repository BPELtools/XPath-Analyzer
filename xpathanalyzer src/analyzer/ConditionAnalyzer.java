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


public class ConditionAnalyzer {
	
	/**
	 * Überprüft, ob sich die Operatoren op1 und op2 für Operanden vom Typ boolean gegenseitig
	 * ausschließen.
	 * @param op1 Operator der ersten Bedingung
	 * @param bool1 Operand der ersten Bedingung
	 * @param op2 Operator der zweiten Bedingung
	 * @param bool2 Operand der zweiten Bedingung
	 * @return false, falls sich die beiden Bedingungen gegenseitig ausschließen, sonst true
	 */
	public boolean typeBoolean(String op1, boolean bool1, String op2, boolean bool2){
		if (bool1 == bool2){
			if (op1.equals("=") && op2.equals("!=")){
				return false;
			}else if(op1.equals("!=") && op2.equals("=")){
				return false;
			}else{
				return true;
			}
		}else{ //if (bool1 != bool2) {
			if (op1.equals(op2)){
				return false;
			}else{
				return true;
			}	
		}
	}
	
	/**
	 * Überprüft, ob sich die Operatoren op1 und op2 für Operanden vom Typ String gegenseitig
	 * ausschließen.
	 * @param op1 Operator der ersten Bedingung
	 * @param literal1 Operand der ersten Bedingung
	 * @param op2 Operator der zweiten Bedingung
	 * @param literal2 Operand der zweiten Bedingung
	 * @return false, falls sich die beiden Bedingungen gegenseitig ausschließen, sonst true
	 */
	public boolean typeString(String op1, String literal1, String op2, String literal2){
		if (literal1.equals(literal2)){
			if (op1.equals(op2)){
				return true;
			}else{
				return false;
			}
		}else{ //if (!literal1.equals(literal2)){
			if (op1.equals("=") && op2.equals("=")){
				return false;
			}else{
				return true;
			}
		}
	}
	
	/**
	 * Überprüft, ob sich die Operatoren op1 und op2 für Operanden vom Typ Number gegenseitig
	 * ausschließen.
	 * @param op1 Operator der ersten Bedingung
	 * @param num1 Operand der ersten Bedingung
	 * @param op2 Operator der zweiten Bedingung
	 * @param num2 Operand der zweiten Bedingung
	 * @return false, falls sich die beiden Bedingungen gegenseitig ausschließen, sonst true
	 */
	public boolean typeNumber(String op1, float num1, String op2, float num2){
		if (num1 == num2){
			//(=, !=), (<, >=), (<=, >), (<, >), (<, =), (>, =)
			if (op1.equals("=") && op2.equals("!=")){
				return false;
			}else if (op1.equals("<") && op2.equals(">=")){
				return false;
			}else if (op1.equals("<=") && op2.equals(">")){
				return false;
			}else if (op1.equals("<") && op2.equals(">")){
				return false;
			}else if (op1.equals("<") && op2.equals("=")){
				return false;
			}else if (op1.equals("=") && op2.equals(">")){
				return false;
			}else if (op2.equals("=") && op1.equals("!=")){
				return false;
			}else if (op2.equals("<") && op1.equals(">=")){
				return false;
			}else if (op2.equals("<=") && op1.equals(">")){
				return false;
			}else if (op2.equals("<") && op1.equals(">")){
				return false;
			}else if (op2.equals("<") && op1.equals("=")){
				return false;
			}else if (op2.equals("=") && op1.equals(">")){
				return false;
			}else{
				return true;
			}
		}else if (num1 < num2){
			//(=, =), (<, >), (<=, >=), (<=, >), (<, >=), (<, =), (<=, =)
			if (op1.equals("=") && op2.equals("=")){
				return false;
			}else if (op1.equals("<") && op2.equals(">")){
				return false;
			}else if (op1.equals("<=") && op2.equals(">=")){
				return false;
			}else if (op1.equals("<=") && op2.equals(">")){
				return false;
			}else if (op1.equals("<") && op2.equals(">=")){
				return false;
			}else if (op1.equals("<") && op2.equals("=")){
				return false;
			}else if (op1.equals("<=") && op2.equals("=")){
				return false;
			}else{
				return true;
			}
		}else{ //if (num1 > num2){
			//(=, =), (>, <), (>=, <=), (>=, <), (>, <=), (>, =), (>=, =)
			if (op1.equals("=") && op2.equals("=")){
				return false;
			}else if (op1.equals(">") && op2.equals("<")){
				return false;
			}else if (op1.equals(">=") && op2.equals("<=")){
				return false;
			}else if (op1.equals(">=") && op2.equals("<")){
				return false;
			}else if (op1.equals(">") && op2.equals("<=")){
				return false;
			}else if (op1.equals(">") && op2.equals("=")){
				return false;
			}else if (op1.equals(">=") && op2.equals("=")){
				return false;
			}else{
				return true;
			}
		}	
	}
	
	/**
	 * Überprüft, ob sich die Operatoren op1 und op2 für dieselben Operanden (Variablen) gegenseitig
	 * ausschließen.
	 * @param op1 Operator der ersten Bedingung
	 * @param expr1 Operand der ersten Bedingung
	 * @param op2 Operator der zweiten Bedingung
	 * @param expr2 Operand der zweiten Bedingung
	 * @return false, falls sich die beiden Bedingungen gegenseitig ausschließen, sonst true
	 */
	public boolean typeVarLoc(String op1, String expr1, String op2, String expr2){
		if (expr1.equals(expr2)){
			if (op1.equals("=") && op2.equals("!=")){
				return false;
			}else if (op1.equals("<") && op2.equals(">=")){
				return false;
			}else if (op1.equals("<=") && op2.equals(">")){
				return false;
			}else if (op1.equals("<") && op2.equals(">")){
				return false;
			}else if (op1.equals("<") && op2.equals("=")){
				return false;
			}else if (op1.equals("=") && op2.equals(">")){
				return false;
			}else if (op2.equals("=") && op1.equals("!=")){
				return false;
			}else if (op2.equals("<") && op1.equals(">=")){
				return false;
			}else if (op2.equals("<=") && op1.equals(">")){
				return false;
			}else if (op2.equals("<") && op1.equals(">")){
				return false;
			}else if (op2.equals("<") && op1.equals("=")){
				return false;
			}else if (op2.equals("=") && op1.equals(">")){
				return false;
			}else{
				return true;
			}
		}else{ //if (!expr1.equals(expr2)){
			if (op1.equals("=") && op2.equals("=")){
				return false;
			}else{
				return true;
			}
		}
	}
}
