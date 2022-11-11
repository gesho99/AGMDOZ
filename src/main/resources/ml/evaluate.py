import sys

from transformers import AutoModelForSequenceClassification
from transformers import TFAutoModelForSequenceClassification
from transformers import AutoTokenizer
import numpy as np
from scipy.special import softmax
import csv
import urllib.request
import torch
import json

task = 'sentiment'
MODEL = f"cardiffnlp/twitter-roberta-base-{task}"


def preprocess(text):
    new_text = []

    for t in text.split(" "):
        t = '@user' if t.startswith('@') and len(t) > 1 else t
        t = 'http' if t.startswith('http') else t
        new_text.append(t)
    return " ".join(new_text)


def main(*args):
    tokenizer = AutoTokenizer.from_pretrained(MODEL)

    # download label mapping
    mapping_link = f"https://raw.githubusercontent.com/cardiffnlp/tweeteval/main/datasets/{task}/mapping.txt"
    with urllib.request.urlopen(mapping_link) as f:
        html = f.read().decode('utf-8').split("\n")
        csvreader = csv.reader(html, delimiter='\t')
    labels = [row[1] for row in csvreader if len(row) > 1]

    model = AutoModelForSequenceClassification.from_pretrained(MODEL)
    # model.save_pretrained(MODEL)
    # tokenizer.save_pretrained(MODEL)

    text = args[0][0]
    text = preprocess(text)
    encoded_input = tokenizer(text, return_tensors='pt')
    output = model(**encoded_input)
    scores = output[0][0].detach().numpy()
    scores = softmax(scores)

    ranking = np.argsort(scores)
    ranking = ranking[::-1]

    dictionary = {}

    for i in range(scores.shape[0]):
        l = labels[ranking[i]]
        s = scores[ranking[i]]
        dictionary[l] = np.round(float(s), 4)

    print(json.dumps(dictionary, indent=4))


if __name__ == '__main__':
    main(sys.argv[1:])
