# lab_ir


## Prepare Workspace


Create the following directory withing the repository in your local computer
```
mkdir postings
```

Download [davisWiki corpus](http://www.csc.kth.se/~jboye/teaching/ir/davisWiki.zip) and [pdfbox library](http://www.csc.kth.se/~jboye/teaching/ir/pdfbox.zip) and store them in the repository in your local computer as `davisWiki` and `pdfbox`. Note that the corpus `davisWiki` is very big, hence we provide a smaller corpus, `daviswiki2`. By default, we index this corpus, to change this edit the `run_search_engine.sh` script.


## Execute program

Compile the java project as
```
sh compile_all
```

Run the search engine program

```
sh run_search_engine
```

Now you can search for a word. Note that when exiting, you can store the index so that the program does not need to index all the documents again (this is done by clicking on "File/Save and Quit"). If you want the program to re-index, then exit by clicking on "File/Quit".


## Remarks

- The java project can be found in the directory `ir/`. Other directories are from other (prior) versions. In particular, `ir-ns` only uses memory cache to store the index. Moreover, `ir-ts` first obtains the index only using memory and finally stores it in the disk. Currently, we are trying to improve the method of `ir-ts`, by doing some back-ups of the index in the meantime. This is done in order to avoid having the whole index in memory which might be unfeasible for some corpus.