#!/bin/sh
if ! [ -d classes ];
then
   mkdir classes
fi
javac -cp .:pdfbox:web-inf/lib/gson-2.8.0.jar -Xlint:unchecked  -d classes ir/Tokenizer.java ir/TokenTest.java ir/Index.java ir/Indexer.java ir/HashedIndex.java ir/Query.java ir/PostingsList.java ir/PostingsEntry.java ir/SearchGUI.java
