package ir;

import java.util.*;
import java.io.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

class MyMap {
         
	    // Maps tokens with corresponding termIDs
    	private Map<String,Integer> termsID = new HashMap<String,Integer>();
    	// Tokens mapping to PostingsList
		private HashMap<String, PostingsList> indexInMemory 
			= new HashMap<String, PostingsList>();
        // Used when indexing to map tokens to termIDs
        private int termID;
        private int cacheLimit;


        /** Constructor */
        public MyMap(){
            this.termID = 0;
            this.cacheLimit = 2;
        }


        public PostingsList getFromMemory(String token){
            return this.indexInMemory.get(token);
        }


        public PostingsList getFromDisk(String token){
            // Read JSON file associated to token and return it as a postingslist
            String filename = "postings/t"+termsID.get(token)+".json";
            PostingsList post_tmp = new PostingsList();
            try(Reader reader = new FileReader(filename)){
                post_tmp = (new Gson()).fromJson(reader, PostingsList.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return post_tmp;
        }


        /** Put token, postingslist pair */
        public void put(String token, PostingsList post){
            this.termsID.put(token, termID(token));
            this.indexInMemory.put(token, post);
            System.err.println();
        }


        /** Returns the termID of token */
        public Integer getID(String token){
            return this.termsID.get(token);
        }


        public boolean isFull(){
            System.err.println("map size: "+this.indexInMemory.size());
            if (this.indexInMemory.size() == this.cacheLimit)
                return true;
            return false;
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


        /** Backs up the map of postings */
        public void backUp(){
            /*for (Map.Entry<Integer, PostingsList> entry : this.indexInMemory.entrySet()) {
                saveJSON("postings/t"+entry.getKey()+".json", entry.getValue());
                if (entry.getKey()%1000==0)
                    System.err.println("storing "+ entry.getKey());
            }*/
            System.err.println(this.termID);
            System.err.println("back up");
            //saveJSON("postings/termsID.json", this.termsID);
        }

        /** [NEW] 
         *  Saves the token-termIDs map and list with document names
         */
        public void saveAll(){
            // Store postings 
            for (Map.Entry<String, PostingsList> entry : this.indexInMemory.entrySet()) {
                saveJSON("postings/t"+termsID.get(entry.getKey())+".json", entry.getValue());
                if (termsID.get(entry.getKey())%1000==0)
                    System.err.println("storing "+ entry.getKey());
            }
            saveJSON("postings/termsID.json", this.termsID);
        } 


        /** Returns true if the token has already been seen */
        public boolean containsKey(String token){
            return this.termsID.containsKey(token);
        }


        /** Returns true if the key is in memory*/
        public boolean containsKeyInMemory(String token){
            return this.indexInMemory.containsKey(token);
        }


        /** Recovers the stored data */
        public void recover(){
            // Loads hashmap containing mapping between tokens and termIDs 
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

        public Set<String> keySet() {
            //return this.index.keySet().iterator();
            return this.termsID.keySet();
        }


        public String hash(String token){
            return Long.toString(System.currentTimeMillis());
        }    
    }