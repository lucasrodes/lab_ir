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

    // Map mapping terms to postingslist (how many times a term appears in each document)
    private Map<String, PostingsList> index
            = new TreeMap<String, PostingsList>();

    // NEW IN TASK 3
    // Map mapping a docID to the set of terms appearing in it. In particular it mapps to a
    // map which maps term to number of appearences
    public Map<Integer, HashMap<String, Integer>> tfMap
            = new TreeMap<Integer, HashMap<String, Integer>>();
    public Map<String, Integer> idfMap = new HashMap<String, Integer>();

    /* [MODIFIED] Maps indices to document names */
    public Map<String, String> docIDs = new HashMap<String,String>();
    /* Maps name of document to its length (number of words) */
    public Map<String, Integer> docLengths = new HashMap<String,Integer>();
    int count;// Used when indexing to map tokens to termIDs
    /* Maps name of document to its norm (norm of tf-idf vector of the document) */
    //public Map<String,Integer> docNorms = new HashMap<String,Integer>();
    /* Stores number of appearences of a word in a document */
    //public Map<String,Integer> docNorms = new HashMap<String,Integer>();

    final static int MAX_NUMBER_OF_DOCS = 2000000;


    /* This is what they should have given us! */
    Hashtable<String,Integer> titleToNumber = new Hashtable<String,Integer>();
    /**
     *   Mapping from document names to document numbers.
     */
    Hashtable<String,Integer> docNumber = new Hashtable<String,Integer>();
    /**
     * Page rank scores of each document
     */
    double[] pageRank = new double[MAX_NUMBER_OF_DOCS];

    /** Constructor */
    public HashedIndex(){
        this.loadPageRank();
        this.count = 0;
    }

// INDEXING MANAGEMENT --------------------------------------------------

    /** [MODIFIED]
     *  Inserts new information provided by new occurence of token
     *  in docID at offset.
     */
    public void insert( String token, int docID, int offset ) {
        // Add to map of postingslist
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

        // Add to matrix of tf
        if (!this.tfMap.containsKey(docID)){
            HashMap<String, Integer> m = new HashMap<String, Integer>();
            m.put(token, 1);
            this.tfMap.put(docID, m);

            if (this.idfMap.containsKey(token))
                this.idfMap.put(token, this.idfMap.get(token)+1);
            else
                this.idfMap.put(token, 1);
        }

        else if (!this.tfMap.get(docID).containsKey(token)){
            this.tfMap.get(docID).put(token, 1);

            if (this.idfMap.containsKey(token))
                this.idfMap.put(token, this.idfMap.get(token)+1);
            else
                this.idfMap.put(token, 1);
        }

        else{
            int v = this.tfMap.get(docID).get(token) + 1;
            this.tfMap.get(docID).put(token, v);
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

        try(Reader reader = new FileReader("postings/titleToNumber.json")){
            this.titleToNumber = (new Gson()).fromJson(reader,
            new TypeToken<Map<String, Integer>>(){}.getType());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void loadPageRank(){
        try(Reader reader = new FileReader("ir/files/docNumber.json")){
            this.docNumber = (new Gson()).fromJson(reader,
            new TypeToken<Hashtable<String, Integer>>(){}.getType());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try(Reader reader = new FileReader("ir/files/pageRank.json")){
            this.pageRank = (new Gson()).fromJson(reader,
            new TypeToken<double[]>(){}.getType());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try(Reader reader = new FileReader("ir/files/titleToNumber.json")){
            this.titleToNumber = (new Gson()).fromJson(reader,
            new TypeToken<Hashtable<String,Integer>>(){}.getType());
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
        PostingsList post = new PostingsList();
        if(this.index.containsKey(token))
            post = (this.index.get(token)).clone();
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

        // System.err.println(pageRank[docNumber.get("121")]);

        if (query.size()>0){
            switch (queryType){
                case Index.INTERSECTION_QUERY:
                    System.err.println("Intersection query");
                    return intersect(query);
                case Index.PHRASE_QUERY:
                    System.err.println("Phrase query");
                    return  phrase_query(query);
                case Index.RANKED_QUERY:
                    System.err.println("Ranked query");
                    switch(rankingType){
                        case Index.TF_IDF: return ranked_query(query, 1);
                        case Index.PAGERANK: return ranked_query(query, 0);//ranked_query2(query, 0);
                        case Index.COMBINATION: return ranked_query(query, 0.005);
                    }
                default:
                    System.out.println("not valid query");
                    return null;
            }
        }
        else
            // Query was empty
            return null;
    }


    public PostingsList ranked_query(Query query, double w){
        // w denotes how much we rely on tf-idf, should be small due to scale between tf-idf and
        // pagerank

        PostingsList result = this.union_query(query);//new PostingsList();
        PostingsEntry postEnt = new PostingsEntry();

        double nDocs = this.docIDs.size();
        double w_query_term;
        double termFrequency_doc, documentFrequency_doc, w_doc_term;

        String term;


        for(Map.Entry<String, Double> entry : query.weights.entrySet()){
            term = entry.getKey();
            w_query_term = entry.getValue();
            documentFrequency_doc = Math.log(nDocs/new Double(this.idfMap.get(term)));
            Iterator<PostingsEntry> it_d = result.iterator();
            while(it_d.hasNext()){
                postEnt = it_d.next();
                if (this.tfMap.get(postEnt.docID).containsKey(term)){
                    termFrequency_doc = this.tfMap.get(postEnt.docID).get(term);
                    w_doc_term = documentFrequency_doc*termFrequency_doc;
                    postEnt.score += w_query_term*w_doc_term;
                }
            }
        }

        // TODO: Normalize the score of document d with the norm of the vector containing
        // the tf-idf values of all the terms in document d.

        Iterator<PostingsEntry> it = result.iterator();
        while(it.hasNext()){
            postEnt = it.next();
            postEnt.score /= (new Double(this.docLengths.get(""+postEnt.docID)));//(Math.sqrt(postEnt.norm2));
            postEnt.score = w * postEnt.score + (1-w) * quality(postEnt.docID);
        }

        result.sort();
        return result;
    }


    public PostingsList ranked_query1(Query query, double w){
        // w denotes how much we rely on tf-idf, should be small due to scale between tf-idf and
        // pagerank

        PostingsList result = this.union_query(query);//new PostingsList();
        PostingsEntry postEnt = new PostingsEntry();

        double nDocs = this.docIDs.size();
        double w_query_term;
        double termFrequency_doc, documentFrequency_doc, w_doc_term;

        String term;


        for(Map.Entry<String, Double> entry : query.weights.entrySet()){
            term = entry.getKey();
            w_query_term = entry.getValue();
            documentFrequency_doc = Math.log(nDocs/new Double(this.idfMap.get(term)));
            Iterator<PostingsEntry> it_d = result.iterator();
            while(it_d.hasNext()){
                postEnt = it_d.next();
                if (this.tfMap.get(postEnt.docID).containsKey(term)){
                    termFrequency_doc = this.tfMap.get(postEnt.docID).get(term);
                    w_doc_term = documentFrequency_doc*termFrequency_doc;
                    postEnt.score += w_query_term*w_doc_term;
                }
            }
        }

        // TODO: Normalize the score of document d with the norm of the vector containing
        // the tf-idf values of all the terms in document d.

        Iterator<PostingsEntry> it = result.iterator();
        while(it.hasNext()){
            postEnt = it.next();
            postEnt.score /= (new Double(this.docLengths.get(""+postEnt.docID)));//(Math.sqrt(postEnt.norm2));
            postEnt.score = w * postEnt.score + (1-w) * quality(postEnt.docID);
        }

        result.sort();
        return result;
    }


    public PostingsList ranked_query2(Query query, double w){
        // w denotes how much we rely on tf-idf

        PostingsList result = new PostingsList();
        PostingsList postList = new PostingsList();
        PostingsList pospptList = this.union_query(query);
        PostingsEntry postEnt = new PostingsEntry();
        Map<String, Double> queryRecord = new HashMap<String, Double>();
        String q;

        double nDocs = this.docIDs.size();
        double termFrequency_query, documentFrequency_query, w_query;
        double termFrequency_doc, documentFrequency_doc, w_doc;

        // Obtain number of occurrences of each term in the query
        Iterator<String> it_q = query.terms.iterator();
        while(it_q.hasNext()){
            q = it_q.next();
            if (!queryRecord.containsKey(q))
                queryRecord.put(q, new Double(1));
        }
        //Map<String, Double> map = query.weights.iterator();
        //for(Map.Entry<String, Double> entry : map.entrySet()){

        // Iterate over each query
        it_q = query.terms.iterator();
        while(it_q.hasNext()){
            q = it_q.next();
            postList = getPostings(q);
            //w_query = query.weights.get(q);
            //System.err.println("count: " + queryRecord.get(query.terms.get(i)));
            //System.err.println("size: " + query.size());

            w_query = queryRecord.get(q)/(new Double(query.size()));
            //termFrequency_query = 1+ Math.log(termFrequency_query);
            //documentFrequency_query = 1;//Math.log(nDocs/postList.size());
            //w_query = termFrequency_query*documentFrequency_query;
            // Ignote terms in query not appearing in collection
            /*if (Double.isInfinite(idf)){
                idf = 0;
            }*/

            /*System.err.println(q);
            System.err.println("\t tf_q: " +termFrequency_query);
            System.err.println("\t idf: " +documentFrequency_query);
            System.err.println("\t tf*idf: " +termFrequency_query*documentFrequency_query);*/

            // Iterate over each document
            documentFrequency_doc = Math.log(nDocs/postList.size());
            Iterator<PostingsEntry> it_d = postList.iterator();
            while(it_d.hasNext()){
                postEnt = it_d.next();
                termFrequency_doc = postEnt.positions.size();//
                    //(new Double(this.docLengths.get(""+postEnt.docID)));
                //termFrequency_doc = 1+ Math.log(termFrequency_doc);
                w_doc =documentFrequency_doc*termFrequency_doc;
                /*if (this.docIDs.get(""+postEnt.docID).equals("Zombie_Walk.f")){
                    System.err.println("\n" + this.docIDs.get(""+postEnt.docID));
                    System.err.println("\t idf = " + documentFrequency_doc);
                    System.err.println("\t tf_d = " + termFrequency_doc);
                    System.err.println("\t w_doc = " + w_doc);
                    System.err.println("\t w_query*w_doc = " + w_query*w_doc);
                }*/
                //System.err.println("\n\t score = " + tf_q*idf*tf_d*idf);
                //System.err.println("\t norm_d = " + (tf_d*idf)*(tf_d*idf));
                result.insert(postEnt.docID, w_query*w_doc);
            }
        }

        // TODO: Normalize the score of document d with the norm of the vector containing
        // the tf-idf values of all the terms in document d.

        Iterator<PostingsEntry> it = result.iterator();
        while(it.hasNext()){
            postEnt = it.next();
            /*if (this.docIDs.get(""+postEnt.docID).equals("Zombie_Walk.f")){
                System.err.println("\n" + this.docIDs.get(""+postEnt.docID));
                System.err.println("\t w = " + postEnt.score + "/"+
                    (new Double(this.docLengths.get(""+postEnt.docID))));
            }*/
            postEnt.score /= (new Double(this.docLengths.get(""+postEnt.docID)));//(Math.sqrt(postEnt.norm2));
            /*if (this.docIDs.get(""+postEnt.docID).equals("Zombie_Walk.f")){
                System.err.println("\t w = " + postEnt.score);
            }*/

            /*if (this.docIDs.get(""+postEnt.docID).equals("Davis.f")){
                System.err.println(postEnt.docID + " corresponds to " + this.docIDs.get(""+postEnt.docID));
            }*/
            postEnt.score = w * postEnt.score + (1-w) * quality(postEnt.docID);
        }
        //System.err.println("Peta? --- "+titleToNumber.get("Davis.f"));
        result.sort();
        return result;
    }


    public double quality(int d){
        /*if (this.docIDs.get(""+d).equals("Davis.f"))
        {
            System.err.println(this.docIDs.get(""+d));
            System.err.println(titleToNumber.get(this.docIDs.get(""+d)));
        }*/
        //System.err.println(d+"--"+this.docIDs.get(""+d)+"--"+titleToNumber.get(this.docIDs.get(""+d)));
        return this.pageRank[this.docNumber.get(""+titleToNumber.get(this.docIDs.get(""+d)))];
    }


    public PostingsList union_query(Query query){

        PostingsList result = new PostingsList();//this.getPostings(query.terms.get(0));

        for (int i = 0; i<query.size(); i++){
            if (!this.getPostings(query.terms.get(i)).isEmpty()){
                result = union_query(result, this.getPostings(query.terms.get(i)));
            }
        }
        //System.err.println("step 5");
        return result;
    }


    public PostingsList union_query(PostingsList l1, PostingsList l2){

        Set<Integer> docs = new HashSet<Integer>();
        PostingsList union = new PostingsList();

        //System.err.println("step 1");
        Iterator<PostingsEntry> it = l1.iterator();
        //System.err.println("step 2");
        PostingsEntry p = new PostingsEntry();
        while(it.hasNext()){
            p = it.next();
            union.insert(p.docID, 0);
            docs.add(p.docID);
        }
        //System.err.println("step 3");
        it = l2.iterator();
        p = new PostingsEntry();
        while(it.hasNext()){
            p = it.next();
            if(!docs.contains(p.docID)){
                union.insert(p.docID,0);
                docs.add(p.docID);
            }
        }
        //System.err.println("step 4");
        return union;
    }

    /** [NEW]
     * Finds documents containing the query as a phrase
     * TODO: Skip pointer
     */
    public PostingsList phrase_query(Query query){

        LinkedList<PostingsList> listQueriedPostings = new LinkedList<PostingsList>();
        // List with postings corresponding to the queries
        for (int i = 0; i<query.size(); i++){
            // If any query has zero matches, return 0 results
            //if(!(new File("postings/t"+hash(query.terms.get(i))+".json")).exists()){
            if (!this.index.containsKey(query.terms.get(i))){
                return null;
            }
            // Otherwise store postings in the list
            listQueriedPostings.add(this.getPostings(query.terms.get(i)));
        }

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
    public PostingsList intersect(Query query){

        LinkedList<PostingsList> listQueriedPostings = new LinkedList<PostingsList>();
        // List with postings corresponding to the queries
        for (int i = 0; i<query.size(); i++){
            // If any query has zero matches, return 0 results
            //if(!(new File("postings/t"+hash(query.terms.get(i))+".json")).exists()){
            if (!this.index.containsKey(query.terms.get(i))){
                return null;
            }
            // Otherwise store postings in the list
            listQueriedPostings.add(this.getPostings(query.terms.get(i)));
        }

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
