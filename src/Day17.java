import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class Day17 {

	private static long[] origCode = new long[8192];

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

	private static int occurrence(String pat, String str) {
		int lastOccurrence = 0;
		int occurrenceCount = 0;
		while (true) {
			lastOccurrence = str.indexOf(pat, lastOccurrence);
			if (lastOccurrence == -1)
				break;
			lastOccurrence += pat.length();
			occurrenceCount++;
		}
		return occurrenceCount;
	}

	public static void main(String[] args) throws FileNotFoundException, InterruptedException {
		boolean part2 = true;
		boolean show = true;
		readProgram(origCode, "data/data17.txt");

		Pipe<Long> inputPipe = new Pipe<Long>(2);
		Pipe<Long> outputPipe = new Pipe<Long>(2);

		Thread thread = new Thread(() -> {
			try {
				runProgram(inputPipe, outputPipe);
			} catch (InterruptedException ie) {
			}
		});
		thread.start();
		Map<Pair, Character> map = new TreeMap<Pair, Character>();
		int x = 0;
		int y = 0;
		Pair start = null;
		while (true) {
			long res = outputPipe.get();
			if (res == EOF)
				break;
			char c = (char) res;
			if (c == '\n') {
				x = 0;
				y++;
			} else {
				if (c == '#' || c == '<' || c == '>' || c == '^' || c == 'v') {
					map.put(new Pair(x, y), c);
					if (c != '#')
						start = new Pair(x, y);
				}
				x++;
			}
			if (show)
				System.out.print(c);
		}
		if (!part2) {
			int count = 0;
			for (Pair p : map.keySet()) {
				int nc = 0;
				for (Pair n : p.neighbours())
					if (map.containsKey(n))
						nc++;
				if (nc == 4)
					count += (p.x * p.y);
			}
			System.out.println(count);
		} else {
			String path = "";
			Pair next = start;
			Pair prev = new Pair(-1, -1);
			char dc = map.get(start);
			int d = -1;
			String move = null;
			int forwardCount = 0;
			while (true) {
				Pair[] ns = next.neighbours();
				if (d != -1 && map.containsKey(ns[d])) {
					prev = next;
					next = ns[d];
					forwardCount++;
				} else {
					int nc = 0;
					for (int ni = 0; ni < ns.length; ni++) {
						Pair n = ns[ni];
						if (map.containsKey(n) && !prev.equals(n)) {
							nc++;
							d = ni;
						}
					}
					if (nc == 0)
						break;
					switch (dc) {
					case '^':
						if (d == 1) {
							move = "R,";
							dc = '>';
						} else if (d == 3) {
							move = "L,";
							dc = '<';
						}
						break;
					case '>':
						if (d == 2) {
							move = "R,";
							dc = 'v';
						} else if (d == 0) {
							move = "L,";
							dc = '^';
						}
						break;
					case 'v':
						if (d == 3) {
							move = "R,";
							dc = '<';
						} else if (d == 1) {
							move = "L,";
							dc = '>';
						}
						break;
					case '<':
						if (d == 0) {
							move = "R,";
							dc = '^';
						} else if (d == 2) {
							move = "L,";
							dc = 'v';
						}
						break;
					default:
						break;
					}
					if (forwardCount > 0) {
						move = forwardCount + "," + move;
						forwardCount = 0;
					}
					path += move;
				}
			}
			if (forwardCount > 0)
				path += "" + forwardCount + ",";
			String moveCommands = "";
			char functionName = 'A';
			while (true) {
				int idx = -1;
				char fc = 0;
				do
					fc = path.charAt(++idx);
				while (idx < path.length() && fc != 'L' && fc != 'R');
				String toSearch = "";
				int occurrence = 0;
				int r = 0;
				while (true) {
					String nextPair = path.substring(idx, path.indexOf(',', path.indexOf(',', idx) + 1) + 1);
					idx += nextPair.length();
					r++;
					if (r > 3 && occurrence(toSearch + nextPair, path) < occurrence)
						break;
					toSearch += nextPair;
					if (r == 3)
						occurrence = occurrence(toSearch, path);
				}
				path = path.replaceAll(toSearch, functionName + ",");
				moveCommands += toSearch.substring(0, toSearch.length() - 1) + "\n";
				functionName++;
				if (functionName == 'D')
					break;
			}
			moveCommands = path.substring(0, path.length() - 1) + "\n" + moveCommands + "n\n";

			origCode[0] = 2L;
			thread = new Thread(() -> {
				try {
					runProgram(inputPipe, outputPipe);
				} catch (InterruptedException ie) {
				}
			});
			thread.start();

			String lastLine = "";
			boolean send = false;

			while (true) {
				long c = outputPipe.get();
				if (c == EOF)
					break;
				if (moveCommands.length() == 0) {
					if (c < 128) {
						if (show)
							System.out.print((char) c);
						continue;
					} else {
						System.out.println(c);
					}
				}
				if (c == 10L) {
					if (show)
						System.out.println(lastLine);
					if (lastLine.equals("Main:")) {
						send = true;
					}
					lastLine = "";
					if (send) {
						do {
							c = moveCommands.charAt(0);
							inputPipe.put(c);
							if (show)
								System.out.print((char) c);
							moveCommands = moveCommands.substring(1);
						} while (c != '\n');
					}
				} else {
					lastLine += (char) c;
				}
			}

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
			return new Pair[] { new Pair(x, y - 1), new Pair(x + 1, y), new Pair(x, y + 1), new Pair(x - 1, y) };
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
