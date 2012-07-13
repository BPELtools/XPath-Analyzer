/**
 * @author Bastian Schurr
 * 
 * Institut f�r Architektur von Anwendungssystemen
 * Universit�t Stuttgart
 * Universit�tsstra�e 38
 * D�70569 Stuttgart
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
     * Der StreamObserver erh�lt als Eingabe einen zu �berwachenden InputStream und den Typ des Streams
     * @param is InputStream
     * @param type Typ des InputStreams (ERROR oder OUTPUT)
     */
    public StreamObserver(InputStream is, String type){
        this.is = is;
        this.type = type;
        result = false;
    }
    
    /**
     * Gibt das Ergebnis des Schnitttest zur�ck.
     * @return true, falls im InputStream OUTPUT "overlaps" vorkommt, sonst false
     */
    public boolean getResult(){
    	return result;
    }
    
    /**
     * Der Thread, der den jeweilgen Stream �berwacht.
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
