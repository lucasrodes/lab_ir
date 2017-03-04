#LAB1 - Some notes

## Task 2.1: Ranked Retrieval
- The score of a document _d_ for a term _t_ is uniquely defined by the _wd(t)_ term, which was computed using the normalized _tf_ value of the queried word in the _d_ and the idf value of that term in the whole collection.

## Task 2.2. Ranked Multiword Retrieval
- For this task, I introduced the term _wq(t)_ to weight the different terms in the query. Assume we have terms _t1_ and _t2_ appearing in document _d_. Then, the score of _d_ is computed as: _wq(t1)*wd(t1) + wq(t2)*wd(t2)_
- This should be normalized, at the end, by the length of the document _d_, number of words. 
- Another approach is to normalize it using the euclidean distance: _sqrt(sum_k{wd(tk)^2})_, for all terms _tk_ in _d_. I did not use the cosine similarity, seemed a world to me.
- Furthermore, log(tf+1) could be interesting to use. If a document contains a word _m_ times and another document contains that word _2m_ times, the later is not necessarily two times as much relevant as the first one!


## Task 2.3: What is a good search result?
- Results can be seen in _LucasRodesGuirao.txt_, _LucasRodesGuirao-as1.txt_ and  _LucasRodesGuirao-as2.txt_.
- I implemented a python script, _plot_precrec.py_, which reads the above mentioned txt file and plots the precision-recall curve at 10, 20, 30, 40 and 50.
- Image _precrecplot.png_ shows the precision-recall curve. We see that:
	* **Recall = |relevant intersection returned|/|relevant|**. We observe that |relevant| remains constant, while the term |relevant intersection returned| can only increase as the number of considered documents increases.
	* **Precision = |relevant intersection returned|/|returned|**. We see that this can indeed decrease. For instance, As we increase the number of considered documents it is likely that less 'relevant' documents appear.
- Below you can find the corresponding picture

![Precision-Recall curve](https://github.com/lucasrodes/lab_ir/blob/master/report/precrecplot.png)


## Task 2.4: Computing PageRank with Power Iteration
- The results match the given ones, provided that we use `EPSILON = 0.00001`.
- The highest ranked document is Davis, which is the homepage and makes sense since various documents might link to it. The order of ranked documents is: 

_Davis, Photo_Requests, UC_Davis, Seed/Definition, departed_businesses_

which seem to be general pages of the wiki. On the contrary, as we go down we find more specific documents such as _Picnic_Day_ or _Music_Scene_. Nonetheless we are only considering files within the top 30 rank, and hence all files are still generic.

For detailed results report, check file _results_pagerank.txt_
----
## Task 1.4: Phrase queries
- Results obtained match the ones in the tutorial
- In this case, we note that we obtain less amount of files after searching. This is due to the fact that all phrase query results will also be intersection results. However, all intersection results do not necessarily need to be phrase query results.


## Task 1.5: What is a good search result?
- I searched for the intersection query `graduate program mathematics` and assessed the relevance of the 22 resulting documents. My evaluation was as follows:

ID    | File name           | relevance           |
------|:-------------------:|---------------------:
121   |Davis      			| 0.00798059840550247
21    |Photo_Requests	  	| 0.007730984456317006
245   |UC_Davis 			| 0.007359630703669526
1531  |Seed/Definition		| 0.005093884876376878
1367  |departed_businesses 	| 0.0028365843639788293
31    |Sacramento			| 0.0025368982636868537
80    |ASUCD 				| 0.0022163829698854196 
1040  |Woodland 			| 0.002182422761837147
254   |campus 				| 0.00202342614246361
452   |City_Council 		| 0.0019453622194147726
157   |East_Davis			| 0.0016263026061710758
392   |Yolo_County 			| 0.0016194797697939396
169   |South_Davis 			| 0.0016097789983853183
100   |West_Davis 			| 0.0015630307240319225
561   |City_of_Davis 		| 0.0014601559872791186
3870  |Cul-de-sacs	 		| 0.0014439442930812727
997   |ASUCD_Senate 		| 0.001354354910116531
884   |Interstate_80 		| 0.0012777054622262459
202   |The_California_Aggie	| 0.0012661041494637345
8     |2007 				| 0.0012574555890029367
72    |Campus 				| 0.0012304828540332297
145   |North_Davis 			| 0.0011900955970500443
27    |Arboretum 			| 0.0010921363205047983
645	  |Memorial_Union 		| 0.0010831187908053872
490   |Davis_Enterprise 	| 0.0010626444775174563
2883  |Dentists 			| 0.0010500858618729793
81    |KDVS 				| 0.001026432324632966
942   |2006 				| 0.0010101214289792184
125   |Music_Scene 			| 0.0009522208532489646
247   |Picnic_Day 			| 0.000402656092627628



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