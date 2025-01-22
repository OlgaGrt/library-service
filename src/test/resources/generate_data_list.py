import json

def generate_data(num_records):
    data = []
    for i in range(1, num_records + 1):
        user_index = i % 5

        record = {
            "username": "Username {}".format(i),
            "userFullName": "Full User Name {}".format(i),
            "userActive": True,
            "bookName": "Book Name {}".format(i),
            "bookAuthor": "Book Author {}".format(i)
        }
        data.append(record)

    return data

num_records = 1000000
data = generate_data(num_records)

with open('data4.json', 'w') as f:
    json.dump(data, f, ensure_ascii=False, indent=4)