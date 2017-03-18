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

    public double query_size = 0;
    /**
     *  Creates a new empty Query
     */
    public Query() {
    }

    /**
     *  Creates a new Query from a string of words
     */
    public Query( String queryString  ) {
        double termFrequency_query_term, documentFrequency_query_term, w_query_term;
    	String query_term;
        StringTokenizer tok = new StringTokenizer( queryString );

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

    	}

        for(Map.Entry<String, Double> entry : weights.entrySet()){
            this.weights.put(entry.getKey(), entry.getValue() / this.size());
        }

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
                Map<String, Integer> map = indexer.index.tfMap.get(results.get(i).docID);
                Double len = new Double(indexer.index.docLengths.get( "" + results.get(i).docID));
                for(Map.Entry<String, Integer> entry : map.entrySet()){
                    String key = entry.getKey();
                    Double value = (new Double(entry.getValue()))/len;
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


