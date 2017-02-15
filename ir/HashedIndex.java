/*  
 *   This file is part of the computer assignment for the
 *   Information Retrieval course at KTH.
 * 
 *   First version:  Johan Boye, 2010
 *   Second version: Johan Boye, 2012
 *   Additions: Hedvig Kjellström, 2012-14
 */  


package ir;

import java.util.*;
import java.lang.*;
import java.io.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 *   Implements an inverted index as a Hashtable from words to PostingsLists.
 */
public class HashedIndex implements Index {

    private Map<String, PostingsList> index 
            = new TreeMap<String, PostingsList>();
    /* [MODIFIED] Maps indices to document names */
    public Map<String, String> docIDs = new HashMap<String,String>();
    public Map<String,Integer> docLengths = new HashMap<String,Integer>();
    int count;// Used when indexing to map tokens to termIDs


    /** Constructor */
    public HashedIndex(){
        this.count = 0;
    }

// INDEXING MANAGEMENT --------------------------------------------------

    /** [MODIFIED]
     *  Inserts new information provided by new occurence of token 
     *  in docID at offset.
     */
    public void insert( String token, int docID, int offset ) {
        if (this.index.containsKey(token)){
            //System.err.println(token+" already in memory");
            this.index.get(token).insert(docID, offset);
        }
        else{
            //System.err.println("new token: " +token+" stored");
            PostingsList postingslist = new PostingsList();
            postingslist.insert(docID, offset);
            this.index.put(token, postingslist);
        }
    }


// DISK/MEMORY MANAGEMENT -----------------------------------------------


    /** [NEW]
     * Recovers the essential files required to retrieve information in memory. 
     * This includes hashmap mapping tokens with respective terms and list of 
     * names of the retrieved documents.
     */
    public void load(){
        // Recover docIDs
        try(Reader reader = new FileReader("postings/docIDs.json")){
            this.docIDs = (new Gson()).fromJson(reader, 
            new TypeToken<Map<String, String>>(){}.getType());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try(Reader reader = new FileReader("postings/docLengths.json")){
            this.docLengths = (new Gson()).fromJson(reader, 
            new TypeToken<Map<String, Integer>>(){}.getType());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /** [NEW] 
     *  Saves the token-termIDs map and list with document names
     */
    public void saveAll(){
        // Store postings 
        for (Map.Entry<String, PostingsList> entry : this.index.entrySet()) {
            saveJSON("postings/t"+hash(entry.getKey())+".json", entry.getValue());
            System.err.println("postings/t"+hash(entry.getKey())+".json");
            count++;
            if (count%1000==0)
                System.err.println("storing "+ count);
        }
        // Store mapping ID<->document names mapping
        this.saveJSON("postings/docIDs.json", this.docIDs);
        this.saveJSON("postings/docLengths.json", this.docLengths);
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


    /**
     *  No need for cleanup in a HashedIndex.
     */
    public void cleanup() {
        this.index = new TreeMap<String, PostingsList>();
        this.count = 0;
    }

  
// SEARCH MANAGEMENT (USER) ---------------------------------------------


    /** [MODIFIED]
     *  Returns the postings for a specific term, or null
     *  if the term is not in the index.
     */
    public PostingsList getPostings( String token ) {
        PostingsList post = (this.index.get(token)).clone();
        /*System.err.println("start search...");
        // Read JSON file associated to token and return it as a postingslist
        String filename = "postings/t"+hash(token)+".json";
        PostingsList post = new PostingsList();
        try(Reader reader = new FileReader(filename)){
            post = (new Gson()).fromJson(reader, PostingsList.class);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        return post;
    }


// QUERIES MANAGEMENT --------------------------------------------------

    /**
     *  Searches the index for postings matching the query.
     */
    public PostingsList search( Query query, int queryType, int rankingType, 
        int structureType ) {

        LinkedList<PostingsList> l = new LinkedList<PostingsList>();
        if (query.size()>0){ 
            // List with postings corresponding to the queries
            for (int i = 0; i<query.size(); i++){
                // If any query has zero matches, return 0 results
                //if(!(new File("postings/t"+hash(query.terms.get(i))+".json")).exists()){
                if (!this.index.containsKey(query.terms.get(i))){
                    return null;}
                // Otherwise store postings in the list
                l.add(this.getPostings(query.terms.get(i)));
            }
            
            PostingsList result = new PostingsList();

            switch (queryType){
                case Index.INTERSECTION_QUERY: 
                    System.err.println("Intersection query");
                    return intersect(l);
                case Index.PHRASE_QUERY: 
                    System.err.println("Phrase query");
                    return  phrase_query(l);
                case Index.RANKED_QUERY:
                    System.err.println("Ranked query");
                    return ranked_query(l);
                default: 
                    System.out.println("not valid query");
                    return null;
            }
        }
        else
            // Query was empty
            return null;
    }


    public PostingsList ranked_query(LinkedList<PostingsList> listQueriedPostings){        
        
        PostingsList result = listQueriedPostings.get(0);

        //System.err.println("result: "+ result.toString());
        double df = result.size();
        double N = this.docIDs.size();
        double idf = 0;
        double tf = 0;
        double l = 0;

        /*PostingsEntry pi = new PostingsEntry();
        for(int i = 0; i<result.size(); i++){
            pi = result.get(i);
            tf = pi.positions.size();
            N = this.docLengths.get(""+pi.docID);
            idf = Math.log(N/df);
            pi.setScore(tf*idf/N);
        }*/
        Iterator<PostingsEntry> it = result.iterator();
        PostingsEntry pi = new PostingsEntry();
        while (it.hasNext()){
            pi = it.next();
            tf = pi.positions.size();
            l = this.docLengths.get(""+pi.docID);
            idf = Math.log(N/df);
            pi.setScore(tf*idf/l);
        }

        result.sort();
        // In case only one word is queried
        /*if (listQueriedPostings.size() == 1){
            return result;
        }

        // Apply algorithm as many times as words in the query
        for(int i = 1; i < listQueriedPostings.size(); i++){
            result = phrase_query(result, listQueriedPostings.get(i));
            if (result.isEmpty()){
                return null;
            }
        }*/
        return result;
    }

    /** [NEW] 
     * Finds documents containing the query as a phrase 
     * TODO: Skip pointer
     */
    public PostingsList phrase_query(LinkedList<PostingsList> listQueriedPostings){
        
        PostingsList result = new PostingsList();
        result = listQueriedPostings.get(0);

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
    public PostingsList intersect(LinkedList<PostingsList> listQueriedPostings){

        // Order the posting list by increasing document frequency
        listQueriedPostings = sortByIncreasingFrequency(listQueriedPostings); 

        PostingsList result = (PostingsList)listQueriedPostings.get(0);
   
        // In case only one word is queried
        if (listQueriedPostings.size() == 1){
            return result;
        }

        // Apply algorithm as many times as words in the query
        for(int i = 1; i < listQueriedPostings.size(); i++){
            result = intersect(result, listQueriedPostings.get(i));
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


// OTHERS --------------------------------------------------------------
    
    /** [MODIFIED]
     *  Returns all the words in the index.
     */
    public Set<String> keySet() {
        return this.index.keySet();
    }  


    /** [NEW]
     *  Hashes the string to unique int representation
     */
    public static String hash(String token){
        return Integer.toString( token.hashCode() );
    } 


    /** [NEW]
     *  Strings the map containing all postingslist and tokens
     */
    public String toString(){
        String s = " ";
        for (Map.Entry<String, PostingsList> entry : this.index.entrySet()) {
            s += entry.getKey()+" - " + entry.getValue().toString() + "\n";
        }
        return s;//this.indexInMemory.toString();
    } 

}
