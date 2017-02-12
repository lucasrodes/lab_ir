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

/**
 *   A list of postings for a given word.
 */
public class DiskHashMap{
    private HashMap<String,String> tokensAndFiles = new HashMap<String,String>();
    private int id = 0;

    public DiskHashMap(){

    }

    public boolean containsKey(String token){
    	return tokensAndFiles.containsKey(token);
    }

    public PostingsList get(String token){
    	// Read file associated to token
    	String filename = this.tokensAndFiles.get(token);
    	return this.readFile(filename);
    	// Store it in PostingsList format
    }
    

    /* Add or update postingslist*/
    public void put(String token, int docID, int offset){
    	if (this.containsKey(token)){
	    	String filename = this.tokensAndFiles.get(token);
	    	PostingsList p = readFile(filename);
	    	p.insert(docID, offset);
	    	writeFile(filename, p);
        }
        else{
        	// Add new token to hashmap, with its corresponding fileName
	    	String a = Integer.toString(this.id);
	    	String filename =  "postings/token"+a+".ser";
	    	this.id++;
	    	this.tokensAndFiles.put(token, filename);
	    	// Create new postingslist
	    	PostingsList postingslist = new PostingsList();
	        postingslist.insert(docID, offset);
	        // Store postingslist in memory
	        this.writeFile(filename, postingslist);
        }
    }

    
    /* Stores the postingslist p in the file named filename */
    public void writeFile(String filename, PostingsList p){
    	try{
    		FileOutputStream fileOut = new FileOutputStream(new File(filename));
    		ObjectOutputStream out = new ObjectOutputStream(fileOut);
    		out.writeObject( p );
    		out.close();
    		//System.out.println(filename+" correctly stored");
    	}
    	catch(IOException i){
            i.printStackTrace();
        } 
    }


    /* */
    public PostingsList readFile(String filename){
    	try{
    		PostingsList p = new PostingsList();
    		FileInputStream fileIn = new FileInputStream(new File(filename));
    		ObjectInputStream in = new ObjectInputStream(fileIn);
    		p = (PostingsList) in.readObject();
    		return p;
		}
		catch(IOException i){
            i.printStackTrace();
            return null;
        }
        catch(ClassNotFoundException c)
        {
            System.out.println("PostingsList class not found");
            c.printStackTrace();
            return null;
        } 
    }

    /* Returns the keys from the index, i.e. the terms*/
    public Set<String> keySet(){
    	return this.tokensAndFiles.keySet();
    }
}
	

			   
