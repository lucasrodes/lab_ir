/*  
 *   This file is part of the computer assignment for the
 *   Information Retrieval course at KTH.
 * 
 *   First version:  Johan Boye, 2010
 *   Second version: Johan Boye, 2012
 */  

package ir;

import java.util.*;
import java.io.Serializable;

/**
 *   A list of postings for a given word.
 */
public class PostingsList implements Serializable, Cloneable {
    
    /** The postings list as a linked list. */
    private LinkedList<PostingsEntry> list = new LinkedList<PostingsEntry>();

    public PostingsList(){}


    /**  Number of postings in this list  */
    public int size() {
	   return list.size();
    }


    /** Checks if the list of postings is empty */
    public boolean isEmpty(){
        if (size()==0) {
            return true;
        }
        return false;
    }


    /**  i-th posting */
    public PostingsEntry get( int i ) {
	   return list.get( i );
    }


    /** Last docID in postingslist **/
    public PostingsEntry getLast(){
        return this.list.getLast();
    }



    /** [NEW]
     *  Add new posting Corresponding to docID 
     */
    public void insert(int docID){
        this.list.add(new PostingsEntry(docID, 0));
    }


    /** [NEW]
     *  Corresponding to docID and its corresponding positional index pos 
     */
    public void insert(int docID, int pos){
        /* Check if the list of postings is empty or there is no entry
           for docID in the list. Add new postingsentry with (docID, pos). */
        if (this.isEmpty()||this.getLast().docID != docID)
            this.list.add(new PostingsEntry(docID, 0, pos));
        else
            this.getLast().insertPosition(pos);
    }


    public Iterator<PostingsEntry> iterator(){
        return this.list.iterator();
    }


    public void sort(){
        Collections.sort(list);
    }
    
    /** [NEW]
     *  
     */
    public String toString(){

        String s = "";
        for(PostingsEntry p : this.list){
            s += p.toString() +",";
        }
        return s;
    }

    public void remove(){
        this.list.remove(0);
    }


    protected PostingsList clone(){
        try{
            final PostingsList p =  (PostingsList)super.clone();
            p.list = (LinkedList<PostingsEntry>) this.list.clone();
            return p;
        }
        catch (CloneNotSupportedException e) {}
        return null;
    }


}
	

			   
