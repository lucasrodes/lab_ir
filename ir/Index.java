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

public interface Index {

    /* Index types */
    public static final int PERSISTENT_HASHED_INDEX = 1;
    public static final int HASHED_INDEX = 0;

    /* Query types */
    public static final int INTERSECTION_QUERY = 0;
    public static final int PHRASE_QUERY = 1;
    public static final int RANKED_QUERY = 2;

    /* Ranking types */
    public static final int TF_IDF = 0;
    public static final int PAGERANK = 1;
    public static final int COMBINATION = 2;

    /* Structure types */
    public static final int UNIGRAM = 0;
    public static final int BIGRAM = 1;
    public static final int SUBPHRASE = 2;


    //  INDEX/SEARCH
    public void insert( String token, int docID, int offset );
    public PostingsList getPostings( String token );
    // MEMORY/DISK
    public void load();
    public void saveAll();
    public void saveJSON(String fileName, Object o);

    //public Iterator<String> getDictionaryInMemory();
    // SEARCH
    public PostingsList search( Query query, int queryType, int rankingType, int structureType );
    public void cleanup();


    public int getDocLength(int id);
    public void putDocLength(int id, int offset);
    public int getNumberDocs();
    public void putdDocID( int id, String filename );
    public String getDocName ( int id );
    public void setParameters(HashedIndex i);
    // Attributes //

    // Map mapping a docID to the set of terms appearing in it. In particular it mapps to a
    // map which maps term to number of appearences
    public Map<Integer, HashMap<String, Integer>> tfMap
            = new TreeMap<Integer, HashMap<String, Integer>>();

    public Map<String, Integer> idfMap = new HashMap<String, Integer>();
}

