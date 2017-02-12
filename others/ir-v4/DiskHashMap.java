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
    private HashMap<String,Integer> termsID = new HashMap<String,Integer>();
    private HashMap<Integer, PostingsList> indexInMemory = new HashMap<Integer, PostingsList>();
    private int termID = 0;

    public DiskHashMap(){

    }

    public boolean containsKey(String token){
    	return termsID.containsKey(token);
    }

    public PostingsList get(String token){
    	Gson gson = new Gson();
        // Read file associated to token
    	PostingsList p = new PostingsList();
        String filename = "postings/t"+termID(token)+".json";
    	PostingsList post_tmp = new PostingsList();
        try(Reader reader = new FileReader(filename)){
            post_tmp = gson.fromJson(reader, PostingsList.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return post_tmp;
    	// Store it in PostingsList format
    }
    

    /* Obtain termID of term */ 
    public Integer termID(String token){
        Integer res;
        if (this.containsKey(token)){
            res = this.termsID.get(token);//.toString();
        }
        else{
            res = this.termID;//Integer.toString(this.termID);
            this.termsID.put(token, termID);
            this.termID++;
            
        }
        return res;
    }

    /* Add or update postingslist*/
    public void put(String token, int docID, int offset){
    	if (this.containsKey(token)){
	    	PostingsList p = readJSON(token);
	    	p.insert(docID, offset);
	    	writeJSON(token, p);
        }
        else{
        	// Add new token to hashmap, with its corresponding fileName
	    	this.termsID.put(token, termID(token));
	    	// Create new postingslist
	    	PostingsList postingslist = new PostingsList();
	        postingslist.insert(docID, offset);
	        // Store postingslist in memory
	        this.writeJSON(token, postingslist);
        }
    }

    
    public void writeAll(){

    }

    
    /* Stores the postingslist p in the file named filename */
    public void writeJSON(String token, PostingsList p){
    	Gson gson = new Gson();
        //String json = gson.toJson(p);
        String filename =  "postings/t"+termID(token)+".json";
        //System.out.println(json);
        try(FileWriter writer = new FileWriter(filename)){
            gson.toJson(p, writer);
            //System.err.println("\n Token stored");
        }catch (IOException e) {
            e.printStackTrace();
        }
    }


    /* Read JSON */
    public PostingsList readJSON(String token){
    	Gson gson = new Gson();
        PostingsList post_tmp = new PostingsList();
        String filename = "postings/t"+termID(token)+".json";
        try(Reader reader = new FileReader(filename)){
            post_tmp = gson.fromJson(reader, PostingsList.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return post_tmp;
    }

    /* Returns the keys from the index, i.e. the terms*/
    public Set<String> keySet(){
    	return this.termsID.keySet();
    }
}
	

			   
