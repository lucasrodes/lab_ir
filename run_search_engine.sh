#!/bin/sh
java -cp classes:pdfbox:lib/gson-2.8.0.jar -Xmx4g ir.SearchGUI -d daviswiki -l ir17.gif -p patterns.txt
