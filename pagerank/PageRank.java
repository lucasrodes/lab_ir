/*  
 *   This file is part of the computer assignment for the
 *   Information Retrieval course at KTH.
 * 
 *   First version:  Johan Boye, 2012
 */  

import java.util.*;
import java.io.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class PageRank{

    /**  
     *   Maximal number of documents. We're assuming here that we
     *   don't have more docs than we can keep in main memory.
     */
    final static int MAX_NUMBER_OF_DOCS = 2000000;

    /**
     *   Mapping from document names to document numbers.
     */
    Hashtable<String,Integer> docNumber = new Hashtable<String,Integer>();

    /* This is what they should have given us! */
    Hashtable<String,Integer> titleToNumber = new Hashtable<String,Integer>();
    /**
     *   Mapping from document numbers to document names
     */
    String[] docName = new String[MAX_NUMBER_OF_DOCS];

    /**  
     *   A memory-efficient representation of the transition matrix.
     *   The outlinks are represented as a Hashtable, whose keys are 
     *   the numbers of the documents linked from.<p>
     *
     *   The value corresponding to key i is a Hashtable whose keys are 
     *   all the numbers of documents j that i links to.<p>
     *
     *   If there are no outlinks from i, then the value corresponding 
     *   key i is null.
     */
    Hashtable<Integer,Hashtable<Integer,Boolean>> link = new Hashtable<Integer,Hashtable<Integer,Boolean>>();

    /**
     * Page rank scores of each document
     */ 
    double[] pageRank = new double[MAX_NUMBER_OF_DOCS];

    /**
     *   The number of outlinks from each node.
     */
    int[] out = new int[MAX_NUMBER_OF_DOCS];

    /**
     *   The number of documents with no outlinks.
     */
    int numberOfSinks = 0;

    /**
     *   The probability that the surfer will be bored, stop
     *   following links, and take a random jump somewhere.
     */
    final static double BORED = 0.15;

    /**
     *   Convergence criterion: Transition probabilities do not 
     *   change more that EPSILON from one iteration to another.
     */
    final static double EPSILON = 0.00001;

    /**
     *   Never do more than this number of iterations regardless
     *   of whether the transistion probabilities converge or not.
     */
    final static int MAX_NUMBER_OF_ITERATIONS = 1000;

    
    /* --------------------------------------------- */


    public PageRank( String filename, int method ) {
    	int noOfDocs = readDocs( filename );
        readTitles();
        //System.out.println("real: "+ noOfDocs);
        //System.out.println("est: "+link.size());
    	computePagerank( method, noOfDocs );
        printBestResults(30);
    }


    /* --------------------------------------------- */


    /**
     *   Reads the documents and creates the docs table. When this method 
     *   finishes executing then the @code{out} vector of outlinks is 
     *   initialised for each doc, and the @code{p} matrix is filled with
     *   zeroes (that indicate direct links) and NO_LINK (if there is no
     *   direct link. <p>
     *
     *   @return the number of documents read.
     */
    int readDocs( String filename ) {
    	int fileIndex = 0;
    	try {
    	    System.err.print( "Reading file... " );
    	    BufferedReader in = new BufferedReader( new FileReader( filename ));
    	    String line;
    	    while ((line = in.readLine()) != null && fileIndex<MAX_NUMBER_OF_DOCS ) 
            {
        		int index = line.indexOf( ";" );
        		String title = line.substring( 0, index );
        		Integer fromdoc = docNumber.get( title );
        		//  Have we seen this document before?
        		if ( fromdoc == null ) {	
        		    // This is a previously unseen doc, so add it to the table.
        		    fromdoc = fileIndex++;
        		    docNumber.put( title, fromdoc );
        		    docName[fromdoc] = title;
        		}
        		// Check all outlinks.
        		StringTokenizer tok = new StringTokenizer( line.substring(index+1), "," );
        		while ( tok.hasMoreTokens() && fileIndex<MAX_NUMBER_OF_DOCS ) 
                {
        		    String otherTitle = tok.nextToken();
        		    Integer otherDoc = docNumber.get( otherTitle );
        		    if ( otherDoc == null ) {
        			// This is a previousy unseen doc, so add it to the table.
        			otherDoc = fileIndex++;
        			docNumber.put( otherTitle, otherDoc );
        			docName[otherDoc] = otherTitle;
        		    }
        		    // Set the probability to 0 for now, to indicate that there is
        		    // a link from fromdoc to otherDoc.
                    if ( link.get(fromdoc) == null ) {
        	           //System.out.println("origin: "+fromdoc);
                       link.put(fromdoc, new Hashtable<Integer,Boolean>());
        		    }
        		    if ( link.get(fromdoc).get(otherDoc) == null ) {
                        //System.out.println("destination" +otherDoc);
                        link.get(fromdoc).put( otherDoc, true );
                        out[fromdoc]++;
        		    }
        		}
    	    }
    	    if ( fileIndex >= MAX_NUMBER_OF_DOCS ) {
                System.err.print( "stopped reading since documents table is full. " );
    	    }
    	    else {
                System.err.print( "done. " );
    	    }
    	    // Compute the number of sinks.
    	    for ( int i=0; i<fileIndex; i++ ) {
        		if ( out[i] == 0 )
        		    numberOfSinks++;
        	}
    	}

    	catch ( FileNotFoundException e ) {
    	    System.err.println( "File " + filename + " not found!" );
    	}
    	catch ( IOException e ) {
    	    System.err.println( "Error reading file " + filename );
    	}
    	System.err.println( "Read " + fileIndex + " number of documents" );
    	return fileIndex;
    }


    void readTitles(  ) {
        String filename = "articleTitles.txt";
        int fileIndex = 0;
        try {
            System.err.print( "Reading titles... " );
            BufferedReader in = new BufferedReader( new FileReader( filename ));
            String line;
            while ((line = in.readLine()) != null && fileIndex<MAX_NUMBER_OF_DOCS ) 
            {
                String[] string = line.split(";");
                Integer number = Integer.parseInt(string[0]);//line.indexOf( ";" );
                String title = string[1];
                if (!title.equals("Z-World.html") && !title.equals("Z-World.txt"))
                    title += ".f";//line.substring( 0, index);
                titleToNumber.put( title, number );
            }
        }
        catch(Exception e){}
    }

    /* --------------------------------------------- */


    /*
     *   Computes the pagerank of each document.
     */
    void computePagerank( int method, int numberOfDocs) {
        double[] p = new double[numberOfDocs];
        switch (method){
            case 0: powerIteration(numberOfDocs);
                        break;
            case 1: monteCarlo1(numberOfDocs, 1000);
                        break;
            case 2: monteCarlo2(numberOfDocs, 1000);
                        break;
            case 3: monteCarlo3(numberOfDocs, 1000);    
                        break;
            case 4: monteCarlo4(numberOfDocs, 10);
                        break;
            case 5: monteCarlo5(numberOfDocs, 1000);
                        break;
        }

    }

    double[] powerIteration(int numberOfDocs){
    //
    //   YOUR CODE HERE
    //
        long startTime = System.currentTimeMillis();
        
        // Initialize pageRank with uniform probability
        for (int i = 0; i < numberOfDocs; i++){
            pageRank[i] = 1.0/numberOfDocs; // randomly set
        }

        /*Random rand = new Random();
        int  n = rand.nextInt(numberOfDocs) + 1;
        pageRank[n] = 1.0; */

        // POWER ITERATION ALGORITHM
        int iterations = 0;
        double incrementProb = EPSILON+1;

        while (iterations < MAX_NUMBER_OF_ITERATIONS && 
            incrementProb > EPSILON){
            double[] newPageRank = new double[numberOfDocs];  

            // Summation of all the ranks
            double sum = 0;
            for (int i = 0; i < numberOfDocs; i++){
                //sum += pageRank[i];
                if (link.get(i) == null)
                    sum += pageRank[i];
            }

            // Base value for newPageRank vector
            for (int i = 0; i < numberOfDocs; i++){
                // Matrix J
                newPageRank[i] = BORED/(double)numberOfDocs;  
                // Matrix P (when state has no links to other states, we
                // allow the surfer to go to any state with uniform probability)
                newPageRank[i] += (1.0-BORED)*((double)sum/(double)numberOfDocs);
                // Do not allow for self connection ?
                /*if (link.get(i) == null) {
                    newPageRank[i] -= (1.0 - BORED) * (double)pageRank[i]
                    /((double)numberOfDocs);
                }*/
            }

            // Account for the actual transition states, i.e. the matrix P
            for (Integer i : link.keySet()){
                for (Integer j : link.get(i).keySet()){
                    newPageRank[j] += (1.0 - BORED)*(pageRank[i]/(double)out[i]);
                }
            }

            // Find maximum increment in transition probability
            incrementProb = 0.0;
            for (int i = 0; i < numberOfDocs; i++){
                incrementProb = Math.max(Math.abs(pageRank[i]-
                    newPageRank[i]), incrementProb);    
            }

            // Increment number of iterations
            iterations++;
            
            // Update new page rank vector
            pageRank = newPageRank;

            //System.err.println(iterations);
            //System.err.println(incrementProb);
        }
       
        long estimatedTime = System.currentTimeMillis() - startTime;
        // System.out.println("elapsed time: "+ estimatedTime/1000.0+"s");
        return pageRank;
    }


    public void normalize() {
            double sum = 0;
            for (int i = 0; i < pageRank.length; i++) {
                sum += this.pageRank[i];
            }
            for (int i = 0; i < pageRank.length; i++) {
                this.pageRank[i] /= sum;
            }
    }


    void monteCarlo1(int numberOfDocs, int numberOfWalks){
        Random rand = new Random(System.currentTimeMillis());
        int id;

        for (int n = 0; n < numberOfWalks; n++){
            
            // Run random walk n
            id = rand.nextInt(numberOfDocs);
            while(rand.nextDouble() > BORED){
                if (link.get(id) == null)
                    id = rand.nextInt(numberOfDocs);
                else{
                    Integer[] outlinks = link.get(id).keySet().toArray(new Integer[0]);
                    id = outlinks[rand.nextInt(out[id])];
                }
            }
            pageRank[id]++;
        }

        for (int n = 0; n < numberOfDocs; n++){
            pageRank[n] /= numberOfWalks;
        }

        normalize();
    }


    void monteCarlo2(int numberOfDocs, int iterations){
        Random rand = new Random(System.currentTimeMillis());
        int id;

        for (int n = 0; n < iterations; n++){
            for (int d = 0; d < numberOfDocs; d++){
                id = d;
                while(rand.nextDouble() > BORED){
                    if (link.get(id) == null)
                        id = rand.nextInt(numberOfDocs);
                    else{
                        Integer[] outlinks = link.get(id).keySet().toArray(new Integer[0]);
                        id = outlinks[rand.nextInt(out[id])];
                    }
                }
                pageRank[id]++;
            }
        }

        for (int n = 0; n < numberOfDocs; n++){
            pageRank[n] /= (iterations*numberOfDocs);
        }

        normalize();
    }


    void monteCarlo3(int numberOfDocs, int iterations){
        Random rand = new Random(System.currentTimeMillis());
        int id;

        for (int n = 0; n < iterations; n++){
            for (int d = 0; d < numberOfDocs; d++){
                id = d;
                while(rand.nextDouble() > BORED){
                    pageRank[id]++;
                    if (link.get(id) == null)
                        id = rand.nextInt(numberOfDocs);
                    else{
                        Integer[] outlinks = link.get(id).keySet().toArray(new Integer[0]);
                        id = outlinks[rand.nextInt(out[id])];
                    }
                }
            }
        }

        for (int n = 0; n < numberOfDocs; n++){
            pageRank[n] /= (iterations*numberOfDocs);
        }

        normalize();
    }


    void monteCarlo4(int numberOfDocs, int iterations){
        Random rand = new Random(System.currentTimeMillis());
        int id;

        for (int n = 0; n < iterations; n++){
            for (int d = 0; d < numberOfDocs; d++){
                id = d;
                while(rand.nextDouble() > BORED){
                    pageRank[id]++;
                    if (link.get(id) == null)
                        break;
                    else{
                        Integer[] outlinks = link.get(id).keySet().toArray(new Integer[0]);
                        id = outlinks[rand.nextInt(out[id])];
                    }
                }
            }
        }

        for (int n = 0; n < numberOfDocs; n++){
            pageRank[n] /= (iterations*numberOfDocs);
        }

        normalize();
    }


    void monteCarlo5(int numberOfDocs, int numberOfWalks){
        Random rand = new Random(System.currentTimeMillis());
        int id;

        for (int n = 0; n < numberOfWalks; n++){
            
            // Run random walk n
            id = rand.nextInt(numberOfDocs);
            while(rand.nextDouble() > BORED){
                pageRank[id]++;
                if (link.get(id) == null)
                    break;
                else{
                    Integer[] outlinks = link.get(id).keySet().toArray(new Integer[0]);
                    id = outlinks[rand.nextInt(out[id])];
                }
            }
        }

        for (int n = 0; n < numberOfDocs; n++){
            pageRank[n] /= numberOfWalks;
        }

        normalize();
    }


    /*boolean[] computeError(){

    }*/

    void printBestResults(int numberOfResults){
        //Arrays.sort(pageRank);
        double[] pRank = new double[pageRank.length];

        System.arraycopy( pageRank, 0, pRank, 0, pageRank.length );

        double s = 0;
        for (int j = 0; j < pRank.length; j++){
            s += pRank[j];
        }

        for (int i = 0; i < numberOfResults; i++){
            double max = 0.0;
            int id_max = 0;
            for (int j = 0; j < pRank.length; j++){
                if (pRank[j] > max){
                    max = pRank[j];
                    id_max = j;
                }
            }
            pRank[id_max] = -1;
            System.out.println(docName[id_max]+": "+max);
        }



        System.out.println("sum = " + s);
    }

    /* --------------------------------------------- */

    public void save(){
        this.saveJSON("../ir/files/pageRank.json", this.pageRank);
        this.saveJSON("../ir/files/docNumber.json", this.docNumber);
        this.saveJSON("../ir/files/titleToNumber.json", this.titleToNumber);
    }

    /** [NEW]
     * Saves object "o" as a JSON file called fileName 
     */
    public void saveJSON(String fileName, Object o){
        // System.err.println("accessing disk");
        Gson gson = new Gson();
        try(FileWriter writer = new FileWriter(fileName)){
            gson.toJson(o, writer);
            //System.err.println("\n Token stored");
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* --------------------------------------------- */
   
    public static void main( String[] args ) {
	if ( args.length != 1 ) {
	    System.err.println( "Please give the name of the link file" );
	}
	else {
	    PageRank p1 = new PageRank( args[0], 0 );
        String file = "Davis.f";
        System.err.println(file + " -- " + p1.titleToNumber.get(file));
        int name = p1.titleToNumber.get(file);
        System.err.println(p1.pageRank[p1.docNumber.get(""+name)]);
        //PageRank p2 = new PageRank( args[0], 1 );
        p1.save();
	}
    }
}
