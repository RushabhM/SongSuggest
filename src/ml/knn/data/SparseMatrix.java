package ml.knn.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SparseMatrix {

    public final int size;
    private HashMap[] trainingMatrix;
    

    public SparseMatrix(int num) {
        size = num;								
        trainingMatrix = new HashMap[size];
    }

    public void putValue(int userID, int songID, int freq) {
        if (freq != 0) {
            if (trainingMatrix[userID] == null) {
                trainingMatrix[userID] = new HashMap();
                //System.out.println("Created map for : " + userID);
            }
            trainingMatrix[userID].put(songID, freq);
        }
    }

    public int putMapForArtist(int ID, HashMap artist)
    {
    	trainingMatrix[size-1] = artist;
    	return size-1;
    }
    
    public int getValue(int userID, int songID) {
        int val = 0;
        if (trainingMatrix[userID].containsKey(songID)) {
            val = (Integer) trainingMatrix[userID].get(songID);
        }
        return val;
    }

    public int dotProduct(int i, int j) {
        int dotPdt = 0;
        
        HashMap H2 = trainingMatrix[j];
        HashMap H1 = trainingMatrix[j];

        Iterator itr = H2.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry me = (Map.Entry) itr.next();
            int key = (Integer) me.getKey();
            //System.out.println(key);
            if (H1.containsKey(key)) {
                dotPdt += ((Integer) H2.get(key)) * ((Integer) H1.get(key));
            }

        }
        return dotPdt;
    }
    
    public int opt_dotProduct(int user1, int user2)
    {
    	int key1, key2, dotProduct = 0;
    	
    	Iterator itr1 = trainingMatrix[user1].entrySet().iterator();
    	Iterator itr2 = trainingMatrix[user2].entrySet().iterator();
    	
    	Map.Entry m1 = (Map.Entry) itr1.next();
		Map.Entry m2 = (Map.Entry) itr2.next();
    	
    	while(itr1.hasNext()&&itr2.hasNext())
    	{
    		key1 = (Integer) m1.getKey(); //System.out.println(key1);
    		key2 = (Integer) m2.getKey(); //System.out.println(key2);
    		
    		if(key1<key2) 
    			{
    				m1 = (Map.Entry) itr1.next();
    				//System.out.println(key1 + " < " + key2);
    			}
			else if(key1>key2) 
			{
				m2 = (Map.Entry) itr2.next();
				//System.out.println(key1 + " > " + key2);
			}
    		
			else if(key1==key2)
    		{
    			dotProduct += ((Integer) trainingMatrix[user1].get(key1)) * ((Integer) trainingMatrix[user2].get(key2));
    			//System.out.println(key1 + " = " + key2);
    			m1 = (Map.Entry) itr1.next();
    			m2 = (Map.Entry) itr2.next();
    		}
    	}
    	return 0;
    }
    
    public double cosine(int a,int b)
    {
    	return (dotProduct(a,b)/(mod(a,a)*mod(b,b)));
    }

    public double mod(int a, int b) {
        return Math.sqrt(dotProduct(a, b));
    }

    //TODO - May be not required- Eligible for removal
    public HashMap euclideanDistance(int i, int j) {
        HashMap distanceVector = new HashMap();
        Iterator itr = trainingMatrix[i].entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry me = (Map.Entry) itr.next();
            distanceVector.put((Integer) me.getKey(), (Integer) me.getValue());
        }
        itr = trainingMatrix[j].entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry me = (Map.Entry) itr.next();
            int key = (Integer) me.getKey();
            int prevVal = 0;
            if (distanceVector.get(key) != null) {
                prevVal = (Integer) distanceVector.get(key);
            }
            distanceVector.put(key, prevVal - (Integer) me.getValue());
        }
        return distanceVector;
    }

    //TODO - May be not required- Eligible for removal
    public double modVector(HashMap vector) {
        int mod = 0;
        Iterator itr = vector.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry me = (Map.Entry) itr.next();
            int value = (Integer) me.getValue();
            if (value != 0) {
                mod += (value * value);
            }
        }
        return Math.sqrt(mod);
    }
    
    
    
    
    public double InverseSquared(int i, int j) {
        HashMap distanceVector = new HashMap();
        double mod = 0;
        Iterator itr = trainingMatrix[i].entrySet().iterator();

        while (itr.hasNext()) {
            Map.Entry me = (Map.Entry) itr.next();
            distanceVector.put((Integer) me.getKey(), (Integer) me.getValue());
        }
        itr = trainingMatrix[j].entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry me = (Map.Entry) itr.next();
            int key = (Integer) me.getKey();
            int prevVal = 0;
            if (distanceVector.get(key) != null) {
                prevVal = (Integer) distanceVector.get(key);
            }
            distanceVector.put(key, prevVal - (Integer) me.getValue());
        }
        itr = distanceVector.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry me = (Map.Entry) itr.next();
            int value = (Integer) me.getValue();
            if (value != 0) {
                mod += (value * value);
            }
        }
        return mod;
    }

    public HashMap[] getMatrix() {
        return trainingMatrix;
    }
    
    public HashMap getMapByUser(int userID) {
    	if(userID == -1)
    		return trainingMatrix[size-1];
    	else
    		return trainingMatrix[userID];
    }

    // TODO Test Code- Remove before submission
    public static void main(String[] args) {
        SparseMatrix playlists = new SparseMatrix(5);
        playlists.putValue(0, 1, 5);
        playlists.putValue(0, 2, 4);
        playlists.putValue(0, 5, 4);
        playlists.putValue(0, 7, 9);
        playlists.putValue(0, 11, 5);
        playlists.putValue(1, 2, 3);
        playlists.putValue(1, 7, 4);
        playlists.putValue(1, 11, 10);
        playlists.putValue(1, 13, 5);
        System.out.println("DotP : " + playlists.dotProduct(0, 0));
        System.out.println("Mod : "  + playlists.mod(0, 1));
        System.out.println("Inverse Squared : "  + playlists.InverseSquared(0, 1));
        System.out.println("Euclidean Distance : " + playlists.modVector(playlists.euclideanDistance(0, 1)));
        System.out.println("Mod Vector : " + playlists.modVector(playlists.trainingMatrix[0]));
    }

}
