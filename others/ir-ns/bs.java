// Supose we have a query 
Query query;
// with two elements in it
terms = query.terms;

// How to search for their interesection given a list of postings per each term?
PostingsList p1 = getPostings(listTerms.get(0)); 
PostingsList p2 = getPostings(listTerms.get(1));
// We define a pointer for each term
int point1 = 0;
int point2 = 0; 