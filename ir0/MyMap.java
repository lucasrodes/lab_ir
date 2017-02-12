package ir;

import java.util.*;
import java.io.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.rmi.server.UID;

class MyMap {
         
    	// Tokens mapping to PostingsList
		private TreeMap<String, PostingsList> indexInMemory 
			= new TreeMap<String, PostingsList>();
        private int cacheLimit;
        private static int priority = 0;
        private int count = 0;


        /** Constructor */
        public MyMap(int cl){
            this.cacheLimit = cl;
        }


        public PostingsList getFromMemory(String token){
            PostingsList p = this.indexInMemory.remove(token);
            this.indexInMemory.put(token, p);
            return this.indexInMemory.get(token);
        }


        public PostingsList getFromDisk(String token){
            // Read JSON file associated to token and return it as a postingslist
            //System.err.println("gettin from disk");
            String filename = "postings/t"+hash(token)+".json";
            PostingsList p = new PostingsList();
            try(Reader reader = new FileReader(filename)){
                p = (new Gson()).fromJson(reader, PostingsList.class);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Remove least recent accessed entry
            if (this.isFull()){
                Map.Entry<String, PostingsList> entry = 
                    this.indexInMemory.pollFirstEntry();
                //System.err.println("---- FULL MEMORY! Moved <"+entry.getKey()+"> from Memory to Disk");
                this.saveJSON("postings/t"+hash(entry.getKey())+".json", entry.getValue());
            }
            //System.err.println("---- Moved <"+token+"> from Disk to Memory");
            this.indexInMemory.put(token, p);
            return this.indexInMemory.get(token);
        }


        public PostingsList get(String token){
            System.err.println(hash(token));
            PostingsList p = new PostingsList();
            if (this.containsKeyInMemory(token)){
                System.err.println("mymap-mem: "+token);
                // Update its corresponding postingslist with new occurence
                p = this.getFromMemory(token);
            }
            else if (this.containsKeyInDisk(token)){
                System.err.println("mymap-disk: "+token);
                p = this.getFromDisk(token);
            }
            return p;
        }


        /** Put token, postingslist pair */
        public void put(String token, int docID, int offset){
            // Check if token has already been seen
            //System.err.println("Searching for <"+ token+">");
            System.err.println("--"+indexInMemory.size());
            if (this.containsKeyInMemory(token)){
                //System.err.println("-- Found in memory, updating its value!");
                // Update its corresponding postingslist with new occurence
                PostingsList p = this.indexInMemory.remove(token);
                p.insert(docID, offset);
                this.indexInMemory.put(token, p);
            }
            else if (this.containsKeyInDisk(token)){
                System.err.println("-- Found it in Disk");
                this.getFromDisk(token).insert(docID, offset);
            }
            else{
                //System.err.println("-- Did not find it");
                if (this.isFull()){
                    // Remove least recent accessed entry
                    Map.Entry<String, PostingsList> entry = 
                        this.indexInMemory.pollLastEntry();
                    //System.err.println("---- FULL MEMORY! Moved <"+entry.getKey()+"> from Memory to Disk");
                    this.saveJSON("postings/t"+hash(entry.getKey())+".json", entry.getValue());                  
                }
                // Create new postingslist for the token and store it in 
                // indexInMemory.
                //System.err.println("---- Stored <"+token+"> to Memory");
                PostingsList postingslist = new PostingsList();
                postingslist.insert(docID, offset);
                this.indexInMemory.put(token, postingslist);
                priority++;                
            }
        }


        public boolean isFull(){
            //System.err.println("map size: "+this.indexInMemory.size());
            if (this.indexInMemory.size() == this.cacheLimit)
                return true;
            return false;
        }


        public void removeLeastRecentAccessed(){
            // Remove least accessed
        }


        /** [NEW] 
         *  Saves the token-termIDs map and list with document names
         */
        public void saveToDisk(){
            // Store postings 
            for (Map.Entry<String, PostingsList> entry : this.indexInMemory.entrySet()) {
                saveJSON("postings/t"+hash(entry.getKey())+".json", entry.getValue());
                count++;
                if (count%1000==0)
                    System.err.println("storing "+ count);
            }
        } 


        /** Returns true if the token has already been seen */
        public boolean containsKeyInDisk(String token){
            //System.err.println("Checking if " +"postings/t"+hash(token)+".json"+ " exists in disk... ");
            File f = new File("postings/t"+hash(token)+".json");
            if((f.exists() && !f.isDirectory())|| this.indexInMemory.containsKey(token)) { 
                
                //System.err.println("...and it does!");
                return true;
            }
            //System.err.println("...and it doesnt");
            return false;
        }


        /** Returns true if the key is in memory*/
        public boolean containsKeyInMemory(String token){
            //System.err.println("Checking memory... ");
            return this.indexInMemory.containsKey(token);
        }

        public boolean containsKey(String token){
            return (containsKeyInMemory(token)||containsKeyInDisk(token));
        }

        /** [NEW]
         * Saves object o as a JSON file called fileName 
         */
        public void saveJSON(String fileName, Object o){
            try(FileWriter writer = new FileWriter(fileName)){
                (new Gson()).toJson(o, writer);
                //System.err.println("\n Token stored");
            }catch (IOException e) {
                e.printStackTrace();
            }
        }


        /*public Set<String> keySet() {
            //return this.index.keySet().iterator();
            return this.indexInMemory.keySet();
        }*/


        public static String hash(String token){
            return Integer.toString( token.hashCode() );
        }   

        public String toString(){
            String s = " ";
            for (Map.Entry<String, PostingsList> entry : this.indexInMemory.entrySet()) {
                s += entry.getKey()+" - " + entry.getValue().toString() + "\n";
            }
            return s;//this.indexInMemory.toString();
        } 
    }