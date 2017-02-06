/*  
 *   This file is part of the computer assignment for the
 *   Information Retrieval course at KTH.
 * 
 *   First version:  Johan Boye, 2010
 *   Second version: Johan Boye, 2012
 */  

package ir;

import java.util.*;
import java.io.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 *   A list of postings for a given word.
 */
public class DiskHashMap{
    // Maps tokens with corresponding termIDs
    private Map<String,Integer> termsID = new HashMap<String,Integer>();
    // termIDs mapping to PostingsList
    private HashMap<Integer, PostingsList> indexInMemory = new HashMap<Integer, PostingsList>();
    // Used when indexing to map tokens to termIDs
    private int termID;

    
    /* Constructor */
    public DiskHashMap(){
        this.termID = 0;
    }


    /** [MODIFIED]
     * Checks if the token has already been encountered, i.e. if it has
     *  appeared in the documents
     */
    public boolean containsKey(String token){
    	return termsID.containsKey(token);
    }


    /** [MODIFIED]
     *  Retrieve the PostingsList corresponding to an specific token
     */
    public PostingsList get(String token){
        
        // Read JSON file associated to token and return it as a postingslist
    	PostingsList p = new PostingsList();
        String filename = "postings/t"+termsID.get(token)+".json";
    	PostingsList post_tmp = new PostingsList();
        try(Reader reader = new FileReader(filename)){
            post_tmp = (new Gson()).fromJson(reader, PostingsList.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return post_tmp;
    	// Store it in PostingsList format
    }
    

    /** [NEW]
     * Obtain termID of term, only used when indexing.
     */ 
    public Integer termID(String token){
        Integer res;
        if (this.termsID.containsKey(token)){
            res = this.termsID.get(token);//.toString();
        }
        else{
            res = this.termID;//Integer.toString(this.termID);
            this.termsID.put(token, termID);
            this.termID++;   
        }
        return res;
    }


    /** [MODIFIED]
     * Add or update indexInMemory by adding new occurence information
     */
    public void put(String token, int docID, int offset){
    	// Check if token is contained in termsID
       if (this.termsID.containsKey(token)){
            // Update its corresponding postingslist with new occurence
	    	this.indexInMemory.get(termID(token)).insert(docID, offset);
        }
        else{
        	// Add new token to termsID, with its corresponding termID
	    	this.termsID.put(token, termID(token));
	    	// Create new postingslist for the token and store it in 
            // indexInMemory.
	    	PostingsList postingslist = new PostingsList();
	        postingslist.insert(docID, offset);
	        this.indexInMemory.put(termID(token), postingslist);
        }
    }

    
    /** [NEW]
     *  Saves termsID (token-termID map) + all postingslists
     */
    public void saveAll(){
        for (Map.Entry<Integer, PostingsList> entry : this.indexInMemory.entrySet()) {
            saveJSON("postings/t"+entry.getKey()+".json", entry.getValue());
            if (entry.getKey()%1000==0)
                System.err.println("storing "+ entry.getKey());
        }
        saveJSON("postings/termsID.json", this.termsID);
    }


    /** [NEW]
     * Loads hashmap containing mapping between tokens and termIDs 
     */
    public void loadTermsID(){
        try(Reader reader = new FileReader("postings/termsID.json")){
            this.termsID = (new Gson()).fromJson(reader, 
            new TypeToken<Map<String, Integer>>(){}.getType());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    /** [NEW]
     * Saves object o as a JSON file called fileName 
     */
    public void saveJSON(String fileName, Object o){
        Gson gson = new Gson();
        try(FileWriter writer = new FileWriter(fileName)){
            gson.toJson(o, writer);
            //System.err.println("\n Token stored");
        }catch (IOException e) {
            e.printStackTrace();
        }
    }


    /* Returns the keys from the index, i.e. the terms*/
    public Set<String> keySet(){
    	return this.termsID.keySet();
    }
}
	

			   
