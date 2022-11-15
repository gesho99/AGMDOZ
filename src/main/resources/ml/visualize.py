import numpy as np
import matplotlib.pyplot as plt
import sys


def main(result_id, args):
    x = [i for i in range(int(len(args)/3))]
    y = np.split(np.array(args), int(len(args)/3))
    y1 = [float(i[0]) for i in y]
    y2 = [float(i[1]) for i in y]
    y3 = [float(i[2]) for i in y]

    labels = ["POSITIVE ", "NEUTRAL", "NEGATIVE"]
    colors = ["yellowgreen", "yellow", "tomato"]

    plt.style.use('dark_background')
    fig, ax = plt.subplots()
    ax.stackplot(x, y1, y2, y3, labels=labels, colors=colors)
    ax.legend(loc='upper left')
    plt.title("Conversation Mood Plot")
    plt.xlabel("Sentence Number")
    plt.ylabel("Mood Confidence")
    plt.xticks(x)
    plt.savefig("../static/{}.png".format(result_id))

# Alternative visualization through stacked bar charts:
    # y = np.split(np.array(args), int(len(args)/3))
    # plot_data = pd.DataFrame({
    #     "Positive": [float(i[0]) for i in y],
    #     "Neutral": [float(i[1]) for i in y],
    #     "Negative": [float(i[2]) for i in y]
    # })
    # plt.style.use('dark_background')
    # plot_data.plot(kind="bar", stacked=True, color=["yellowgreen", "cyan", "tomato"])
    # plt.title("Conversation mood plot")
    # plt.xlabel("Sentence Number")
    # plt.ylabel("Mood Confidence")
    # plt.savefig("../static/{}.png".format(result_id))


main(sys.argv[1], sys.argv[2:])
