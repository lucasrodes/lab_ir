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
public class BiwordIndex implements Index{
    private static int previousDocID = -1;
    private static String previousToken = new String();
    private Map<String, PostingsList> index = new TreeMap<String, PostingsList>();
    String bigram = new String();
    int count;
    /* [MODIFIED] Maps indices to document names */
    public Map<String, String> docIDs = new HashMap<String,String>();
    /* Maps name of document to its length (number of words) */
    public Map<String, Integer> docLengths = new HashMap<String,Integer>();

    /* This is what they should have given us! */
    Hashtable<String,Integer> titleToNumber = new Hashtable<String,Integer>();
    /**
     *   Mapping from document names to document numbers.
     */
    Hashtable<String,Integer> docNumber = new Hashtable<String,Integer>();
    /**
     * Page rank scores of each document
     */
    final static int MAX_NUMBER_OF_DOCS = 2000000;
    double[] pageRank = new double[MAX_NUMBER_OF_DOCS];

    public BiwordIndex(){
        this.count = 0;
        this.loadPageRank();
    }

    //  INDEX/SEARCH
    public void insert( String token, int docID, int offset ){


        if (previousDocID == docID) {
            bigram = previousToken + " " + token;
            //if (docID % 100 == 0)
                //System.err.println("Trying to insert ''" + bigram + "'' found in doc "+docID);
            if (this.index.containsKey(bigram)){
                this.index.get(bigram).insert(docID, offset);
            }

            else{
                //System.err.println("new token: " +token+" stored");
                PostingsList postingslist = new PostingsList();
                postingslist.insert(docID, offset);
                this.index.put(bigram, postingslist);
            }

            // Add to matrix of tf
            if (!this.tfMap.containsKey(docID)){
                HashMap<String, Integer> m = new HashMap<String, Integer>();
                m.put(bigram, 1);
                this.tfMap.put(docID, m);

                if (this.idfMap.containsKey(bigram)){
                    this.idfMap.put(bigram, this.idfMap.get(bigram)+1);
                    //System.err.println("  +1 for '" + bigram + "'");
                }
                else{
                    this.idfMap.put(bigram, 1);
                    //System.err.println("  new for '" + bigram + "'");
                }
            }

            else if (!this.tfMap.get(docID).containsKey(bigram)){
                this.tfMap.get(docID).put(bigram, 1);

                if (this.idfMap.containsKey(bigram)){
                    this.idfMap.put(bigram, this.idfMap.get(bigram)+1);
                    //System.err.println("  +1 for '" + bigram + "'");
                }
                else{
                    this.idfMap.put(bigram, 1);
                    //System.err.println("  new for '" + bigram + "'");
                }
            }

            else{
                int v = this.tfMap.get(docID).get(bigram) + 1;
                this.tfMap.get(docID).put(bigram, v);
            }
        }

        previousDocID = docID;
        previousToken = token;

    }

    public PostingsList getPostings( String token ){
        PostingsList post = new PostingsList();
        if(this.index.containsKey(token))
            post = (this.index.get(token)).clone();
        return post;
    }

    // MEMORY/DISK
    public void load(){
        System.err.println("NOT IMPLEMENTED");

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


    public void saveAll(){
        // Store postings
        for (Map.Entry<String, PostingsList> entry : this.index.entrySet()) {
            saveJSON("postings/tb"+hash(entry.getKey())+".json", entry.getValue());
            System.err.println("postings/tb"+hash(entry.getKey())+".json");
            count++;
            if (count%1000==0)
                System.err.println("storing "+ count);
        }
    }


    public void saveJSON(String fileName, Object o){

    }

    //public Iterator<String> getDictionaryInMemory();
    // SEARCH
    public PostingsList search( Query query, int queryType, int rankingType,
        int structureType ) {
        double idf_threshold = new Double(0);
        double index_elimination = new Double(0.008);
        // System.err.println(pageRank[docNumber.get("121")]);

        if (query.size()>0){
            switch (queryType){
                case Index.INTERSECTION_QUERY:
                    System.err.println("Intersection query for Bigram model not implemented");
                    return (new PostingsList()); //intersect(query);
                case Index.PHRASE_QUERY:
                    System.err.println("Phrase query for Bigram model not implemented");
                    return  (new PostingsList());//phrase_query(query);
                case Index.RANKED_QUERY:
                    System.err.println("Ranked query");
                    switch(rankingType){
                        case Index.TF_IDF: return ranked_query(query, 1, idf_threshold);
                        case Index.PAGERANK: return ranked_query(query, 0, idf_threshold);//ranked_query(query, 0, idf_threshold);//ranked_query2(query, 0);
                        case Index.COMBINATION: return ranked_query(query, index_elimination, idf_threshold);
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


    public PostingsList ranked_query(Query query, double w, double idf_threshold){
        // query: Query inserted by the user
        // w: Weight the tf-idf score, usually very small to account for scale difference with pagerank
        // idf_threshold: Threshold to perform index elimination

        /*
         * 1) Obtain union of results containing the considered query terms
         */
        long startTime = System.nanoTime();
        PostingsList result = new PostingsList();

        // Obtain union of terms above idf threshold if indexElimination is true
        // (Set idf_threshold to zero to disable this feature)
        Set<String> termsToConsider = this.queryTermsConsidered(query, idf_threshold);
        result = this.union_query(query, termsToConsider);
        //System.err.println("Size of result is " + result.size());

        long estimatedTime = (System.nanoTime() - startTime)/1000000;
        System.err.println("* (B) Union took: " + estimatedTime);


        /*
         * 2) Iterate over PostingsEntries and build the solution
         */
        startTime = System.nanoTime();
        // Required when iterating over a PostingsList
        PostingsEntry postEnt = new PostingsEntry();
        // Number of documents in the collection
        double nDocs = this.docIDs.size();
        // Weight of a query vector coefficient
        double w_query_term;
        // Document tf-idf variables
        double termFrequency_doc, documentFrequency_doc, w_doc_term;

        for(String bigram : termsToConsider){
            // Obtain idf of the term
            System.err.println("-- Now: " + bigram);
            documentFrequency_doc = Math.log(nDocs/new Double(this.idfMap.get(bigram)));

            // Obtain weight of the bigram
            w_query_term = query.bigramWeights.get(bigram);
            // Iterate over all documents containing the bigram and update the score(q,d)
            Iterator<PostingsEntry> it_d = result.iterator();
            while(it_d.hasNext()){
                postEnt = it_d.next();
                if (this.tfMap.get(postEnt.docID).containsKey(bigram)){
                    termFrequency_doc = this.tfMap.get(postEnt.docID).get(bigram);
                    w_doc_term = documentFrequency_doc*termFrequency_doc;
                    postEnt.score += w_query_term*w_doc_term;
                }
            }
        }
        estimatedTime = (System.nanoTime() - startTime)/1000000;
        System.err.println("* (B) Building solution took: " + estimatedTime);

        /*
         * 3) Normalize the scores
         */
        startTime = System.nanoTime();
        Iterator<PostingsEntry> it = result.iterator();
        while(it.hasNext()){
            postEnt = it.next();
            postEnt.score /= (new Double(this.docLengths.get(""+postEnt.docID)));//(Math.sqrt(postEnt.norm2));
            postEnt.score = w * postEnt.score + (1-w) * quality(postEnt.docID);
        }
        estimatedTime = (System.nanoTime() - startTime)/1000000;
        System.err.println("* (B) Normalizing took: " + estimatedTime);

        /*
         * 4) Sort the resulting solution
         */
        startTime = System.nanoTime();
        result.sort();
        estimatedTime = (System.nanoTime() - startTime)/1000000;
        System.err.println("* (B) Sorting took: " + estimatedTime);

        return result;
    }


    // Which terms should be considered? Based on Index elimination theory, idf-thresholding!
    public Set<String> queryTermsConsidered(Query query, double idf_threshold){
        double idf, nDocs = this.docIDs.size();
        String term, bigram;

        Set<String> termsToConsider = new HashSet<String>();

        Iterator<String> it = query.terms.iterator();
        previousToken = it.next();
        while(it.hasNext()){
            System.err.println(1);
            term = it.next();
            System.err.println(2);
            bigram = previousToken + " " + term;
            System.err.println("examining bigram : '"+ bigram +"'");
            idf = -1;
            if (this.idfMap.containsKey(bigram))
                idf = Math.log(nDocs/new Double(this.idfMap.get(bigram)));
            System.err.println("bigram '" + bigram+ "' with idf = " + idf);
            if (idf >= idf_threshold){
                System.err.println("    considered!");
                termsToConsider.add(bigram);
            }
            /*else{
                System.err.println(term + " not considered since idf = " + idf);
            }*/
            previousToken = term;
        }

        return termsToConsider;

    }

    public PostingsList union_query(Query query){

        PostingsList result = new PostingsList();
        String term, bigram;

        Iterator<String> it = query.terms.iterator();
        previousToken = it.next();
        while(it.hasNext()){
            term = it.next();
            bigram = previousToken + " " + term;
            if (!this.getPostings(bigram).isEmpty()){
                result = union_query(result, this.getPostings(bigram));
            }
            previousToken = term;
        }

        return result;
    }


    public PostingsList union_query(Query query, Set<String> consider){

        PostingsList result = new PostingsList();//this.getPostings(query.terms.get(0));
        String term, bigram;

        Iterator<String> it = query.terms.iterator();
        previousToken = it.next();
        while(it.hasNext()){
            term = it.next();
            bigram = previousToken + " " + term;
            System.err.println("Searching for '"+ bigram + "'");
            System.err.println("To be considered? " + consider.contains(bigram));
            System.err.println("Empty? " + this.getPostings(bigram).isEmpty());
            if (!this.getPostings(bigram).isEmpty() && consider.contains(bigram)){
                result = union_query(result, this.getPostings(bigram));
            }
            previousToken = term;
        }
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
                union.insert(p.docID, 0);
                docs.add(p.docID);
            }
        }
        //System.err.println("step 4");
        return union;
    }


    public double quality(int d){
        return this.pageRank[this.docNumber.get(""+titleToNumber.get(this.docIDs.get(""+d)))];
    }


    public void cleanup(){

    }

    public static String hash(String token){
        return Integer.toString( token.hashCode() );
    }

    public int getDocLength(int id){
        return this.docLengths.get( "" + id);
    }
    public void putDocLength(int id, int offset){

    }
    public int getNumberDocs(){
        return this.docIDs.size();
    }
    public void putdDocID( int id, String filename ){

    }
    public String getDocName ( int id ){
        return this.docIDs.get( "" + id );
    }

    public void setParameters(HashedIndex i){
        this.docIDs = i.docIDs;
        this.docLengths = i.docLengths;
    }
}
