import numpy as np
import matplotlib.pyplot as plt
import sys
import pandas as pd


def main(result_id, args):
    y = np.split(np.array(args), int(len(args)/3))
    plot_data = pd.DataFrame({
        "Positive": [float(i[0]) for i in y],
        "Neutral": [float(i[1]) for i in y],
        "Negative": [float(i[2]) for i in y]
    })
    plt.style.use('dark_background')
    plot_data.plot(kind="bar", stacked=True, color=["yellowgreen", "cyan", "tomato"])
    plt.title("Conversation mood plot")
    plt.xlabel("Sentence Number")
    plt.ylabel("Mood Confidence")
    plt.savefig("../static/{}.png".format(result_id))
    # plt.show()


main(sys.argv[1], sys.argv[2:])
