/*  
 *   This file is part of the computer assignment for the
 *   Information Retrieval course at KTH.
 * 
 *   First version:  Johan Boye, 2010
 *   Second version: Johan Boye, 2012
 */  

// IDEA: Store BlockID, i.e. where the fragment of the posting is located

package ir;

import java.util.*;
import java.io.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
/**
 *   A list of postings for a given word.
 */
public class DiskHashMap{
    private HashMap<Integer, PostingsList> block = new HashMap<Integer, PostingsList>();
    private HashMap<String, Integer> termsID = new HashMap<String, Integer>();
    private HashMap<Integer, LinkedList<Integer>> blocksID = 
            new HashMap<Integer, LinkedList<Integer>>();
    private int termID = 0;
    private int blockID = 0;

    public DiskHashMap(){

    }

    public boolean containsKey(String token){
    	return this.termsID.containsKey(token);
    }

    public PostingsList get(String token){
        Gson gson = new Gson();
        PostingsList p = new PostingsList();
        // Obtain blocks containing fragments of the posting
        LinkedList<Integer> listOfBlocks = this.blocksID.get(termID(token));
        //System.err.println(listOfBlocks.toString());
        for(int i = 0; i < listOfBlocks.size(); i++){
            //System.err.println(this.blocksID.get(termID(token)).toString());
            String filename = "postings/b"+listOfBlocks.get(i)+".json";
            //System.err.println(filename);
            Map<Integer, PostingsList> block_tmp = new HashMap<Integer, PostingsList>();
            try(Reader reader = new FileReader(filename)){
                block_tmp = gson.fromJson(reader, 
                new TypeToken<Map<Integer, PostingsList>>(){}.getType());
            } catch (IOException e) {
                e.printStackTrace();
            }
            p = mergePostingsList(p, block_tmp.get(termID(token)));
        }
        
        return p;
        /*String filename = "postings/b"+listOfBlocks.get(0)+".json";
        Map<Integer, PostingsList> block_tmp = new HashMap<Integer, PostingsList>();
        try(Reader reader = new FileReader(filename)){
            block_tmp = gson.fromJson(reader, 
            new TypeToken<Map<Integer, PostingsList>>(){}.getType());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return block_tmp.get(termID(token));*/
    }

    public PostingsList mergePostingsList(PostingsList p1, PostingsList p2){
        List<PostingsEntry> l = new LinkedList<PostingsEntry>();
        if (p1.isEmpty() && !p2.isEmpty()){
            return p2;
        } 
        else if (p2.isEmpty() && !p1.isEmpty()){
            return p1;
        }
        else if (p2.isEmpty() && p1.isEmpty()){
            return null;
        }
        else{
            l.addAll(p1.getList());
            l.addAll(p2.getList());
            //System.err.println("merging!");
            return (new PostingsList(l));
        }
    }
    

    /* Add or update postingslist*/
    public void put(String token, int docID, int offset){
        
        if(this.block.containsKey(termID(token))){
            // Add postings to the block
            this.block.get(termID(token)).insert(docID, offset);
            }
        else{
            if (this.blocksID.containsKey(termID(token)))
                this.blocksID.get(termID(token)).add(this.blockID);
            else{
                LinkedList<Integer> l = new LinkedList<Integer>();
                l.add(this.blockID);
                this.blocksID.put(termID(token), l);
            }

            // Add termID(token) as key and postingslist as value to block postingslist
            PostingsList postingslist = new PostingsList();
            postingslist.insert(docID, offset);
            this.block.put(termID(token), postingslist);
        }

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


    /* Write current block list */
    public void writeBlock(){
        Gson gson = new Gson();
        //System.err.println("------------------------");
        String filename = "postings/b"+ Integer.toString(this.blockID)+".json";
        String json = gson.toJson(this.block);
        //System.out.println(json);
        try(FileWriter writer = new FileWriter(filename)){
            gson.toJson(this.block, writer);
            System.err.println("\nBlock stored");
        }catch (IOException e) {
            e.printStackTrace();
        }
        //System.err.println("------------------------");
        this.blockID++;
        this.block = new HashMap<Integer, PostingsList>();
    }

    
    public void mergeBlocks(){
        // Merge all json files
    }
    /* Stores the postingslist p in the file named filename */


    /* Returns the keys from the index, i.e. the terms*/
    public Set<String> keySet(){
    	return this.termsID.keySet();
    }
}
	

			   
