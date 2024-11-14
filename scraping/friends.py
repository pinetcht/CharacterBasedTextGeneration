import requests
import json

# read the JSON file from the web
json_file = 'https://raw.githubusercontent.com/emorynlp/character-mining/master/json/friends_season_10.json'
r = requests.get(json_file)

# load season 1
season = json.loads(r.text)
# retrieve episodes
episodes = season['episodes']

with open('data/phoebe_buffay_dialouge.txt','a') as f:
    # iterate through the episodes
    for episode in episodes:
        scenes = episode['scenes']
        for scene in scenes:
            utterances = scene['utterances']
            for utterance in utterances:
                speaker = utterance['speakers']
                if len(speaker) != 0 and speaker[0] == 'Phoebe Buffay':
                    dialogue = utterance['transcript']
                    f.write(dialogue + "\n")
f.close()
print("write file done")
