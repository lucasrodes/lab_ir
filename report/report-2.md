#LAB1 - Some notes

## Task 2.1: Ranked Retrieval
- The score of a document _d_ for a term _t_ is uniquely defined by the _wd(t)_ term, which was computed using the normalized _tf_ value of the queried word in the _d_ and the idf value of that term in the whole collection.

## Task 2.2. Ranked Multiword Retrieval
- For this task, I introduced the term _wq(t)_ to weight the different terms in the query. Assume we have terms _t1_ and _t2_ appearing in document _d_. Then, the score of _d_ is computed as: _wq(t1)*wd(t1) + wq(t2)*wd(t2)_
- This should be normalized, at the end, by the length of the document _d_, number of words.
- Another approach is to normalize it using the euclidean distance: _sqrt(sum_k{wd(tk)^2})_, for all terms _tk_ in _d_. I did not use the cosine similarity, seemed a world to me.


## Task 2.3: What is a good search result?
- Results can be seen in _LucasRodesGuirao.txt_.
- I implemented a python script, _plot_precrec.py_, which reads the above mentioned txt file and plots the precision-recall curve at 10, 20, 30, 40 and 50.


## Task 1.4: Phrase queries
- Results obtained match the ones in the tutorial
- In this case, we note that we obtain less amount of files after searching. This is due to the fact that all phrase query results will also be intersection results. However, all intersection results do not necessarily need to be phrase query results.


## Task 1.5: What is a good search result?
- I searched for the intersection query `graduate program mathematics` and assessed the relevance of the 22 resulting documents. My evaluation was as follows:

Query | File name                                                 	| Relevance |
------|:-----------------------------------------------------------:|-----------:
1     | Biological_Systems_Engineering.f 						  	| 1			|
1     | Candidate_Statements.f 										| 0			|
1     | Computer_Science.f 											| 2			|
1     | document_translated.f 										| 0			|
1     | ECE_Course_Reviews.f 										| 0			|
1     | Economics.f 												| 2			|
1     | Elaine_Kasimatis.f 											| 1			|
1     | Evelyn_Silvia.f 											| 1			|
1     | Events_Calendars.f 											| 0			|
1     | Fiber_and_Polymer_Science.f 								| 1			|
1     | Hydrology.f 												| 0			|
1     | Mathematics.f 												| 4			|
1     | MattHh.f 													| 2			|
1     | Private_Tutoring.f 											| 1			|
1     | Quantitative_Biology_and_Bioinformatics.f 					| 1			|
1     | Statistics.f 												| 3			|
1     | Student_Organizations.f 									| 0			|
1     | UC_Davis_English_Department.f 								| 0			|
1     | UCD_Honors_and_Prizes.f 									| 0			|
1     | University_Departments.f 									| 0			|
1     | What_I_Wish_I_Knew...Before_Coming_to_UC_Davis_Entomology.f | 0			|
1     | Wildlife%2C_Fish%2C_and_Conservation_Biology.f 				| 1 		|


precision = |relevant union returned|/|returned| = 12/22 ~= 54%
recall = |relevant union returned|/|relevant| = 12/100 ~= 12%

## Task 1.6: What is a good query?
We are asked to create a query to obtain results resolving: **Info about the education in Mathematics on a graduate level at UC Davis**. In this regard, we are supposed to use *intersection query*. I have used:

**graduate mathematics course**

- university mathematics: 44 matches
- university mathematics program: P = 7/19

2011_Archive.f - 0
Candidate_Statements.f - 0
Children%27s_Summer_Programs.f - 0
Computer_Science.f - 2
document_translated.f - 0
Evelyn_Silvia.f - 1
Majors.f - 1
MattHh.f - 2
Patrick_Sheehan.f - 0
Private_Tutoring.f - 1
Seminars_Project.f - 0
Statistics.f - 3
Student_Organizations.f - 0
Town_History.f - 0
UC_Davis_English_Department.f - 0
UCD_Honors_and_Prizes.f - 0
University_Departments.f - 1
What_I_Wish_I_Knew...Before_Coming_to_UC_Davis_Entomology.f - 0
Wiki_History.f - 0

university mathematics program graduate: P = 6/12

Candidate_Statements.f - 0
Computer_Science.f - 2
document_translated.f - 0
Evelyn_Silvia.f - 1
MattHh.f - 2
Private_Tutoring.f - 1
Statistics.f - 3
Student_Organizations.f - 0
UC_Davis_English_Department.f - 0
UCD_Honors_and_Prizes.f - 0
University_Departments.f - 1
What_I_Wish_I_Knew...Before_Coming_to_UC_Davis_Entomology.f - 0

- graduate program mathematics: 22 matches
P = 12/22

- mathematics course university: P = 5/14
2011_Archive.f - 0
archive.f - 0
Computer_Science.f - 2
document_translated.f - 0
EB_Roessler.f - 1
GregKuperberg.f - 0
MattHh.f - 2
Private_Tutoring.f - 1
Shadiness_Factor.f - 0
Statistics.f - 3
Teaching_Assistants.f
UC_Davis_English_Department.f - 0
UCD_Honors_and_Prizes.f - 0
What_I_Wish_I_Knew...Before_Coming_to_UC_Davis_Entomology.f - 0

- mathematics uc davis graduate program: 10/17 ~= 58%
Candidate_Statements.f - 0
Computer_Science.f - 2
ECE_Course_Reviews.f - 0
Economics.f - 2
Elaine_Kasimatis.f -1
Evelyn_Silvia.f - 1
Events_Calendars.f - 0
Hydrology.f - 0
Mathematics.f - 4
MattHh.f - 2
Private_Tutoring.f - 1
Statistics.f - 3
Student_Organizations.f - 0
UC_Davis_English_Department.f - 0
UCD_Honors_and_Prizes.f - 0
University_Departments.f - 1
What_I_Wish_I_Knew...Before_Coming_to_UC_Davis_Entomology.f - 0
Wildlife%2C_Fish%2C_and_Conservation_Biology.f - 1

- mathematics uc davis graduate program university: P = 7/11 ~= 63%

Candidate_Statements.f - 0
Computer_Science.f - 2
Evelyn_Silvia.f - 1
MattHh.f - 2
Private_Tutoring.f - 1
Statistics.f - 3
Student_Organizations.f - 0
UC_Davis_English_Department.f - 0
UCD_Honors_and_Prizes.f - 0
University_Departments.f - 1
What_I_Wish_I_Knew...Before_Coming_to_UC_Davis_Entomology.f - 1


We note that the longer is the query the less results we get. However, we are able to get more precise and specific results. Thus, if we wrote the whole information need description we run the risk of no retrieving anything at all.

## Task 1.7

* `ir/`: Prior to task 1.7
* `ir1/`: Stores file per postingslist
* `ir2/`: Trying to optimize 