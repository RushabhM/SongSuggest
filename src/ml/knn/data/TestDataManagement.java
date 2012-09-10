package ml.knn.data;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class TestDataManagement {
	
    private static final String USER_SONG_SEPARATOR  		= "-";
    private static final String TOKENIZER        			= " ";
    private static final String SONG_FREQ_SEPARATOR        	= ":";
    private static int          trainingDataSize 			= 0;

    public String getTestDataByUserID(int userID) {
        String test = null;
        int iterator = 1;
        try {
            FileInputStream fstream = new FileInputStream("user_test.txt");
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String str;
            while ((str = br.readLine()) != null) {
            	if(iterator==userID)
            		return str;
            	iterator++;
            }
        } catch (Exception e) {// Catch exception if any
            System.err.println("Error: " + e);
        }
        return test;
    }
}
