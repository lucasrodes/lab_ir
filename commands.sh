
sh run_tokenizer.sh 
diff testfile_tokenized_ok.txt tokenized_result.txt | grep "^>" | wc -l