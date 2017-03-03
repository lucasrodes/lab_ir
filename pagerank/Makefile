all:
	make cp
	make run

cp: PageRank.java
	javac -Xlint:unchecked PageRank.java

run: PageRank.java linksDavis.txt
	java -Xmx4g PageRank linksDavis.txt