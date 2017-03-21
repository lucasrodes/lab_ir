/*
 *   This file is part of the computer assignment for the
 *   Information Retrieval course at KTH.
 *
 *   First version:  Hedvig Kjellström, 2012
 */

package ir;

import java.util.*;
import java.lang.*;
import java.io.*;

public class Query {

    public LinkedList<String> terms = new LinkedList<String>();
    public Map<String, Double> weights = new HashMap<String, Double>();
    public Map<String, Double> bigramWeights = new HashMap<String, Double>();

    public double query_size = 0;
    public int nDocs = 0;

    /**
     *  Creates a new empty Query
     */
    public Query() {
    }

    /**
     *  Creates a new Query from a string of words
     */
    public Query( String queryString, Indexer indexer  ) {
        double termFrequency_query_term, documentFrequency_query_term, w_query_term;
    	String query_term, bigram, last_term = null;
        StringTokenizer tok = new StringTokenizer( queryString );
        Map<String, Integer> idfmap = indexer.index.idfMap;
        this.nDocs = indexer.index.getNumberDocs();

    	while ( tok.hasMoreTokens() ) {
            // Obtain query term
            query_term = tok.nextToken();

    	    this.terms.add( query_term );

            // Add the query term to the map with its corresponding tf-idf value
            if (this.weights.containsKey(query_term)){
                this.weights.put( query_term, this.weights.get( query_term ) + 1 );
            }
            else
                this.weights.put( query_term, new Double(1) );

            // Add bigram weights
            if (last_term != null){
                bigram = last_term + " " +query_term;
                System.err.println("- Adding: ''" + bigram +"''");
                // Add the query term to the map with its corresponding tf-idf value
                if (this.bigramWeights.containsKey(bigram)){
                    this.bigramWeights.put( bigram, this.bigramWeights.get( bigram ) + 1 );
                    System.err.println("   - Already contained, updating tf");
                }
                else{
                    this.bigramWeights.put( bigram, new Double(1) );
                    System.err.println("   - New bigram added");
                }
                System.err.println("  ... Correctly added!");
            }

            last_term = query_term;
    	}

        // Normalize weights of terms
        double idf;
        for(Map.Entry<String, Double> entry : weights.entrySet()){
            String key = entry.getKey();
            idf = 0;
            if(idfmap.containsKey(key))
                idf = Math.log(nDocs/new Double(idfmap.get(key)));

            this.weights.put(key, idf*entry.getValue() / this.size());
        }

        System.err.println("- Normalizing the bigram weights!");
        // Normalize weights of bigrams
        for(Map.Entry<String, Double> entry : bigramWeights.entrySet()){
            String key = entry.getKey();
            System.err.println("   - Bigram ''" + key + "''");
            idf = 0;
            if(idfmap.containsKey(key))
                idf = Math.log(nDocs/new Double(idfmap.get(key)));
            System.err.println("      - idf computed");
            this.bigramWeights.put(key, idf*entry.getValue()/ (this.size()-1));
            System.err.println("   ... Bigram correctly normalized!");
        }
        System.err.println("  ... Correctly normalized!");

        /*for(Map.Entry<String, Double> entry : bigramWeights.entrySet()){
            String key = entry.getKey();
            System.err.println("Bigram '" + key +"' with weight " + entry.getValue());
        }*/


        this.query_size = new Double(this.size());
    }

    /**
     *  Returns the number of terms
     */
    public int size() {
	   return terms.size();
    }

    /**
     *  Returns a shallow copy of the Query
     */
    public Query copy() {
    	Query queryCopy = new Query();
    	queryCopy.terms = (LinkedList<String>) terms.clone();
    	queryCopy.weights = new HashMap<String, Double>(weights);
    	return queryCopy;
    }

    /**
     *  Expands the Query using Relevance Feedback
     */
    public void relevanceFeedback( PostingsList results, boolean[] docIsRelevant, Indexer indexer ) {
	// results contain the ranked list from the current search
	// docIsRelevant contains the users feedback on which of the 10 first hits are relevant

	//
	//  YOUR CODE HERE
	//
        double alpha = 1;
        double beta = 0.75;
        double gamma = 0;
        double dr = 0;
        //System.err.println("Relevant documents: ");
        Map<String, Double> centroid_weights = new HashMap<String, Double>();

        // Obtain centroid
        for (int i = 0; i<10; i++){
            // Update weights according to relevant documents
            if (docIsRelevant[i]){
                dr++;
                //System.err.println("* " + indexer.index.docIDs.get( "" + results.get(i).docID ));
                // Build vector of this document
                // Obtain tf values of terms in i:th document
                Map<String, Integer> tfmap = indexer.index.tfMap.get(results.get(i).docID);
                // Obtain length of i:th document
                Double len = new Double(indexer.index.getDocLength(results.get(i).docID));
                for(Map.Entry<String, Integer> entry : tfmap.entrySet()){
                    // Get term in i:th document and obtain
                    String key = entry.getKey();
                    // Obtain normalized tf value * idf
                    double idf = Math.log(this.nDocs/new Double(indexer.index.idfMap.get(key)));
                    Double value = idf*(new Double(entry.getValue()))/len;
                    if (centroid_weights.containsKey(key))
                        centroid_weights.put(key, centroid_weights.get(key)+value);
                    else
                        centroid_weights.put(key, value);
                }
            }
        }

        // Update weights
        String key;
        double val;
        for(Map.Entry<String, Double> entry : centroid_weights.entrySet()){
            key = entry.getKey();
            val = entry.getValue();
            if (this.weights.containsKey(key))
                this.weights.put(key, alpha*this.weights.get(key)+beta/dr*val);
            else{
                this.terms.add(key); //COMENT THIS TO BOOST THE ALGORITHM
                this.weights.put(key, beta/dr*val);
            }
            //System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
        }
    }

}


