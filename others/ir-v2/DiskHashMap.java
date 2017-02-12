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
    private HashMap<String, String> tokensAndFiles = new HashMap<String,String>();
    private HashMap<String, PostingsList> block = new HashMap<String, PostingsList>();
    private int tokenID = 0;
    private int blockID = 0;

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
        String filename;
        // Get termID
        if (this.containsKey(token)){
	    	filename =  this.tokensAndFiles.get(token);
        }
        else{
	    	filename =  Integer.toString(this.tokenID);
            this.tokensAndFiles.put(token, filename);
        }

        // Add postings to the block
        if(this.block.containsKey(filename))
            this.block.get(filename).insert(docID, offset);
        else{
            PostingsList postingslist = new PostingsList();
            postingslist.insert(docID, offset);
            this.block.put(filename, postingslist);
        }

    }


    /* Generate new termID */
    public String termID(){
        String tokenID_str = Integer.toString(this.tokenID);
        this.tokenID++;
        return "postings/t"+tokenID_str+".ser"; 
    }

    /* Obtain termID of term */ 
    public String termID(String term){
        return this.tokensAndFiles.get(term);
    }


    /* Write current block list */
    public void writeBlock(){
        String blockName = "postings/b"+Integer.toString(this.blockID)+".ser";
        try{
            FileOutputStream fileOut = new FileOutputStream(new File(blockName));
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject( this.block );
            out.close();
            //System.out.println(filename+" correctly stored");
        }
        catch(IOException i){
            i.printStackTrace();
        } 
        this.tokenID = 0;
        this.blockID++;
        this.block = new HashMap<String, PostingsList>();
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
	

			   
