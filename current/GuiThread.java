package oracle;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * NOTES HERE
 * @author Ashton
 */
public class GuiThread extends Thread
{
    //  BEGIN STATIC VARIABLES

    //  END STATIC VARIABLES

    //  BEGIN INSTANCE VARIABLES
    
    //  END INSTANCE VARIABLES

    //  BEGIN METHODS

    public GuiThread()
    {
    }
    
    @Override
    public void run()
    {
        int total_messages = 0;
        while(true)
        {
            // TODO : put a GUI here
            if(Main.GuiQHasNext())
            {
                Date d = new Date();
                Calendar c = Calendar.getInstance();
                int hr = c.get(Calendar.HOUR_OF_DAY)+1;
                int mn = c.get(Calendar.MINUTE);
                
                boolean mktOpen = false;
                if(hr < 16)
                {
                    if(hr > 9 || (hr == 9 && mn >= 30))
                    {
                        mktOpen = true;
                    }
                }
                
                System.out.println("GUIQ : " + (mktOpen ? "Y":"N") + " " + total_messages + " " + d.getTime());
                System.out.println(Main.getGuiQMsg());
                total_messages ++;
            }
            else
            {
                try {
                    sleep(500);
                } catch (InterruptedException ex) {
                    Logger.getLogger(GuiThread.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    //  END METHODS

}   //END OF GUIThread
