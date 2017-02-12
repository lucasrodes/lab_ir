/*  
 *   This file is part of the computer assignment for the
 *   Information Retrieval course at KTH.
 * 
 *   First version:  Johan Boye, 2010
 *   Second version: Johan Boye, 2012
 */  

package ir;

import java.io.Serializable;
import java.util.LinkedList;

public class PostingsEntry implements Comparable<PostingsEntry>, Serializable {
    
    public int docID;
    public LinkedList<Integer> positions = new LinkedList<Integer>();
    public double score;

    public PostingsEntry( int dI, double sc){
        this.docID = dI;
        this.score = sc;
    }
    public PostingsEntry( int dI, double sc, int pos){
        this.docID = dI;
        this.score = sc;
        this.positions.add(pos);
    }

    /**
     *  PostingsEntries are compared by their score (only relevant 
     *  in ranked retrieval).
     *
     *  The comparison is defined so that entries will be put in 
     *  descending order.
     */
    public int compareTo( PostingsEntry other ) {
	   return Double.compare( other.score, score );
    }

    public String toString(){
        return Integer.toString(this.docID) +
                " - " + positions.toString();
    }
    
    /* Add the position index pos to this postingentry */
    public void insertPosition(int pos){
        this.positions.add(pos);
    }

}

    
