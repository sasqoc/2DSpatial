package pl.sasqoc.game;

/**
 *
 * @author amadela
 */
public class Statistics {
    
    public static String functionName = "Funkcja -";
    public static int  checksCount;
    public static int  bruteChecks, spatialIndexChecks;        
    
    public static void reset(){
        functionName = "Funkcja -";
        checksCount = 0;
        bruteChecks = 0;
        spatialIndexChecks = 0;
    }
}