import sys

from deepmultilingualpunctuation import PunctuationModel


def main(*args):
    model = PunctuationModel()
    result = model.restore_punctuation(args[0][0])
    print(result)


main(sys.argv[1:])
