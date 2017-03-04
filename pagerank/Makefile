all:
	make cp
	make run

cp: PageRank.java
	javac -cp .:gson-2.8.0.jar -Xlint:unchecked PageRank.java 

run: PageRank.java linksDavis.txt
	java -cp .:gson-2.8.0.jar -Xmx4g PageRank linksDavis.txt