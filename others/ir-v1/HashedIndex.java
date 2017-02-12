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
    //private HashMap<String,PostingsList> index = new HashMap<String,PostingsList>();
    private DiskHashMap indexDisk = new DiskHashMap();

    /**
     *  Accounts for token appearing in docID, by Inserts this token in index.
     */
    public void insert( String token, int docID, int offset ) {
        /* Check if Map already contains the term token and if it already
           appeared in docID */
        //if(this.index.containsKey(token))// &&
        //    this.index.get(token).insert(docID, offset);
        /* Dictionary does not contain the term token, so we add a new
           postingslist with docID and the corresponding positional index 
         */
        /*else{
            PostingsList postingslist = new PostingsList();
            postingslist.insert(docID, offset);
            this.index.put(token, postingslist);*/
            // this.indexDisk.add(token, docID, offset)
        //}
        this.indexDisk.put(token, docID, offset);
    }


    /**
     *  Returns all the words in the index.
     */
    public Iterator<String> getDictionary() {
        //return this.index.keySet().iterator();
        return this.indexDisk.keySet().iterator();
    }


    /**
     *  Returns the postings for a specific term, or null
     *  if the term is not in the index.
     */
    public PostingsList getPostings( String token ) {
	   return this.indexDisk.get(token);
    }


    /**
     *  Searches the index for postings matching the query.
     */
    public PostingsList search( Query query, int queryType, int rankingType, 
        int structureType ) {
        // Construct a linkedlist with the postings of all queries
        if (query.size()>0){
            switch (queryType){
                case Index.INTERSECTION_QUERY: return intersect(query);
                case Index.PHRASE_QUERY: return phrase_query(query);
                case Index.RANKED_QUERY:
                    System.out.println("ranked query");
                    return null;
                default: 
                    System.out.println("not valid query");
                    return null;
            }
        }
        else
            return null;
    }


    /* Finds documents containing the query as a phrase */
    // TODO: Skip pointer
    public PostingsList phrase_query(Query query){
        
        // List with postings corresponding to the queries
        LinkedList<PostingsList> l = new LinkedList<PostingsList>();
        for (int i = 0; i<query.size(); i++){
            // If any query has zero matches, return 0 results
            if (!this.indexDisk.containsKey(query.terms.get(i)))
                return null;
            // Otherwise store postings in the list
            l.add(this.getPostings(query.terms.get(i)));
        }

        PostingsList result = l.get(0);

        // In case only one word is submitted
        if (l.size() == 1){
            return result;
        }

        // Apply algorithm as many times as words in the query
        for(int i = 1; i < l.size(); i++){
            result = phrase_query(result, l.get(i));
            if (result.isEmpty()){
                return null;
            }
        }
        return result;
    }


    /* Finds documents containing a two-words phrase */
    public PostingsList phrase_query(PostingsList l1, PostingsList l2){
        
        /*if(l1.isEmpty() || l2.isEmpty()){
            System.out.println("null");
            return null;
        }*/

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
            // docID match
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
                    // Match, consecutive words
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
    

    /* Intersects a set of queries */
    // TODO: Skip pointer
    public PostingsList intersect(Query query){
        
        // List with postings corresponding to the queries
        LinkedList<PostingsList> l = new LinkedList<PostingsList>();
        for (int i = 0; i<query.size(); i++){
            // If any query has zero matches, return 0 results
            if (!this.indexDisk.containsKey(query.terms.get(i)))
                return null;

            // Otherwise store postings in the list
            l.add(this.getPostings(query.terms.get(i)));
        }

        // Order the posting list by increasing document frequency
        l = sortByIncreasingFrequency(l); 

        PostingsList result = l.get(0);
        if (l.size() == 1){
            return result;
        }

        for(int i = 1; i < l.size(); i++){
            result = intersect(result, l.get(i));
            if (result.isEmpty()){
                return null;
            }
        }
        return result;
    }


    /* Sorts a set of postings list according to the document frequency */
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


    /* Intersects two queries */
    public PostingsList intersect(PostingsList l1, PostingsList l2){
        
        /*if(l1.isEmpty() || l2.isEmpty()){
            System.out.println("null");
            return null;
        }*/

        PostingsList intersection = new PostingsList();
        
        int count1 = 0;
        int count2 = 0;

        PostingsEntry p1 = l1.get(0);
        PostingsEntry p2 = l2.get(0);
        while(true){
            if (p1.docID == p2.docID){
                intersection.insert(p1.docID);
                count1++;
                count2++;
                if (count1<l1.size() && count2<l2.size()){
                    p1 = l1.get(count1);
                    p2 = l2.get(count2);
                }
                else
                    break;
            }
            else if (p1.docID < p2.docID){
                count1++;
                if (count1<l1.size())
                    p1 = l1.get(count1);
                else
                    break;
            }
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


    /**
     *  No need for cleanup in a HashedIndex.
     */
    public void cleanup() {
    }
}
