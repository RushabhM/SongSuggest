package ml.homework.knn.data;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SongsMapping {
	
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

    public static String getSongnameBySongID(Integer songID) {
        
        String id = songID.toString();
        try {
            FileInputStream fstream = new FileInputStream("song_mapping.txt");
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String str;
            while ((str = br.readLine()) != null) {
               if(patternMatch(str, id))
            	   return str.substring(6);
            }
        } catch (Exception e) {// Catch exception if any
            System.err.println("Error: " + e);
        }
       return null;
    }
    
    public static HashMap getSongsByArtist(String artist)
    {
    	int i = 0, j=0; String temp = null;
    	HashMap<Integer,Integer> songs = new HashMap<Integer, Integer>();
    	
    	try {
            FileInputStream fstream = new FileInputStream("song_mapping.txt");
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String str;
            while ((str = br.readLine()) != null) {
            j = 0;
               if(patternMatch(str, artist))
            	   { 
            	   i = 0;
            	   temp = str.substring(j, j+1);
            	   	while(!temp.equals("\t"))
            	   		{
            	   			i = i*10 + Integer.parseInt(temp);
            	   			j++;
            	   			if(str.charAt(j)=='\t') break;
            	   			temp = str.substring(j, j+1);
            	   		}
            	   	songs.put(i,1);
            	   	}
            }
        } catch (Exception e) {// Catch exception if any
            System.err.println("Error: " + e);
        }
        
    	return songs;
    }
    
    public static boolean patternMatch(String str, String pattern)
    {
       	Pattern p = Pattern.compile(pattern);
    	Matcher matcher = p.matcher(str);
    	if (matcher.find()) {
    	    return true;
    	} else {
    	    return false;
    	}
    }
    
    public static void main(String[] args)
    {
    	getSongsByArtist("Tiesto");
    }
}
