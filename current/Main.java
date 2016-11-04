package oracle;

import java.util.*;
import java.io.*;
import java.net.MalformedURLException;

/**
 * NOTES AND RANDOM to-do'S HERE
 * @author Ashton
 * TODO : 1. script lang for indicators & figure out class structs
 */
public class Main {
    
    private static Map<String, TdaStockRecord> recordMap;
    private static boolean recordLock;
    
    private static Queue<TdaStockRecord> GuiQ, AnalysisQ;
    private static boolean GuiQLock, AnalysisQLock;
    
    public static void submitUpdate(TdaStockRecord rec)
    {
        while(AnalysisQLock || GuiQLock);
        AnalysisQLock = true;
        GuiQLock = true;
        
        GuiQ.add(rec);
        AnalysisQ.add(rec);
        
        AnalysisQLock = false;
        GuiQLock = false;
        
        while(recordLock);
        recordLock = true;
        TdaStockRecord trec = recordMap.get(rec.symbol);
        if(trec != null)    trec.updateTo(rec);
        else    trec = rec;
        recordMap.put(rec.symbol, trec);
        recordLock = false;
    }
    
    public static TdaStockRecord getRecord(String symbol)
    {
        while(recordLock);
        recordLock = true;
        TdaStockRecord ret = recordMap.get(symbol);
        recordLock = false;
        return ret;
    }
    
    public static boolean GuiQHasNext()
    {
        while(GuiQLock);
        return !(GuiQ.isEmpty());
    }
    
    public static boolean AnalysisQHasNext()
    {
        while(AnalysisQLock);
        return !(AnalysisQ.isEmpty());
    }
    
    public static TdaStockRecord getGuiQMsg()
    {
        while(GuiQLock);
        return GuiQ.poll();
    }
    
    public static TdaStockRecord getAnalysisQMsg()
    {
        while(AnalysisQLock);
        return AnalysisQ.poll();
    }
    
    /**
     * @param args the command line arguments
     */
    
    public static void main(String[] args) throws MalformedURLException, IOException {
        initialize();
        
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                
        TdaApiSession sesh = new TdaApiSession();
        String username = "", password = "", sourceID = "";
        while(sesh.logIn(username, password, sourceID) == false)
        {
            if(username.length() > 0)   System.out.println("Last login failed");
            System.out.println("username : ");
            username = br.readLine();
            System.out.println("password : ");
            password = br.readLine();
            System.out.println("sourceID : ");
            sourceID = br.readLine();
        }
        System.out.println("Successful Log In!\n");
        
        System.out.println("Retrieving Streamer Info...");
        sesh.streamerInfo();
        System.out.println("Got Streamer Info!\n");
        
        Thread sThread = new StreamThread(sesh);
        sThread.start();
        
        Thread gThread = new GuiThread();
        gThread.start();

        Thread aThread = new AnalysisThread();
        aThread.start();
    }

    /*
     * Sets up the Q's and RecordMap
     */
    private static void initialize() {
        GuiQ = new LinkedList<>();
        AnalysisQ = new LinkedList<>();
        recordMap = new TreeMap<>();
        GuiQLock = false;
        AnalysisQLock = false;
        recordLock = false;
    }

}
