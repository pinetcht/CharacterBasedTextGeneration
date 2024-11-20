import json

with open('the-office.json', 'r') as file:
    data = json.load(file)

def extract_michael_lines(data):
    michael_lines = []
    if isinstance(data, list):
        for item in data:
            michael_lines.extend(extract_michael_lines(item))
    elif isinstance(data, dict):
        if data.get("character") == "Michael":
            michael_lines.append(data["line"])
        for key, value in data.items():
            michael_lines.extend(extract_michael_lines(value))
    return michael_lines

michael_lines = extract_michael_lines(data)

with open('michael_lines.txt', 'w') as output_file:
    output_file.write("\n".join(michael_lines))
