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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class StreamObserver extends Thread{
    InputStream is;
    String type;
    boolean result;
    
    /**
     * Der StreamObserver erhält als Eingabe einen zu überwachenden InputStream und den Typ des Streams
     * @param is InputStream
     * @param type Typ des InputStreams (ERROR oder OUTPUT)
     */
    public StreamObserver(InputStream is, String type){
        this.is = is;
        this.type = type;
        result = false;
    }
    
    /**
     * Gibt das Ergebnis des Schnitttest zurück.
     * @return true, falls im InputStream OUTPUT "overlaps" vorkommt, sonst false
     */
    public boolean getResult(){
    	return result;
    }
    
    /**
     * Der Thread, der den jeweilgen Stream überwacht.
     */
    public void run(){
        try{
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            
            if (type.equals("ERROR")){
                while ((line = br.readLine()) != null){
                	System.out.println(type + ">" + line); 
                } 
            } else {
                while ((line = br.readLine()) != null){
    				if (line.contains("overlaps")){
    					result = true;
    				}
                	System.out.println(type + ">" + line); 
                } 
            }
        } catch (IOException e){
            e.printStackTrace();  
        }
    }
}
