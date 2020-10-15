import requests
import json
import re
import string
def removePunc(string):
    pattern = re.compile(r'[^:;,.\/_=+)(*&^@#!?]*')
    return pattern.match(string).group(0).strip()
#5f7cc4fb6794ca6d695818b6 <--- Board id
US = requests.get("https://api.trello.com/1/lists/5f7cc4fb6794ca6d695818b7/cards?key=acad9e4b7ea218ba1667b7ad1e050ae8&token=d6090197a61f0f1af8a5790742bfed1137552e1b0f8379c65c2fb5cfe96e0b9f")
req = requests.get("https://api.trello.com/1/lists/5f7ccbc72fa701324f9a88d8/cards?key=acad9e4b7ea218ba1667b7ad1e050ae8&token=d6090197a61f0f1af8a5790742bfed1137552e1b0f8379c65c2fb5cfe96e0b9f")

dic = req.json()
stor = US.json()

content = {"Book":[],"User profile":[],"Searching":[],"Requesting":[],"Accepting":[],"Borrowing":[],"Returning":[],"Photographs":[],"Location":[]}
if len(dic) != len (stor):
    print("number of user stories do not match")
else:
    for index in range(len(dic)):
        item = dic[index]
        story = stor[index]
        stri = item['name']
        regex = re.search(r"(US.+\d.+\d.+\d)(.*)size:(.*)risk:(.*)",stri,re.IGNORECASE)
        storyRegex = re.search(r"(US.+\d.+\d.+\d)(.*)",story['name'],re.IGNORECASE)
        label = item['labels'][0]["name"]
        if regex == None:
            print("User Story was not matched",stri)
        if storyRegex == None:
            print("User story description was not matched",storyRegex)
        if regex:
            #makes sure the regex found matches
            storyId = regex.group(1)
            risk = regex.group(4)
            storyPoint = regex.group(3)
            desc = regex.group(2)
            removePunc(risk)
            content[label].append((storyId.strip(),desc.strip(),removePunc(storyPoint.strip()),removePunc(risk.strip()),storyRegex.group(2)))
    with open("Backlog.md","w") as mdFile:
        for item in content:
            mdFile.write(f"- # {item}\n")
            for element in content[item]:
                mdFile.write(f"  - ## {element[0]}\n     + **Story**: {element[4]}\n     + **Rationale**: {element[1]}\n     + **StoryPoint**: {element[2]}\n     + **Risk**: {element[3]} \n")
