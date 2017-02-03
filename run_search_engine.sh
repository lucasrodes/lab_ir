#!/bin/sh
java -cp classes:pdfbox -Xmx1g ir.SearchGUI -d davisWiki -l ir17.gif -p patterns.txt
