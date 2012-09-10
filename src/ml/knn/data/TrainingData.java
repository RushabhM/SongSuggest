package ml.knn.data;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

public class TrainingData {
	
    private static final String USER_SONG_SEPARATOR  		= "-";
    private static final String TOKENIZER        			= " ";
    private static final String SONG_FREQ_SEPARATOR        	= ":";
    private static int          trainingDataSize 			= 0;

    public static SparseMatrix userTrain;

    private static void parseLine(int userIndex, String str) {
        StringTokenizer st = new StringTokenizer(str, TOKENIZER);
        while (st.hasMoreTokens()) {
            String songPlayed = st.nextToken();
            int separatorIndex = songPlayed.indexOf(SONG_FREQ_SEPARATOR);
            int songId = Integer.parseInt(songPlayed.substring(0, separatorIndex).trim());
            int played = Integer.parseInt(songPlayed.substring(separatorIndex + 1).trim());
            userTrain.putValue(userIndex, songId, played);
        }
    }

    public static void load() {
        trainingDataSize = computeTrainingDataSize();
        // initializing array size 1 extra because in our training data is 1-index based not 0
        userTrain = new SparseMatrix(trainingDataSize + 1);
        try {
            FileInputStream fstream = new FileInputStream("user_train.txt");
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String str;
            while ((str = br.readLine()) != null) {
                int separatorIndex = str.indexOf(USER_SONG_SEPARATOR);
                int userId = Integer.parseInt(str.substring(0, separatorIndex).trim());
                String tokenList = str.substring(separatorIndex + 1).trim();
                parseLine(userId, tokenList);
            }
            in.close();
        } catch (Exception e) {// Catch exception if any
            System.err.println("Error: " + e);
        }
        
    }

    private static int computeTrainingDataSize() {
        int count = 0;
        try {
            FileInputStream fstream = new FileInputStream("user_train.txt");
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String str;
            while ((str = br.readLine()) != null) {
                count++;
            }
        } catch (Exception e) {// Catch exception if any
            System.err.println("Error: " + e);
        }
        return count;
    }
    
    public static ArrayList<Integer> getTop10FrequentSongs(int userID)
    {
    	ArrayList<Integer> topFreq = new ArrayList<Integer>();
    	HashMap<Integer,Integer> musicVector = userTrain.getMapByUser(userID);
    	int maxFreq = Integer.MIN_VALUE, songID = -1;
    	
    	while(topFreq.size()<10)
    	{
    		maxFreq = Integer.MIN_VALUE;
    		songID = -1;
    		Iterator itr = musicVector.entrySet().iterator();
    		while (itr.hasNext()) 
    		{
    			Map.Entry me = (Map.Entry) itr.next();
    			if(maxFreq < (Integer) me.getValue())
    			{
    				maxFreq = (Integer) me.getValue();
    				songID = (Integer) me.getKey();
    			}
        	}
    		musicVector.put(songID,0);
            topFreq.add(songID);
    	}
    	System.out.println(topFreq);
    	return topFreq;
    }
    
    public static SparseMatrix getUserTrain() {
        return userTrain;
    }
    
    public static int getTrainingDataSize() {
        return trainingDataSize;
    }
    
    
    public static void main(String[] args) {
        load();
    	getTop10FrequentSongs(190);
    }
}
