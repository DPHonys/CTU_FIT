import sys


def main():
    for line in sys.stdin:
        if 'Exit' == line.rstrip():
            break
        if 'g' == line.rstrip():
            print("g")
            continue
        print("test")
    print("END")


if __name__ == "__main__":
    main()