#!/bin/sh
java -cp classes:pdfbox ir.TokenTest -f testfile.txt -p patterns.txt -rp -cf > tokenized_result.txt
