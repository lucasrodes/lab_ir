#!/bin/sh
java -cp classes:pdfbox:lib/gson-2.8.0.jar ir.TokenTest -f testfile.txt -p patterns.txt -rp -cf > tokenized_result.txt
