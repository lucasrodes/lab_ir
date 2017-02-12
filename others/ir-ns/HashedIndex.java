/*  
 *   This file is part of the computer assignment for the
 *   Information Retrieval course at KTH.
 * 
 *   First version:  Johan Boye, 2010
 *   Second version: Johan Boye, 2012
 *   Additions: Hedvig Kjellström, 2012-14
 */  


package ir;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Comparator;
import java.util.Collections;

/**
 *   Implements an inverted index as a Hashtable from words to PostingsLists.
 */
public class HashedIndex implements Index {

    /** The index as a hashtable. */
    private HashMap<String,PostingsList> index = new HashMap<String,PostingsList>();


    /** [MODIFIED]
     *  Inserts new information provided by new occurence of token 
     *  in docID at offset into the hashmap.
     */
    public void insert( String token, int docID, int offset ) {
        /* Check if Map already contains the term token and if it already
           appeared in docID */
        if(this.index.containsKey(token))
            this.index.get(token).insert(docID, offset);

        /* Dictionary does not contain the term token, so we add a new
           postingslist with docID and the corresponding positional index 
           */
        else{
            PostingsList postingslist = new PostingsList();
            postingslist.insert(docID, offset);
            this.index.put(token, postingslist);
        }
    }


    /** [MODIFIED]
     *  Returns all the words in the index.
     */
    public Iterator<String> getDictionary() {
        return this.index.keySet().iterator();
    }


    /** [MODIFIED]
     *  Returns the postings for a specific term, or null
     *  if the term is not in the index.
     */
    public PostingsList getPostings( String token ) {
       return this.index.get(token);
    }


    /**
     *  Searches the index for postings matching the query.
     */
    public PostingsList search( Query query, int queryType, int rankingType, 
        int structureType ) {
        if (query.size()>0){
            switch (queryType){
                case Index.INTERSECTION_QUERY: return intersect(query);
                case Index.PHRASE_QUERY: return phrase_query(query);
                case Index.RANKED_QUERY:
                    System.out.println("ranked query - not yet implemented");
                    return null;
                default: 
                    System.out.println("not valid query");
                    return null;
            }
        }
        else
            // Query was empty
            return null;
    }


    /** [NEW] 
     * Finds documents containing the query as a phrase 
     * TODO: Skip pointer
     */
    public PostingsList phrase_query(Query query){
        
        // List with postings corresponding to the queries
        LinkedList<PostingsList> listQueriedPostings = new LinkedList<PostingsList>();
        for (int i = 0; i<query.size(); i++){
            // If any query has zero matches, return 0 results
            if (!this.index.containsKey(query.terms.get(i)))
                return null;
            // Otherwise store postings in the list
            listQueriedPostings.add(this.getPostings(query.terms.get(i)));
        }

        PostingsList result = listQueriedPostings.get(0);

        // In case only one word is queried
        if (listQueriedPostings.size() == 1){
            return result;
        }

        // Apply algorithm as many times as words in the query
        for(int i = 1; i < listQueriedPostings.size(); i++){
            result = phrase_query(result, listQueriedPostings.get(i));
            if (result.isEmpty()){
                return null;
            }
        }
        return result;
    }


    /** [NEW] 
     *  Finds documents containing a two-words phrase 
     */
    public PostingsList phrase_query(PostingsList l1, PostingsList l2){
        
        PostingsList phrase = new PostingsList();
        
        // Counters to iterate docIDs
        int count1 = 0;
        int count2 = 0;
        // Counters to iterate positional indices
        int subcount1 = 0;
        int subcount2 = 0;

        // First posting
        PostingsEntry p1 = l1.get(0);
        PostingsEntry p2 = l2.get(0);

        // List of positional indices (changes at each iteration)
        LinkedList<Integer> ll1;
        LinkedList<Integer> ll2;

        // Used to store positional index
        int pp1;
        int pp2;

        while(true){
            // docID match (1/2) //
            if (p1.docID == p2.docID){
                // Obtain list of positional indices
                ll1 = p1.positions;
                ll2 = p2.positions;
                // First positional indices
                pp1 = ll1.get(0);
                pp2 = ll2.get(0);
                // Initialize counter
                subcount1 = 0;
                subcount2 = 0;

                // Search if the phrase exists
                while(true){
                    // Match, consecutive words (2/2) - EUREKA! //
                    if (pp1+1 == pp2){
                        // Save found match (docID and positional index of last
                        // word)
                        phrase.insert(p1.docID, pp2);
                        // Increase counters and pos indices if list is not finished
                        subcount1++;
                        subcount2++;
                        if (subcount1<ll1.size() && subcount2<ll2.size()){
                            pp1 = ll1.get(subcount1);
                            pp2 = ll2.get(subcount2);
                        }
                        // If list finished, break
                        else
                            break;
                    }
                    // Not match
                    else if (pp1+1 < pp2){
                        subcount1++;
                        if (subcount1<ll1.size())
                            pp1 = ll1.get(subcount1);
                        // If list finished, break
                        else
                            break;
                    }
                    // Not match
                    else{
                        subcount2++;
                        if (subcount2<ll2.size())
                            pp2 = ll2.get(subcount2);
                        else
                            break;
                    }
                }

                // Once we finished looking at the list of positional indices of one
                // posting, increase counters and go to next postings (if there are)
                count1++;
                count2++;
                if (count1<l1.size() && count2<l2.size()){
                    p1 = l1.get(count1);
                    p2 = l2.get(count2);
                }
                else{
                    break;
                }
            }
            // docID not match, increase lowest counter
            else if (p1.docID < p2.docID){
                count1++;
                if (count1<l1.size()){
                    p1 = l1.get(count1);
                }
                else{
                    break;
                }
            }
            // docID not match, increase lowest counter
            else{
                count2++;
                if (count2<l2.size()){
                    p2 = l2.get(count2);
                }
                else{
                    break;
                }
            }
        }

        return phrase;
    }
    

    /** [NEW]
     *  Intersects a set of queries *
     *  TODO: Skip pointer
     */
    public PostingsList intersect(Query query){
        // List with postings corresponding to the queries
        LinkedList<PostingsList> l = new LinkedList<PostingsList>();
        for (int i = 0; i<query.size(); i++){
            // If any query has zero matches, return 0 results
            if (!this.index.containsKey(query.terms.get(i)))
                return null;
            // Otherwise store postings in the list
            l.add(this.getPostings(query.terms.get(i)));
        }

        // Order the posting list by increasing document frequency
        l = sortByIncreasingFrequency(l); 


        PostingsList result = l.get(0);
        
        // In case only one word is queried
        if (l.size() == 1){
            return result;
        }

        // Apply algorithm as many times as words in the query
        for(int i = 1; i < l.size(); i++){
            result = intersect(result, l.get(i));
            if (result.isEmpty()){
                return null;
            }
        }
        return result;
    }



    /** [NEW]
     * Intersects two queries (represented by the postingslist) 
     */
    public PostingsList intersect(PostingsList l1, PostingsList l2){
        
        PostingsList intersection = new PostingsList();
        
        // Counters to iterate docIDs
        int count1 = 0;
        int count2 = 0;

        // First posting 
        PostingsEntry p1 = l1.get(0);
        PostingsEntry p2 = l2.get(0);

        while(true){
            // Match - EUREKA! //
            if (p1.docID == p2.docID){
                // Add match
                intersection.insert(p1.docID);
                // Increase counters
                count1++;
                count2++;
                // Go to next postings (check for nullpointer)
                if (count1<l1.size() && count2<l2.size()){
                    p1 = l1.get(count1);
                    p2 = l2.get(count2);
                }
                else
                    break;
            }
            // No match
            else if (p1.docID < p2.docID){
                count1++;
                if (count1<l1.size())
                    p1 = l1.get(count1);
                else
                    break;
            }
            // No match
            else{
                count2++;
                if (count2<l2.size())
                    p2 = l2.get(count2);
                else
                    break;
            }
        }

        return intersection;
    }


    /** [NEW]
     * Sorts a set of postings list according to the document frequency 
     */
    public LinkedList<PostingsList> sortByIncreasingFrequency(LinkedList<PostingsList> l){
        Collections.sort(l, new Comparator<PostingsList>(){
        @Override
        public int compare(PostingsList p1, PostingsList p2){
            if(p1.size() < p2.size()){
               return -1; 
            }
            if(p1.size() > p2.size()){
               return 1; 
            }
            return 0;
        }
        });
        return l;
    }


    /**
     *  No need for cleanup in a HashedIndex.
     */
    public void cleanup() {
    }
}
