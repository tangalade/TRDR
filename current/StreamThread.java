package oracle;

import java.io.*;
import java.net.MalformedURLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * NOTES HERE
 * @author Ashton
 */
public class StreamThread extends Thread
{
    //  BEGIN STATIC VARIABLES

    //  END STATIC VARIABLES

    //  BEGIN INSTANCE VARIABLES
    
    private String request, toRequest;
    private boolean toRequestLock;

    private boolean heartbeatLock;
    private long lastHeartbeat;
    
    private DataInputStream myStream;
    
    private TdaApiSession mySesh;
    //  END INSTANCE VARIABLES

    //  BEGIN METHODS

    StreamThread(TdaApiSession sesh) // add in streaming parameters later
    {
        mySesh = sesh;
        myStream = null;
        
        request = "";
        
        toRequestLock = true;   toRequest = request;    toRequestLock = false;
    }
    
    public long getLastHeartbeat()
    {
        while(heartbeatLock);
        heartbeatLock = true;
        long ret = lastHeartbeat;
        heartbeatLock = false;
        return ret;
    }

    @Override
    public void run()
    {
        try {
            toRequestLock = true;
            request = mySesh.streamerRequestSubscribe();
            myStream = mySesh.openStream(request);
            toRequest = request;
            toRequestLock = false;
            
            //remember to do a keepalive 30 minutes
            
        } catch (MalformedURLException ex) {
            Logger.getLogger(StreamThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(StreamThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        while(true)
        {
            // Streaming Loop
            try {
                procStream();
            } catch (IOException ex) {
                Logger.getLogger(StreamThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void procStream() throws IOException {
        byte t = myStream.readByte();
        switch(t)
        {
            case 72: //'H'
                t = myStream.readByte();
                if((char)(t) == 'T')
                {
                    System.out.print("Heartbeat ");
                    
                    heartbeatLock = true;
                    lastHeartbeat = myStream.readLong();
                    heartbeatLock = false;
                    
                    Date d = new Date();
                    d.setTime(lastHeartbeat);
                    System.out.println(d);
                    System.out.println(lastHeartbeat);
                }
                else
                {
                    System.out.println("This seems random 1 : " + t);
                }
            break;
            case 78: //'N'
                TdaStreamerRecord rec = new TdaStreamerRecord();
                rec.streamerServerID = myStream.readUTF();
                rec.messageLength = myStream.readInt();
                rec.response = myStream.readShort();

                for(int i=0; i<3; i++)
                {
                    byte fieldNo = myStream.readByte();
                    switch(fieldNo)
                    {
                        case 0:
                            rec.serviceID = myStream.readShort();
                        break;
                        case 1:
                            rec.returnCode = myStream.readShort();
                        break;
                        case 2:
                            rec.description = myStream.readUTF();
                        break;
                        default:
                            System.out.println("This seems random 2 : " + fieldNo);
                        break;
                    }
                }
                t = myStream.readByte();
                if(t == -1)
                {
                    t = myStream.readByte();
                    if(t == 0x0A)
                    {
                        System.out.println("Begin Streamer Record");
                        System.out.println(rec);
                        System.out.println("End Streamer Record");
                    }
                    else
                        System.out.println("This seems random 2.5 : " + t);
                }
                else
                {
                    System.out.println("This seems random 2.6 : " + t);
                }
                System.out.println();

            break;
            case 83: //'S'
                TdaStockRecord rec0 = new TdaStockRecord();
                System.out.println("Begin Streaming Record");
                System.out.println("Message Length " + myStream.readShort());
                System.out.println("Level " + myStream.readShort() + " quote");
                
                byte fieldID = myStream.readByte();
                while(-1 != fieldID)
                {
                    rec0.setField(fieldID, myStream);
                    fieldID = myStream.readByte();
                }
                
                System.out.print(rec0);
                
                Main.submitUpdate(rec0);
                
                t = myStream.readByte();
                if(0x0A == t)
                    System.out.println("Normal End of Streaming Record\n");
                else
                    System.out.println("This seems random 4 : " + t);
            break;
            case -1: //delimiter
                t = myStream.readByte();
                if(0x0A == t)
                    System.out.println("random delimiter read\n");
                else
                    System.out.println("this seems random 5 : " + t);
            break;
            default:
                System.out.println("This seems random 6 : " + t);
            break;
        }
    }
    
    //  END METHODS

}   //END OF StreamThread
