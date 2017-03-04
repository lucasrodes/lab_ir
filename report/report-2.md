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

The table below shows the results in detail.

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


## Task 2.5: Monte-Carlo PageRank Approximation
For this exercise I implemented all the different approaches of the MC approximations. Please find some remarks below.

- As we increase the number of walks or iterations the MC methods become very slow. In these cases, the Power Iteration algorithm is way quicker and provides good results. I can imagine that as the corpus of files increases, PI becomes unfeasible.
- MC1 and MC5 are faster. But it all depends on the parameters that we use.
- The performance measures were a bit confusing, since it **does not make any sense** to compute the Euclidean distance between the scores of the 30 first ranked documents. Note that the IDs of the documents might vary from method to method.
- In this regard, I obtained the Euclidean distance of the scores of the 30 first ranked documents using the PI method. In the other methods these documents do not necessarily appear in the first 30 positions.

### MC1

To evaluate the performance, we increase the parameter _N_ which denotes the number of walks.

N   	| Error on 30 first		| Error on 30 last    |
--------|:---------------------:|---------------------:
10   	|0.01637586651516146  	| 1.1451215838402431E-4
100   	|0.02602037858119098    | 1.1451215838402431E-4
1000    |0.0059717284311837805 	| 9.855450807788597E-4
10000   |0.002835009264502715 	| 2.0544116026881217E-4
100000  |6.406579204139922E-4	| 8.038007264840292E-5
1000000	|3.152729167836288E-4	| 2.5941397012521065E-5

We observe that as _N_ increases, the error decreases.

### MC2

In this example we tune the parameter _m_, which is the number of iterations. Remember that the total number of runs is _N = m*n_, where _n_ is the number of files.

m   	| Error on 30 first		| Error on 30 last    |
--------|:---------------------:|---------------------:
1   	|0.0017790310787830315 	| 1.72782833955441E-4
5   	|0.0011084699084054305  | 8.14134440610171E-5
10   	|6.599837013140591E-4	| 5.272728767603463E-5
50	    |1.6860034714334064E-4	| 1.5424113586773976E-5
100		|1.5585944921191845E-4	| 1.774131459723324E-5
500		|6.589186369720324E-5	| 6.317564331370763E-6


### MC3

Same tendence as in MC2.

m   	| Error on 30 first		| Error on 30 last	  |
--------|:---------------------:|---------------------:
1   	|9.346771468619439E-4 	| 6.701625483534689E-5
5   	|2.3876435194408272E-4  | 2.564015596145578E-5
10   	|2.5543256574445653E-4	| 1.3486433465378679E-5
50	    |1.1239485814957683E-4	| 1.1481498239241906E-5
100		|9.415930330381204E-5	| 4.208586714965573E-6
500		|3.675885228523189E-5	| 2.2361562489291396E-6

We observe that it converges really fast!! We obtain a really accurate result for _m = 1_.

### MC4

This method is very similar to MC3 but much faster since once we end up in a file with
no outlinks we break the loop!

m   	| Error on 30 first		| Error on 30 last	  |
--------|:---------------------:|---------------------:
1   	|8.792356629461152E-4 	| 4.080368276788959E-5
5   	|5.640427684585316E-4   | 2.296692039185706E-5
10   	|5.277122533848887E-4	| 1.4674031403701697E-5
50	    |1.869811368126223E-4	| 6.499375731705036E-6
100		|1.1261676823645375E-4	| 4.1771109292421374E-6
500		|4.9217048807603616E-5	| 1.6812538608612396E-6


### MC5

Similar to MC1 but faster, since we break when we are in a file without outlinks (like MC4)

N   	| Error on 30 first		| Error on 30 last    |
--------|:---------------------:|---------------------:
10   	|0.0585655017678655  	| 1.1451215838402431E-4
100   	|0.01692132778560167    | 1.1451215838402431E-4
1000    |0.006568214496783953 	| 0.001013033964001212
10000   |0.0021649894187068894 	| 1.8831526581657385E-4
100000  |6.381213340031603E-4	| 6.732915407930334E-5
1000000	|2.888929090829961E-4	| 1.6805618287639385E-5
10000000|7.143526934526465E-5	| 5.877840449205131E-6

## Task 2.6: Combine tf-idf and PageRank 

