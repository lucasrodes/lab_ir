package ir;

import java.util.*;
import java.io.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.rmi.server.UID;

class MyMap {
         
    	// Tokens mapping to PostingsList
		private TreeMap<String, PostingsList> index 
			= new TreeMap<String, PostingsList>();
        private int cacheLimit;
        private int count = 0;

        /** Constructor */
        public MyMap(int cl){
            this.cacheLimit = cl;
        }


        /* Get the posting corresponding to a specific token */
        public PostingsList get(String token){
            // Read JSON file associated to token and return it as a postingslist
            String filename = "postings/t"+hash(token)+".json";
            PostingsList post = new PostingsList();
            try(Reader reader = new FileReader(filename)){
                post = (new Gson()).fromJson(reader, PostingsList.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return post;
        }


        /** Put token, postingslist pair */
        public void put(String token, int docID, int offset){
            
            if (this.index.containsKey(token)){
                System.err.println(token+" already in memory");
                this.index.get(token).insert(docID, offset);
            }
            else{
                System.err.println("new token: " +token+" stored");
                PostingsList postingslist = new PostingsList();
                postingslist.insert(docID, offset);
                this.index.put(token, postingslist);
            }

        }


        /** [NEW] 
         *  Saves the token-termIDs map and list with document names
         */
        public void saveToDisk(){
            // Store postings 
            for (Map.Entry<String, PostingsList> entry : this.index.entrySet()) {
                saveJSON("postings/t"+hash(entry.getKey())+".json", entry.getValue());
                count++;
                if (count%1000==0)
                    System.err.println("storing "+ count);
            }
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

        public boolean containsKey(String token){
            return this.index.containsKey(token);
        }


        public Set<String> keySet() {
            return this.index.keySet();
        }


        public static String hash(String token){
            return Integer.toString( token.hashCode() );
        }   

        public String toString(){
            String s = " ";
            for (Map.Entry<String, PostingsList> entry : this.index.entrySet()) {
                s += entry.getKey()+" - " + entry.getValue().toString() + "\n";
            }
            return s;//this.indexInMemory.toString();
        } 
    }