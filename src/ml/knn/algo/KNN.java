package ml.knn.algo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ml.homework.knn.data.SongsMapping;
import ml.homework.knn.data.TestDataManagement;
import ml.homework.knn.data.TrainingData;

public class KNN {
	
    public int     neighborCount;
    public boolean isWeighted;
    public int     simlarityMetric;
    public int     userQuery;
    public String  artistQuery;
    public TrainingData train;
    
    private ArrayList<Integer> K_neighbors;
    private ArrayList<Double> K_distance;
    public 	ArrayList<Double> sortedDist;
    public 	ArrayList<Integer> sortedNeighbor;
    
    public static double sum_precision =  0;
    public static int training_set_size = 0;
    static double temp, sum=0.0;

    public void call_KNN (int neighborCount, boolean isWeighted, int simlarityMetric, int userQuery, String artistQuery) {
        this.neighborCount = neighborCount;
        this.isWeighted = isWeighted;
        this.simlarityMetric = simlarityMetric;
        this.userQuery = userQuery;
        this.artistQuery = artistQuery;
        this.K_distance = new ArrayList<Double>();
        this.K_neighbors = new ArrayList<Integer>();
        this.train = new TrainingData();
        this.sortedDist = new ArrayList<Double>();
    	this.sortedNeighbor = new ArrayList<Integer>();
    	
    	train.load();
    	//training_set_size = train.userTrain.size;
    	if(userQuery!=-1)
    		user_processing();
    	else if(!artistQuery.equals(null))
    		artist_processing();
    }
    
    public static void main(String[] args) {
        
    	KNN knn = new KNN();
    	
    	for(int i=1; i<100; i++)
    		knn.call_KNN(10, true , 2 , 3041 , "Metallica");
    	
    	/*
    	int[] user = new int[]{100,200,300,400,500};
    	KNN knn = new KNN();
    	for(int i = 0 ; i < 5; i++)
    		knn.call_KNN(10, false , 2 , user[i] , null);
    	*/
    	
    	
    	/* Effect of K
    	int[] array_K =  new int[] {1,3,5,10,25,50,100,250,500,1000};
    	KNN knn = new KNN();
    	for(int i=0; i< array_K.length; i++)
    	{
    		knn.call_KNN(array_K[i], false , 0 , 2050 , null);
    	}
    	*/
    	
    	//knn.call_KNN(300, true , 2 , 2050 , "Howlin' Wolf"); 0.54
    	//for(int i=1100; i< 1500; i++)
    	//!!knn.call_KNN(50, false , 1 , i , "Howlin' Wolf"); 1100-1500
    	
//		Effect of Metrics    	
//    	KNN knn = new KNN();
//    	for(int i=5; i<= 3323 ; i+=10)
//        	knn.call_KNN(500, false , 1 , i , "Howlin' Wolf");
//    	
//    	System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
//    	System.out.println(sum);
//	
    	/*
    	if (args.length < 5) {
            System.out.println("Invalid Input!! Program accepts input of the form: KNN <num_neighbors> <weighted=true/false> <Similarity Metric(0/1/2)> <User Query(-1:default)> <Artist Query(-1:default)>");
            return;
        }

        try {
            int neighborsToConsider = Integer.parseInt(args[0]);
            boolean isWeighted = Boolean.parseBoolean(args[1]);
            int metricsOption = Integer.parseInt(args[2]);
            int userId = Integer.parseInt(args[3]);
            String artistQuery = args[4];
            new KNN(neighborsToConsider, isWeighted, metricsOption, userId, artistQuery);
        } catch (Exception e) {
            System.out.println("Error in Parsing input!");
        }
        */
    }

    public void user_processing()
    {
    	HashMap rv_map = null; 
    	ArrayList<Integer> rankVector, popularVector, randomVector;
    	ArrayList<Double> simMeasure;
    	
    	computeDistances(userQuery);
    	if(!isWeighted)
    		{
    			rv_map = unweightedRankVector(sortedNeighbor);
    		}
    	else
    		{
    			simMeasure = calculateSimilarity(sortedNeighbor, this.userQuery, this.simlarityMetric);
    			rv_map = weightedRankVector(sortedNeighbor, simMeasure);
    		}
    	rankVector = generateSortedRankVector(rv_map,10,false);
    	    	
    	temp = getPrecisionAt10(rankVector, userQuery);
    	System.out.println("KNN based precision - " + temp);
        
        
        popularVector = getPopularSongs(10);
        System.out.println("Popularity precision - " + getPrecisionAt10(popularVector, userQuery));
        
        randomVector = getRandomSongs(10);
        System.out.println("Random precision - " + getPrecisionAt10(randomVector, userQuery));
        
        printTopRecommendedSongs(rankVector,10);
        
        printTopFrequentSongs(train.getTop10FrequentSongs(userQuery), 10);
    }
    
    public void artist_processing()
    {
    	ArrayList<Integer> rankVector;
    	ArrayList<Double> simMeasure;
    	int artistAsUser = -5;
    	
    	HashMap mv = SongsMapping.getSongsByArtist(artistQuery);
    	Iterator itr = mv.entrySet().iterator();
    	int i = 0;
    	
    	System.out.println("========================================================================");
    	System.out.println("Songs by the artist\n");
    	
        while (itr.hasNext()&&(i<10)) {
            Map.Entry me = (Map.Entry) itr.next();
            System.out.println(SongsMapping.getSongnameBySongID((Integer)me.getKey()));
            i++;
        }
        
        artistAsUser = train.userTrain.putMapForArtist(-1, mv);
        //System.out.println(artistAsUser);
        
        computeDistances(artistAsUser);
    	if(!isWeighted)
    			mv = unweightedRankVector(sortedNeighbor);
    	else
    		{
    			simMeasure = calculateSimilarity(sortedNeighbor, artistAsUser, this.simlarityMetric);
    			mv = weightedRankVector(sortedNeighbor, simMeasure);
    		}
    	rankVector = generateSortedRankVector(mv,10, true);
        printTopRecommendedSongs(rankVector,10);
    }
    

    
    private HashMap unweightedRankVector(ArrayList<Integer> neighbors) 
    {
    	int size = neighborCount, key; double value;
    	HashMap<Integer,Double> rv = new HashMap<Integer,Double>();
    	HashMap neighbourMV[] = new HashMap[neighbors.size()];
    	
    	for(int i=0; i<size; i++)
    	{
    		neighbourMV[i] = train.userTrain.getMapByUser(neighbors.get(i));
    	}
    	
    	for(int j=0; j<size; j++)
    	{
    		Iterator itr = neighbourMV[j].entrySet().iterator();
    		
    		while (itr.hasNext()) 
    		{
                Map.Entry me = (Map.Entry) itr.next();
                key = (Integer) me.getKey();
                value = (Integer)me.getValue();
                if(!rv.containsKey(key))
                		rv.put(key,((double)value)/size);
                else
                		rv.put(key, rv.get(key) + ((double)value)/size);
            }
    	}

    	return rv;
    }
    
    private void computeDistances(int userID) {
        
    	int minIndex = 0; double minD = Double.MAX_VALUE;
    	double distance = 0.0;
    	int no_users = train.getTrainingDataSize();
    	
    	for (int i = 1 ; i <= no_users ; i++)
    	{
//    		if(userID!=-1)
//    			distance = train.userTrain.InverseSquared(userID,i);
//    		else
    			distance = train.userTrain.InverseSquared(userID, i);
    		//System.out.println(distance + " from user : " + i);
    		if(i==userID) continue;
    		
    		K_neighbors.add(i);
			K_distance.add(distance);
    		
    	}
    	
    	//System.out.println("All distances : "  + K_distance);
    	
    	while(sortedNeighbor.size() <= neighborCount)
    	{
    		minIndex = -1;
    		minD = Double.MAX_VALUE;
    		for(int x=0; x<K_neighbors.size(); x++)
    		{
    			if(minD > K_distance.get(x))
    			{
    				minD = K_distance.get(x);
    				minIndex= x;
    			}
    		}
    		K_distance.set(minIndex, Double.MAX_VALUE);
    		sortedDist.add(minD);
    		sortedNeighbor.add(K_neighbors.get(minIndex));
    	}
    	
    	//System.out.println("Sorted Distance : " + sortedDist);
    	//System.out.println("Sorted Neighbors : " + sortedNeighbor);
    }
    
    private ArrayList<Double> calculateSimilarity(ArrayList<Integer> neighbors, int userID, int choice)
    {
    	ArrayList<Double> similarityMatrix = new ArrayList<Double>();
    	
    	for(int i=0; i<neighborCount; i++)
    	{
    		switch(choice)
    		{
    		case 0: similarityMatrix.add(1/(train.userTrain.InverseSquared(userID, neighbors.get(i)))); break;
    		case 1: similarityMatrix.add((double)(train.userTrain.dotProduct(userID, neighbors.get(i)))); break;
    		case 2: similarityMatrix.add((train.userTrain.cosine(userID, neighbors.get(i)))); break;
    		}
    	}
    	
    	return similarityMatrix;
    }
    
    private HashMap weightedRankVector(ArrayList<Integer> neighbors, ArrayList<Double> similarityvalue) 
    {
    	int size = neighborCount, key; double value, sumWt=0.0;
    	HashMap<Integer,Double> rv = new HashMap<Integer,Double>();
    	HashMap neighbourMV[] = new HashMap[neighbors.size()];
    	
    	for(int k=0; k<size; k++)
    	{
    		sumWt += similarityvalue.get(k);
    	}
    	
    	for(int i=0; i<size; i++)
    	{
    		neighbourMV[i] = train.userTrain.getMapByUser(neighbors.get(i));
    	}
    	
    	for(int j=0; j<size; j++)
    	{
    		Iterator itr = neighbourMV[j].entrySet().iterator();
    		
    		while (itr.hasNext()) 
    		{
                Map.Entry me = (Map.Entry) itr.next();
                key = (Integer) me.getKey();
                value = (Integer)me.getValue();
                if(!rv.containsKey(key))
                	rv.put(key,similarityvalue.get(j)*((double)value)/sumWt);
                else
                	rv.put(key, rv.get(key) + similarityvalue.get(j)*((double)value)/sumWt);
            }
    	}
    	
    	return rv;
    }

    public ArrayList<Integer> generateSortedRankVector(HashMap rv, int limit, boolean ifArtist)
    {
    	ArrayList<Integer> sortedRv = new ArrayList<Integer>();
    	ArrayList<Double> sortedDis = new ArrayList<Double>();
    	int size = rv.size(), maxID = -1; double maxWt = Double.MIN_VALUE;
    	
    	HashMap user = train.userTrain.getMapByUser(userQuery);
    	if(ifArtist)
    		user = train.userTrain.getMapByUser(train.userTrain.size-1);
    	
    	while(sortedRv.size()!=limit)
    	{
    		maxWt = Double.MIN_VALUE;
    		maxID = -1;
    		Iterator itr = rv.entrySet().iterator();
    		
    		while (itr.hasNext()) 
    		{
    			Map.Entry me = (Map.Entry) itr.next();
    			if(maxWt < (Double)me.getValue())
    			{
    				maxWt = (Double)me.getValue();
    				maxID = (Integer)me.getKey();
    			}
    		}
    		
    		rv.put(maxID, Double.MIN_VALUE);
    		if(!user.containsKey(maxID))
			{sortedRv.add(maxID);		sortedDis.add(maxWt); }
    		
    	}
    	//System.out.println("Sorted Rv - "+sortedRv);
    	//System.out.println("Sorted Dist of RV - "+sortedDis);
    	
    	return sortedRv;
    }
    
    public boolean patternMatch(String str, String pattern)
    {
       	Pattern p = Pattern.compile(pattern);
    	Matcher matcher = p.matcher(str);
    	if (matcher.find()) {
    	    //System.out.println(matcher.group(0)); //prints /{item}/
    	    return true;
    	} else {
    	    //System.out.println("Match not found");
    	    return false;
    	}
    }
    
    public double getPrecisionAt10(ArrayList<Integer> recommendedList, int userID)
    {
    	int hits = 0;
    	String testString = null;
    	TestDataManagement tdm = new TestDataManagement();
    	
    	for(int i = 0 ; i < 10; i++)
    	{
    		testString = tdm.getTestDataByUserID(userID);
    		if(patternMatch(testString, recommendedList.get(i).toString())) hits++;
    	}
    	return ((double)hits)/10;
    }
    
    public void printTopRecommendedSongs(ArrayList<Integer> recommendedList, int limit)
    {
    	System.out.println("========================================================================");
    	System.out.println("Top " + limit + " songs recommended by the system\n");
    	SongsMapping songs = new SongsMapping();
    	for(int i = 0 ; i < limit; i++)
    	{
    		System.out.println(songs.getSongnameBySongID(recommendedList.get(i)));    		
    	}
    }
    
    public void printTopFrequentSongs(ArrayList<Integer> songList, int limit)
    {
    	System.out.println("========================================================================");
    	System.out.println("\nTop " + limit + " songs freuqntly played by the user\n");
    	SongsMapping songs = new SongsMapping();
    	for(int i = 0 ; i < limit; i++)
    	{
    		System.out.println(songs.getSongnameBySongID(songList.get(i)));    		
    	}
    }
    
    public ArrayList<Integer> getPopularSongs(int topN) 
    {
    	int size = neighborCount, key; double value;
    	ArrayList<Integer> neighbors = new ArrayList<Integer>();
    	for(int x=1; x<=train.userTrain.size ; x++)
    	{
    		neighbors.add(x);
    	}
    	
    	HashMap<Integer,Double> rv = new HashMap<Integer,Double>();
    	HashMap neighbourMV[] = new HashMap[neighbors.size()];
    	
    	for(int i=0; i<size; i++)
    	{
    		neighbourMV[i] = train.userTrain.getMapByUser(neighbors.get(i));
    	}
    	
    	for(int j=0; j<size; j++)
    	{
    		Iterator itr = neighbourMV[j].entrySet().iterator();
    		
    		while (itr.hasNext()) 
    		{
                Map.Entry me = (Map.Entry) itr.next();
                key = (Integer) me.getKey();
                value = (Integer)me.getValue();
                if(!rv.containsKey(key))
                	rv.put(key,(value));
                else
                	rv.put(key, rv.get(key) + value);
            }
    	}
    	
    	ArrayList<Integer> sortedRv = new ArrayList<Integer>();
    	ArrayList<Integer> sortedFreq = new ArrayList<Integer>();
    	int maxID = -1; double maxFreq = Integer.MIN_VALUE;
    	
    	while(sortedRv.size()!=topN)
    	{
    		maxFreq = Integer.MIN_VALUE;
    		maxID = -1;
    		Iterator itr = rv.entrySet().iterator();
		
    		while (itr.hasNext()) 
    		{
    			Map.Entry me = (Map.Entry) itr.next();
    			if(maxFreq < (Double)me.getValue())
    			{
    				maxFreq = (Double)me.getValue();
    				maxID = (Integer)me.getKey();
    			}
    		}
    		
    		rv.put(maxID, Double.MIN_VALUE);
			sortedRv.add(maxID);
			sortedFreq.add((int)maxFreq);
    	}
    	return sortedRv;
    }

    public ArrayList<Integer> getRandomSongs(int n)
    {
    	ArrayList<Integer> random = new ArrayList<Integer>();
    	for(int i = 0 ; i < n ; i++)
    	{
    		random.add((int)(Math.random()*train.userTrain.size));
    	}
    	return random;
    }
}

class ValueComparator implements Comparator<Double> {

    Map<Integer, Double> base;
    public ValueComparator(Map<Integer, Double> base) {
        this.base = base;
    }

	@Override
	public int compare(Double o1, Double o2) {
		// TODO Auto-generated method stub
		return base.get(o1).compareTo(base.get(o2));
	}

	
}
