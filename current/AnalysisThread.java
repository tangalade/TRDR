package oracle;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * NOTES HERE
 * @author Ashton
 */
public class AnalysisThread extends Thread
{
    //  BEGIN STATIC VARIABLES

    //  END STATIC VARIABLES

    //  BEGIN INSTANCE VARIABLES

    //  END INSTANCE VARIABLES

    //  BEGIN METHODS

    @Override
    public void run()
    {
        while(true)
        {
            //TODO : put some analysis here
            if(Main.AnalysisQHasNext())
            {
                Main.getAnalysisQMsg();
            }
            else
            {
                try {
                    sleep(500);
                } catch (InterruptedException ex) {
                    Logger.getLogger(AnalysisThread.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    //  END METHODS

}   //END OF AnalysisThread
