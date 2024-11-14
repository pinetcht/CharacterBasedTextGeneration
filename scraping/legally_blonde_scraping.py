import requests
from bs4 import BeautifulSoup
import re

elle_dialogue = []

# URL of the Star Wars script
url = "https://imsdb.com/scripts/Legally-Blonde.html"

def elle_get_dialogue(url):
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
        if line.startswith("ELLE"):
            # If LUKE is speaking, check if there's a dialogue line right after it
            dialogue_line = ""

            j = i+1

            while(j+1 < len(lines) and not lines[j+1].isspace()):
                dialogue_line += lines[j+1].strip() + " "
                dialogue_line.replace("\n", "")
                j += 1

            dialogue_line = re.sub("\(.*?\)", "", dialogue_line)

            if dialogue_line:
                elle_dialogue.append(dialogue_line)

elle_get_dialogue(url)

# Save the dialogue to a text file
with open("data/elle_woods_dialogue.txt", "w") as file:
    for line in elle_dialogue:
        file.write(line + "\n")

print(f"Elle Woods's dialogue has been saved to 'elle_woods_dialogue.txt'.")