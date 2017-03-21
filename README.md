# lab_ir


## Prepare workspace

Create the following directory withing the repository in your local computer
```
mkdir postings
```

Download [davisWiki corpus](http://www.csc.kth.se/~jboye/teaching/ir/davisWiki.zip) and [pdfbox library](http://www.csc.kth.se/~jboye/teaching/ir/pdfbox.zip) and store them in the repository in your local computer as `davisWiki` and `pdfbox`. Note that the corpus `davisWiki` is very big, hence we do not provide it in this repo. You can edit the `run_search_engine.sh` script to change the collection of files you want to index.


## Execute program

Compile the java project as
```
sh compile_all
```

Run the search engine program

```
sh run_search_engine
```

Now you can search for a word. Note that when exiting, you can store the index so that the program does not need to index all the documents again (this is done by clicking on "File/Save and Quit"). If you want the program to re-index, then exit by clicking on "File/Quit".
