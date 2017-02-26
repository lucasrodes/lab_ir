import numpy as np
import matplotlib.pyplot as plt

def readFile(filename):
	result = np.array([])
	fhand = open(filename)
	for line in fhand:
		line = line.rstrip()
		v = line.split(" ")
		result = np.append(result, int(v[2]))
	return result

# precision = |relevant intersection returned|/|returned|
def precision(array):
	return float(np.sum(array != 0))/np.size(array)

# recall = |relevant intersection returned|/|relevant|
def recall(array):
	return float(np.sum(array != 0))/100


results = readFile("LucasRodesGuirao-as2.txt")

PRE = np.array([])
REC = np.array([])

for i in range(10,51,5):
	PRE = np.append(PRE, precision(results[:i]))
	REC = np.append(REC, recall(results[:i]))

print "Precision:", PRE
print "Recall:", REC

plt.plot(REC, PRE, 'r-')
plt.axis([0, 1, 0, 1])
plt.xlabel('Recall')
plt.ylabel('Precision')
plt.title("Recal-Precision curve")
plt.show()