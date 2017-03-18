# Report lab Assignment 3

## Task 3.1: Relevance Feedback

- *What happens to the two documents that you selected?*
The selected documents will eventually increase their positioning in the retrieved list.

- *What are the characteristics of the other documents in the new top ten list - what are they about? Are there any new ones that were not among the top ten before?*
The new documents are supposed to be more similar to the structure of the documents we marked as relevant. However, note that we do not penalize non-relevan documents. Hence this is still not 100% accurate.

- *How is the relevance feedback process affected by alpha and beta? *
The higher is the term *alpha* the more important will be the initial user query, i.e. the initially searched words. On the other hand, the higher is the term *beta* the more relevant is the Rocchio algorithm update (we do not trust much the user 'information need' guess).

- *Why  is  the  search  after  feedback  slower?  Why  is  the  number  of returned documents larger?*

After the Rocchio algorithm update of the query vector _**q**_, we have that _**q**_ might in fact be larger. Thus, we have now to retrieve the union of more terms (as many as dimensions has _**q**_).

Note that, in general, the document tf-idf vectors, i.e. _**dj**_, usually contain more words than the user query. Thus, the size of the centroid of relevant documents will be larger than the initial query vector _**q**_, which leads to a search of a wider union of terms.

## Task 3.2: Designing an evaluation

test queries: *graduate program mathematics*

## Task 3.3: Speeding Up the Search Engine

