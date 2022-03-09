import serial
import sys


class Uart:
    def __init__(self):
        self._ser = serial.Serial()
        self._ser.port = 'COM6'
        self._ser.baudrate = 9600
        self._ser.timeout = 1
        try:
            self._ser.open()
        except serial.SerialException:
            print("Error while opening " + str(self._ser.port))
            sys.exit()

    def read_data(self):  # returns received data as string
        return self._ser.readline().decode("utf-8")

    def write_data(self, data):  # sends string data
        to_send = bytes(data, 'ASCII')
        self._ser.write(to_send)

    def close(self):
        self._ser.close()


def main():
    ser = Uart()
    for line in sys.stdin:
        if 'Exit' == line.rstrip():
            break

        if 'g' == line.rstrip() or 'G' == line.rstrip() or 'green' == line.rstrip() or 'GREEN' == line.rstrip():
            ser.write_data('G')
            back = ser.read_data()
            if back == '1':
                print("Green led > ON")
                continue
            else:
                print("Green led > OFF")
                continue

        if 'b' == line.rstrip() or 'B' == line.rstrip() or 'blue' == line.rstrip() or 'BLUE' == line.rstrip():
            ser.write_data('B')
            back = ser.read_data()
            if back == '1':
                print("Blue led > ON")
                continue
            else:
                print("Blue led > OFF")
                continue

        if 'r' == line.rstrip() or 'R' == line.rstrip() or 'red' == line.rstrip() or 'RED' == line.rstrip():
            ser.write_data('R')
            back = ser.read_data()
            if back == '1':
                print("Red led > ON")
                continue
            else:
                print("Red led > OFF")
                continue

        if 'j' == line.rstrip() or 'J' == line.rstrip() or 'joy' == line.rstrip() or 'JOY' == line.rstrip():
            ser.write_data('J')
            back = ser.read_data()
            x, y = back.split(' ')
            print(f'Joystick - X {x} Y {y}')
            continue

        print("WRONG COMMAND!")
    ser.close()
    print("END")


if __name__ == "__main__":
    main()
