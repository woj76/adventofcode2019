import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Day07 {

	private static List<int[]> combinations = new LinkedList<int[]>();

	private static void generateCombinations(int digit, int[] current, boolean[] occupied, boolean part2) {
		if (digit == 1) {
			int p = 0;
			while (occupied[p])
				p++;
			current[p] = digit + (part2 ? 4 : -1);
			combinations.add(current.clone());
		} else
			for (int dp = 0; dp < digit; dp++) {
				int p = -1;
				for (int i = 0; i <= dp; i++) {
					p++;
					while (occupied[p])
						p++;
				}
				occupied[p] = true;
				current[p] = digit + (part2 ? 4 : -1);
				generateCombinations(digit - 1, current, occupied, part2);
				occupied[p] = false;
			}
	}

	private static int[] origCode = new int[4096];

	private static void readProgram() throws FileNotFoundException {
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(new File("data/data07.txt")).useDelimiter(",|\n");
		int i = 0;
		while (scanner.hasNextInt())
			origCode[i++] = scanner.nextInt();
		scanner.close();
	}

	private static void runProgram(Pipe<Integer> input, Pipe<Integer> output) throws InterruptedException {
		int[] memory = origCode.clone();

		int pc = 0;

		while (true) {
			int opCode = memory[pc];
			int opCodeBase = opCode % 100;

			int modes = opCode / 100;
			int mode0 = modes % 10;
			modes /= 10;
			int mode1 = modes % 10;
			modes /= 10;
			int mode2 = modes % 10;
			int index0 = mode0 == 1 ? pc + 1 : memory[pc + 1];
			int index1 = mode1 == 1 ? pc + 2 : memory[pc + 2];
			int index2 = mode2 == 1 ? pc + 3 : memory[pc + 3];

			if (opCodeBase == 99) {
				pc++;
				break;
			}
			if (opCodeBase == 3) {
				memory[index0] = input.get();
				pc += 2;
			} else if (opCodeBase == 4) {
				output.put(memory[index0]);
				pc += 2;
			} else if (opCodeBase == 8 || opCodeBase == 7 || opCodeBase == 6 || opCodeBase == 5 || opCodeBase == 2
					|| opCodeBase == 1) {
				int data1 = memory[index0];
				int data2 = memory[index1];
				int res = -1;
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
						pc = data2;
					else
						pc += 3;
					break;
				case 6:
					if (data1 == 0)
						pc = data2;
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
		readProgram();
		generateCombinations(5, new int[5], new boolean[5], part2);
		int maxOutput = Integer.MIN_VALUE;
		for (int[] combination : combinations) {
			int output = 0;
			if (!part2) {
				for (int i = 0; i < 5; i++) {
					Pipe<Integer> inputPipe = new Pipe<Integer>(2);
					Pipe<Integer> outputPipe = new Pipe<Integer>(2);
					inputPipe.put(combination[i]);
					inputPipe.put(output);
					runProgram(inputPipe, outputPipe);
					output = outputPipe.get();
				}
			} else {
				@SuppressWarnings("unchecked")
				Pipe<Integer>[] pipes = (Pipe<Integer>[]) new Pipe[5];
				for (int i = 0; i < 5; i++) {
					pipes[i] = new Pipe<Integer>(2);
					pipes[i].put(combination[i]);
				}
				pipes[0].put(0);
				Thread[] ts = new Thread[5];
				for (int i = 0; i < 5; i++) {
					int pi = i;
					ts[i] = new Thread(() -> {
						try {
							runProgram(pipes[pi], pipes[(pi + 1) % 5]);
						} catch (InterruptedException ie) {
						}
						;
					});
					ts[i].start();
				}
				for (Thread t : ts)
					t.join();
				output = pipes[0].get();
			}
			if (output > maxOutput)
				maxOutput = output;
		}
		System.out.println(maxOutput);
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
