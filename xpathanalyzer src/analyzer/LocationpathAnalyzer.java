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

import java.io.File;


public class LocationpathAnalyzer {
	
	/**
	 * Gibt zurück, ob die beiden Lokalisierungspfade identisch sind
	 * @param locationpath1
	 * @param locationpath2
	 * @return true, falls beide Lokalisierungspfade identisch sind, sonst false
	 */
	public boolean expressionsEqual(String locationpath1, String locationpath2){
		return locationpath1.equals(locationpath2);
	}
	
	/**
	 * Gibt das Ergebnis des Schnitttests der beiden Lokalisierungspfade zurück. Der Schnitttest läuft momentan noch ohne Schema ab, 
	 * da der Schnitttest mit XML-Schema zur Zeit der Implementierung noch nicht vorlag.
	 * @param locationpath1 
	 * @param locationpath2
	 * @param xsdFile eine xsd-Datei auf die sich die beiden Lokalisierungspfade beziehen
	 * @return true, falls sich die beiden Lokalisierungspfade schneiden, andernfalls false
	 */
	public boolean existsIntersection(String locationpath1, String locationpath2, String xsdFile){
		/*
		 * mu-calculus satisfiability solver for XML
		 * entwickelt von Pierre Geneves
		 * stand zur Zeit der Abgabe noch nicht vollständig zur Verfügung. Daher konnte nur der Schnitttest ohne 
		 * XML-Schema oder DTD, nicht aber der Schnitttest mit XML-Schema eingebunden werden.
		 */
		boolean result = false;
		try{
			String path = System.getenv("MuSatSolver");
			
			String[] cmdarray = {"cmd.exe","/C",path+"XPathOverlap.bat", "\""+locationpath1+"\"", "\""+locationpath2+"\""};
			
			File dir = new File(path);
			
			Process p = Runtime.getRuntime().exec(cmdarray, null, dir);
			
			//any error message?
            StreamObserver errorObserver = new StreamObserver(p.getErrorStream(), "ERROR");            
            
            //any output?
            StreamObserver outputObserver = new StreamObserver(p.getInputStream(), "OUTPUT");
                
            //kick them off
            errorObserver.start();
            outputObserver.start();
                 
            //any error???
            int exitVal = p.waitFor();
            if (exitVal != 0){
            	System.err.println("Im MuSatSolver ist ein Fehler aufgetreten.");
            	System.err.println("ExitValue: " + exitVal); 
            }   
            
            //Abfrage erst nach waitFor
            result = outputObserver.getResult();
        } catch (Throwable t){
            t.printStackTrace();
        }
		return result;
	}
}
