import re
import string 

# with open("./data/luke_skywalker_dialogue.txt") as file:
#     with open("./data/preprocessed/luke_preprocessed.txt", "w") as processed_file:
#         for line in file:
#             line = line.lower()
#             p = re.compile("([" + re.escape(string.punctuation) + "])")
#             line = re.sub(p, " \\1 ", line)
#             line = re.sub('\s+', " ", line)
#             processed_file.write(line + '\n')

# with open("./data/michael_dialogue.txt") as file:
#     with open("./data/preprocessed/michael_preprocessed.txt", "w") as processed_file:
#         for line in file:
#             line = line.lower()
#             p = re.compile("([" + re.escape(string.punctuation) + "])")
#             line = re.sub(p, " \\1 ", line)
#             line = re.sub('\s+', " ", line)
#             processed_file.write(line + '\n')

with open("./data/phoebe_buffay_dialogue.txt") as file:
    with open("./data/preprocessed/phoebe_preprocessed.txt", "w") as processed_file:
        for line in file:
            if not re.search('^$', line):
                line = line.lower()
                p = re.compile("([" + re.escape(string.punctuation) + "])")
                line = re.sub(p, " \\1 ", line)
                line = re.sub("\s+", " ", line)
                processed_file.write(line + "\n")
                
