# Report lab Assignment 3

## Task 3.1: Relevance Feedback

**What happens to the two documents that you selected?**

The selected documents will eventually increase their positioning in the retrieved list.

**What are the characteristics of the other documents in the new top ten list - what are they about? Are there any new ones that were not among the top ten before?**

The new documents are supposed to be more similar to the structure of the documents we marked as relevant. However, note that we do not penalize non-relevan documents. Hence this is still not 100% accurate.

**How is the relevance feedback process affected by alpha and beta?**

The higher is the term *alpha* the more important will be the initial user query, i.e. the initially searched words. On the other hand, the higher is the term *beta* the more relevant is the Rocchio algorithm update (we do not trust much the user 'information need' guess).

**Why  is  the  search  after  feedback  slower?  Why  is  the  number  of returned documents larger?**

After the Rocchio algorithm update of the query vector _**q**_, we have that _**q**_ might in fact be larger. Thus, we have now to retrieve the union of more terms (as many as dimensions has _**q**_).

Note that, in general, the document tf-idf vectors, i.e. _**dj**_, usually contain more words than the user query. Thus, the size of the centroid of relevant documents will be larger than the initial query vector _**q**_, which leads to a search of a wider union of terms.

## Task 3.2: Designing an evaluation

test queries: *graduate program mathematics*

## Task 3.3: Speeding Up the Search Engine
For speeding up the search, I have decided to follow the approach 1). This focuses on only considering terms with idf above a certain threshold. So one question arises: How do we set the threshold? Find below some **key points**

- Since our collection contains approximately 17k elements, we have that the idf will be, at most, ln(17000) = 9.7.
- If a word appears in all documents, its idf is 0.

With this in mind, we can conclude that: _0< idf-threshold<9.7_  (approximately). I have worked with _idf-threshold=4_.

**Results**

I performed a test comparing the time needed to retrieve the result depending on the idf-threshold
value. I did two experiments.


* Experiment 1: Relevant Documents = "Zombie_Walk.f"

Threshold | Number of retrieved files | Time needed (ms) |
----------|:-------------------------:|------------------:
0         | 16030                     | [2103, 2957]     |
1         | 14485                     | [1667, 2076]     |
2         | 12375                     | [1133, 1354]     |
3         | 7473                      | [536, 654]       |
4         | 3371                      | [182, 378]       |
5         | 1286                      | [66, 121]        |
6         | 373                       | [26, 34]         |

* Experiment 2: Relevant Documents = {"Zombie_Walk.f", "Kearney_Hall.f"}

Threshold | Number of retrieved files | Time needed (ms) |
----------|:-------------------------:|------------------:
0         | 16160                     | [2787, 3249]     |
1         | 15106                     | [2380, 2657]     |
3         | 9104                      | [871, 1211]      |
4         | 6313                      | [503, 726]       |
5         | 1432                      | [72, 82]         |
6         | 373                       | [31, 37]         |

The higher is the idf-threshold, the more terms we ignore. Thus we retrieve less documents and the search is faster. At the limit, too high threshold will lead to zero retrieved documents. Looking at the results and the performance, I decided to set idf-threshold = 4.