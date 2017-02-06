package ir;

import java.util.*;
import java.io.*;

static class myMap {
         
	    // Maps tokens with corresponding termIDs
    	private Map<String,Integer> termsID = new HashMap<String,Integer>();
    	// Tokens mapping to PostingsList
		private HashMap<String, PostingsList> indexInMemory 
			= new HashMap<String, PostingsList>();
          
         public myMap(K key, V value, Entry<K,V> next){
             this.key = key;
             this.value = value;
             this.next = next;
         }

        public get(K key){

        }

        public boolean containsKeyInMemory(){

        }

        public boolean containsKey(){

        }

        public PostingsList get(String key){

        }

        public PostingsList getFromDisk(String key){

        }

        public PostingsList getFromMemory(String key){
        	
        }
    }