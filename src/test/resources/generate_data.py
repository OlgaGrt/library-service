import json
import random

def generate_data(num_records):

    usernames = ["user6A", "user7A", "user8A", "user9A", "user10A"]
    full_names = ["Full Name 6A", "Full Name 7A", "Full Name 8A", "Full Name 9A", "Full Name 10A"]

    data = []
    for i in range(1, num_records + 1):
        user_index = i % 5

        record = {
            "username": usernames[user_index],
            "userFullName": full_names[user_index],
            "userActive": True,
            "bookName": "Book Name {}".format(i),
            "bookAuthor": "Book Author {}".format(i)
        }
        data.append(record)
    return data

num_records = 1000000
data = {"data": generate_data(num_records)}

with open('data2.json', 'w') as f:
    json.dump(data, f, ensure_ascii=False, indent=4)
