import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class Day25 {

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

		while (!Thread.interrupted()) {
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
		readProgram(origCode, "data/data25.txt");

		Pipe<Long> inputPipe = new Pipe<Long>(512);
		Pipe<Long> outputPipe = new Pipe<Long>(512);

		Map<String, String> commands = new TreeMap<String, String>();

		// This is after manual exploration
		commands.put("mouse", "north\ntake mouse\nsouth\n");
		commands.put("pointer", "north\nnorth\ntake pointer\nsouth\nsouth\n");
		commands.put("monolith", "west\ntake monolith\neast\n");
		commands.put("food ration", "west\nnorth\nwest\ntake food ration\neast\nsouth\neast\n");
		commands.put("space law space brochure",
				"west\nnorth\nwest\nsouth\ntake space law space brochure\nnorth\neast\nsouth\neast\n");
		commands.put("mutex", "west\nsouth\nsouth\nwest\nsouth\ntake mutex\nnorth\n\neast\nnorth\nnorth\neast\n");
		commands.put("asterisk", "west\nsouth\nsouth\nwest\ntake asterisk\neast\nnorth\nnorth\neast\n");
		commands.put("sand", "west\nsouth\ntake sand\nnorth\neast\n");
		commands.put("switch", "south\nsouth\nwest\nsouth\ninv\neast\n");
		commands.put("solution", commands.get("mutex") + commands.get("asterisk")
				+ commands.get("space law space brochure") + commands.get("food ration") + commands.get("switch"));

		Thread thread = new Thread(() -> {
			try {
				runProgram(inputPipe, outputPipe);
			} catch (InterruptedException ie) {
			}
		});
		thread.start();
		Scanner scanner = new Scanner(System.in);

		String lastLine = "";
		int skipInput = 1;
		while (true) {
			char c = (char) outputPipe.get().intValue();
			lastLine += String.valueOf(c);
			if (c == '\n') {
				System.out.println(lastLine);
				if (lastLine.equals("Command?\n"))
					skipInput--;
				else if(lastLine.indexOf("Oh, hello!") != -1)
					break;
				lastLine = "";
			}
			if (skipInput == 0) {
				if (scanner.hasNextLine()) {
					String cmd = scanner.nextLine();
					if (commands.containsKey(cmd)) {
						skipInput = 0;
						if (cmd.equals("solution"))
							skipInput = 0;
						cmd = commands.get(cmd);
						for (char cr : cmd.toCharArray())
							if (cr == '\n')
								skipInput++;
					} else {
						cmd += "\n";
						skipInput = 1;
					}
					for (char ci : cmd.toCharArray())
						inputPipe.put(Long.valueOf(ci));
				}
			}
		}
		scanner.close();
		thread.interrupt();
		thread.join();
	}

	private static class Pipe<T> {
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
