import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Day13 {

	private static long[] origCode = new long[4096];

	private static void readProgram(long[] code, String name) throws FileNotFoundException {
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(new File(name)).useDelimiter(",|\n");
		int i = 0;
		while (scanner.hasNextLong())
			code[i++] = scanner.nextLong();
		scanner.close();
	}

	private static final Long EOF = new Long(Long.MIN_VALUE);

	private static void runProgram(Pipe<Long> input, Pipe<Long> output) throws InterruptedException {
		long[] memory = origCode.clone();

		int pc = 0;
		int relBase = 0;

		while (true) {
			int opCode = (int) memory[pc];
			int opCodeBase = opCode % 100;

			int modes = opCode / 100;
			int mode0 = modes % 10;
			modes /= 10;
			int mode1 = modes % 10;
			modes /= 10;
			int mode2 = modes % 10;
			int index0 = (int) (mode0 == 1 ? pc + 1 : (memory[pc + 1] + (mode0 == 2 ? relBase : 0)));
			int index1 = (int) (mode1 == 1 ? pc + 2 : (memory[pc + 2] + (mode1 == 2 ? relBase : 0)));
			int index2 = (int) (mode2 == 1 ? pc + 3 : (memory[pc + 3] + (mode2 == 2 ? relBase : 0)));

			if (opCodeBase == 99) {
				pc++;
				output.put(EOF);
				break;
			}
			if (opCodeBase == 3) {
				memory[index0] = input.get();
				pc += 2;
			} else if (opCodeBase == 4) {
				output.put(memory[index0]);
				pc += 2;
			} else if (opCodeBase == 9) {
				relBase += (int) memory[index0];
				pc += 2;
			} else if (opCodeBase == 8 || opCodeBase == 7 || opCodeBase == 6 || opCodeBase == 5 || opCodeBase == 2
					|| opCodeBase == 1) {
				long data1 = memory[index0];
				long data2 = memory[index1];
				long res = -1;
				switch (opCodeBase) {
				case 1:
					res = data1 + data2;
					pc += 4;
					break;
				case 2:
					res = data1 * data2;
					pc += 4;
					break;
				case 5:
					if (data1 != 0)
						pc = (int) data2;
					else
						pc += 3;
					break;
				case 6:
					if (data1 == 0)
						pc = (int) data2;
					else
						pc += 3;
					break;
				case 7:
					res = data1 < data2 ? 1 : 0;
					pc += 4;
					break;
				case 8:
					res = data1 == data2 ? 1 : 0;
					pc += 4;
					break;
				default:
				}
				if (opCodeBase != 5 && opCodeBase != 6)
					memory[index2] = res;
			} else {
				System.out.println("Unrecognized op-code!");
				System.exit(1);
			}
		}
	}

	public static void main(String[] args) throws FileNotFoundException, InterruptedException {
		boolean part2 = true;
		readProgram(origCode, "data/data13.txt");

		Pipe<Long> inputPipe = new Pipe<Long>(2);
		Pipe<Long> outputPipe = new Pipe<Long>(2);

		new Thread(() -> {
			try {
				runProgram(inputPipe, outputPipe);
			} catch (InterruptedException ie) {
			}
		}).start();
		int count = 0;
		long totalScore = 0;
		while (true) {
			Long x = outputPipe.get();
			if (x == EOF)
				break;
			Long y = outputPipe.get();
			if (y == EOF)
				break;
			Long t = outputPipe.get();
			if (t == EOF)
				break;
			if (t == 2L) {
				count++;
				// This formula is from hacking the game code on a side
				totalScore += origCode[1647 + ((24 * x.intValue() + y.intValue()) * (int)origCode[613] + (int)origCode[616]) % (24 * 42 * 64)
						% (24 * 42 * 8) % (24 * 42)];
			}
		}
		if (part2)
			System.out.println(totalScore);
		else
			System.out.println(count);
	}

	static class Pipe<T> {
		private T[] elements;
		private int size = 0;
		private int inIndex = 0;
		private int outIndex = 0;

		@SuppressWarnings("unchecked")
		Pipe(int capacity) {
			this.elements = (T[]) new Object[capacity];
		}

		synchronized T get() throws InterruptedException {
			while (size == 0)
				wait();
			T ret = elements[outIndex];
			outIndex = (outIndex + 1) % elements.length;
			size--;
			notifyAll();
			return ret;
		}

		synchronized void put(T e) throws InterruptedException {
			while (size == elements.length)
				wait();
			elements[inIndex] = e;
			inIndex = (inIndex + 1) % elements.length;
			size++;
			notifyAll();
		}
	}
}
