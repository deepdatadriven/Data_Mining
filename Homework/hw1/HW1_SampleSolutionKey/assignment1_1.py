import matplotlib.pyplot as plt
import pandas as pd
import io
import sys

#collect data
reader = pd.read_csv("Amazon.csv", encoding = "ISO-8859-1", iterator = True, chunksize = 100)
sys.stdout = io.TextIOWrapper(sys.stdout.buffer,encoding='ISO-8859-1')
index = 0;
data = [[],[],[],[]]
for chunk in reader:
    id = chunk.get("id")
    title = chunk.get("title")
    company = chunk.get("manufacturer")
    price = chunk.get("price")
    for i in range(0, len(chunk), 1):  
        if (company.get(index) == "microsoft"):
            data[0].append(id.get(index))
            data[1].append(company.get(index))
            data[2].append(title.get(index))
            data[3].append(price.get(index))
        index += 1
#print(index)

#show a boxplot and print outliers
plt.figure('boxplot')
res = plt.boxplot(data[3], notch = 1, sym = 'rs', vert = 0, patch_artist = 0)
outliers = {}
for r in res.get('fliers'):
    for d in r.get_data()[0]:
        outliers[d] = d

#retrieve complete data
print("Outliers:")
for p in outliers:
    for i in range(0, len(data[3]), 1):
        if p == data[3][i]:
            print(str(data[0][i]) + "  " + str(data[1][i]) + "  " + str(data[2][i])[:30] + "...  " + str(data[3][i]))
plt.show()
