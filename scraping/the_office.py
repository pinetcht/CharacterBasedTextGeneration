import json

with open('./data/the-office.json', 'r') as file:
    data = json.load(file)

def extract_michael_lines(data):
    lines = []
    if isinstance(data, list):
        for item in data:
            lines.extend(extract_michael_lines(item))
    elif isinstance(data, dict):
        if data.get("character") == "Michael":
            lines.append(data["line"])
        for key, value in data.items():
            lines.extend(extract_michael_lines(value))
    return lines

lines = extract_michael_lines(data)

with open('michael_dialogue.txt', 'w') as output_file:
    output_file.write("\n".join(lines))
