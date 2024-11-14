import requests
from bs4 import BeautifulSoup
import re

luke_dialogue = []

# URL of the Star Wars script
url1 = "https://imsdb.com/scripts/Star-Wars-A-New-Hope.html"
url2 = "https://imsdb.com/scripts/Star-Wars-The-Empire-Strikes-Back.html"
url3 = "https://imsdb.com/scripts/Star-Wars-Return-of-the-Jedi.html"

def luke_get_dialogue(url):
    # Send a GET request to fetch the webpage
    response = requests.get(url)
    response.raise_for_status()  # Check if request was successful

    # Parse the webpage content
    soup = BeautifulSoup(response.text, 'html.parser')

    # Find the script content inside <pre> tags
    script = soup.find('pre').get_text()

    # Split the script into lines
    lines = script.split('\n')

    # Loop through each line
    for i, line in enumerate(lines):
        line = line.strip()
        
        # # Check if the line starts with "LUKE" (or contains "LUKE" as the speaker)
        if line.startswith("LUKE"):
            # If LUKE is speaking, check if there's a dialogue line right after it
            dialogue_line = ""

            j = i

            while(j+1 < len(lines) and not lines[j+1].isspace()):
                dialogue_line += lines[j+1].strip() + " "
                dialogue_line.replace("\n", " ")
                j += 1
                
            if dialogue_line:
                luke_dialogue.append(dialogue_line)

            # # The next line after LUKE usually contains the dialogue
            # if i + 1 < len(lines):
            #     dialogue_line = lines[i + 1].strip()
            
            # # If there's actual dialogue, store it
            # if dialogue_line:
            #     luke_dialogue.append(dialogue_line)

luke_get_dialogue(url1)
luke_get_dialogue(url2)
luke_get_dialogue(url3)

# Save the dialogue to a text file
with open("data/luke_skywalker_dialogue.txt", "w") as file:
    for line in luke_dialogue:
        if "(" not in line:
            file.write(line + "\n")

print(f"Luke Skywalker's dialogue has been saved to 'luke_skywalker_dialogue.txt'.")
