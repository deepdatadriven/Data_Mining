import matplotlib.pyplot as plt
import pandas as pd
import numpy as np
import io
import sys
import re

#collect data
reader = pd.read_csv("GoogleProducts.csv", encoding = "ISO-8859-1", iterator = True, chunksize = 100)
sys.stdout = io.TextIOWrapper(sys.stdout.buffer,encoding='ISO-8859-1')
index = 0;
data = [[],[],[],[]]
# popular game companies' prefixes and full names
# popular game products and their companies
companies = {
    "blizzard" : "blizzard", 
    "aspyr" : "aspyr", 
    "egames" : "egames", 
    "got" : "got game entertainment", 
    "nintendo" : "nintendo", 
    "sony" : "sony", 
    "kids" : "kids power", 
    "eagle" : "eagle games", 
    "sega" : "sega", 
    "flyboys" : "flyboys", 
    "rock" : "rock star",
    "namco" : "namco",
    "konami" : "konami",
    "evergirl" : "evergirl",
    "vivendi-universal": "vivendi-universal",
    "xbox": "xbox",
    "freeverse" : "freeverse software",
    "enteractive" : "enteractive inc",  
    "warcraft" : "blizzard",
    "starcraft" : "blizzard",
    "wingnuts 2: raina's revenge" : "Freeverse", 
    "midway arcade treasures" : "midway",
    "activision" : "activision inc",
    "vivendi-universal" : "vivendi-universal games inc",
    "11273" : "encore", 
    "fifa" :  "EA Sports"
    }
    
# other keywords
keywords = [
    "warcraft",
    "starcraft",
    "nintendo",
    "xbox",
    "wingnuts 2: raina's revenge",
    "midway arcade treasures",
    "game",
    "gaming"
    ]
    
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
        found = False   
        res = re.match("([\w]+(?=:))",str(title.get(index)))     #match company name ends with :        
        if (res == None) : res = re.match("([\w'-]+(?=\s))",str(title.get(index)))     #match company name ends with space


        if (res != None) : comp = res.group(0)
        else: comp = str(title.get(index))
        if (companies.get(comp) != None): 
            comp = companies.get(comp)
            found = True
        else:
            for keyword in keywords:
                if ((keyword in str(title.get(index))) or (keyword in str(description.get(index)))):
                    found = True
                    if (companies.get(keyword) != None): comp = companies.get(keyword)
                    else : 
                        if (('microsoft' in str(title.get(index))) or ('microsoft' in str(description.get(index)))): comp = 'microsoft'
                    break
        # get a game related item
        if (found == True):         
            data[0].append(re.findall("(?<=/)\w+", id.get(index))[-1])
            if ('\'' in comp or len(comp) <= 2): comp = 'unknown'
            data[1].append(comp)
            data[2].append(title.get(index))
            data[3].append(price.get(index))
        index += 1
#print(index)
count = {}
for i in range(0, len(data[1]), 1):
     if (count.get(data[1][i]) == None): count[data[1][i]] = 1
     else : count[data[1][i]] = count.get(data[1][i]) + 1
#    print(str(data[0][i]) + "  " + str(data[1][i]) + "  " + str(data[2][i])[:20] + "...  " + str(data[3][i]))

plt.figure('histogram', figsize = (24,8))
X = np.arange(len(count))
plt.bar(X, count.values())
plt.xticks(X, count.keys(), rotation='vertical')
plt.xlabel('Manufacturer')
plt.ylabel('Num_Of_Products')
plt.margins(0.2)
plt.show()
