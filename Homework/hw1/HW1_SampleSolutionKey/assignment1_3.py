import matplotlib.pyplot as plt
import pandas as pd
import numpy as np
import io
import sys
import re
import difflib
import datetime
import csv

#collect data
reader = pd.read_csv("GoogleProducts.csv", encoding = "ISO-8859-1", iterator = True, chunksize = 100)
sys.stdout = io.TextIOWrapper(sys.stdout.buffer,encoding='ISO-8859-1')
index = 0;
data = [[],[],[],[],[]]
    
for chunk in reader:
    id = chunk.get("id")
    title = chunk.get("name")
    description = chunk.get("description")
    price = chunk.get("price")
    manufacturer = chunk.get("manufacturer")
    for i in range(0, len(chunk), 1):  
        #parse company roughly
        comp = None
        res = None  
        res = re.match("([\w]+(?=:))",str(title.get(index)))     #match company name ends with :        
        if (res == None) : res = re.match("([\w'-]+(?=\s))",str(title.get(index)))     #match company name ends with space
        if (res != None) : comp = res.group(0)
        else: comp = str(title.get(index))       
        data[0].append(id.get(index))
        if (str(manufacturer.get(index)) != "nan") : comp = str(manufacturer.get(index)) # adjust comp by original data
        data[1].append(str(comp).lower())
        data[2].append(str(title.get(index)).lower())
        if ("gbp" in str(price.get(index))):
            v = 1.22 * float(str(price.get(index))[:len(price.get(index)) - 4])     
            data[3].append(v)#convert into dollar
            #print(v)
            
        else : data[3].append(float(price.get(index)))
        data[4].append(str(description.get(index)).lower())
        index += 1
        
reader = pd.read_csv("Amazon.csv", encoding = "ISO-8859-1", iterator = True, chunksize = 100)
index = 0;
data2 = [[],[],[],[],[]]
for chunk in reader:
    id = chunk.get("id")
    title = chunk.get("title")
    company = chunk.get("manufacturer")
    price = chunk.get("price")
    description = chunk.get("description")
    for i in range(0, len(chunk), 1):  
        data2[0].append(id.get(index))
        comp = ""
        res = re.match("([\w]+(?=\s))",str(company.get(index)))
        if (res == None): 
            comp = str(company.get(index))
        else: 
            comp = res.group(0)
        data2[1].append(str(comp).lower())
        data2[2].append(str(title.get(index)).lower())
        data2[3].append(price.get(index))
        data2[4].append(str(description.get(index)).lower())
        index += 1


f = open("result.csv", "w", encoding='ISO-8859-1')
writer = csv.writer(f, lineterminator='\n')
writer.writerow(("G_ID", "G_TITLE", "A_ID", "A_TITLE"))
t1 = datetime.datetime.now().time()
print(t1) 
for i in range(0, len(data[0]), 1):
    max = 0
    maxJ = -1
    for j in range(0, len(data2[0]), 1):        
        #s_des = difflib.SequenceMatcher(None, data[4][i], data2[4][j]).ratio() too slow....
        s_title = difflib.SequenceMatcher(None, data[2][i], data2[2][j]).ratio()
        s_comp = difflib.SequenceMatcher(None, data[1][i], data2[1][j]).ratio()
        s_price = 0
        if (data2[3][j] != 0):
            m = data[3][i]
            if (data[3][i] < data2[3][j]): m = data2[3][j]
            s_price = 1 - abs(data[3][i] - data2[3][j]) / m
            s = (s_title * 2 + s_comp + s_price) / 4
        else: s = (s_title * 2 + s_comp) / 3   # eliminate missing data term
        if (s > max):
            max = s
            maxJ = j
    if (max > 0.6):
        writer.writerow((data[0][i],data[2][i],data2[0][maxJ],data2[2][maxJ]))
t2 = datetime.datetime.now().time()
f.close()
print(t2)