package stream;

import java.io.File;
import java.io.IOException;
import java.sql.SQLOutput;

public class Persistence {
    public static void persist(String message, long idConv){

        try{
            File persistenceFile = new File("PersistenceData/PersistenceData_"+idConv+".");
            if(persistenceFile.createNewFile()){
                System.out.println("New persistence file created for group chat "+idConv+".");
            }else{
                System.out.println("Persistence file for group chat "+idConv+" was located.");
            }
        }catch(IOException e){
            System.out.println("An error as occured.");
            e.printStackTrace();
        }

    }
}
