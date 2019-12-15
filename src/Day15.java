import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class Day15 {

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
		boolean part2 = false;
		readProgram(origCode, "data/data15.txt");

		Pipe<Long> inputPipe = new Pipe<Long>(2);
		Pipe<Long> outputPipe = new Pipe<Long>(2);

		Thread thread = new Thread(() -> {
			try {
				runProgram(inputPipe, outputPipe);
			} catch (InterruptedException ie) {
			}
		});
		thread.start();
		int currentX = 0;
		int currentY = 0;
		int nextX = 0;
		int nextY = 0;
		int[] directions = { 1, 3, 2, 4 };
		int directionIndex = 0;
		int lastDirectionIndex = 0;
		Map<Pair, Integer> map = new TreeMap<Pair, Integer>();
		while (true) {
			directionIndex = (lastDirectionIndex + 1) % 4;
			nextX = currentX;
			nextY = currentY;
			switch (directions[directionIndex]) {
			case 1:
				nextY--;
				break;
			case 2:
				nextY++;
				break;
			case 3:
				nextX--;
				break;
			case 4:
				nextX++;
				break;
			default:
				break;
			}
			inputPipe.put((long) directions[directionIndex]);
			long res = outputPipe.get();
			if (res == 0L)
				lastDirectionIndex = (lastDirectionIndex + 3) % 4;
			else {
				currentX = nextX;
				currentY = nextY;
				map.put(new Pair(currentX, currentY), (int) res);
				if((currentX == 0 && currentY == 0) || (!part2 && res == 2L))
					break;
				lastDirectionIndex = directionIndex;
			}
		}
		thread.interrupt();
		if (!part2) {
			map.put(new Pair(0, 0), 3);
			while (true) {
				List<Pair> deadEnds = new ArrayList<Pair>();
				for (Pair p : map.keySet())
					if (map.get(p) == 1) {
						int cnt = 0;
						for (Pair n : p.neighbours())
							if (map.containsKey(n))
								cnt++;
						if (cnt == 1)
							deadEnds.add(p);

					}
				if (deadEnds.size() == 0)
					break;
				for (Pair p : deadEnds)
					map.remove(p);
			}
			System.out.println(map.size() - 1);
		} else {
			int cnt = 0;
			Set<Pair> oxygen = new TreeSet<Pair>();
			Set<Pair> noOxygen = new TreeSet<Pair>();
			for (Pair p : map.keySet())
				if (map.get(p) == 1)
					noOxygen.add(p);
				else
					oxygen.add(p);
			while (noOxygen.size() > 0) {
				List<Pair> newOxygen = new ArrayList<Pair>();
				for (Pair p : noOxygen) {
					for (Pair n : p.neighbours()) {
						if (oxygen.contains(n)) {
							newOxygen.add(p);
							break;
						}
					}
				}
				oxygen.addAll(newOxygen);
				noOxygen.removeAll(newOxygen);
				cnt++;
			}
			System.out.println(cnt);
		}
	}

	private static class Pair implements Comparable<Pair> {
		int x, y;

		Pair(int x, int y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public boolean equals(Object other) {
			return compareTo((Pair) other) == 0;
		}

		Pair[] neighbours() {
			return new Pair[] { new Pair(x - 1, y), new Pair(x + 1, y), new Pair(x, y - 1), new Pair(x, y + 1) };
		}

		@Override
		public int compareTo(Pair o) {
			if (x > o.x)
				return 1;
			if (x < o.x)
				return -1;
			if (y > o.y)
				return 1;
			if (y < o.y)
				return -1;
			return 0;
		}
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
