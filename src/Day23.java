import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Day23 {

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

	private static void runProgram(TwoPipes<Long> inputOutput) throws InterruptedException {
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
				inputOutput.put(EOF);
				break;
			}
			if (opCodeBase == 3) {
				memory[index0] = inputOutput.get();
				pc += 2;
			} else if (opCodeBase == 4) {
				inputOutput.put(memory[index0]);
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
		readProgram(origCode, "data/data23.txt");

		List<TwoPipes<Long>> pipes = new ArrayList<TwoPipes<Long>>();
		Pipe<Long> mainOutput = new Pipe<Long>(2048);
		List<Thread> threads = new ArrayList<Thread>();

		for (int threadIndex = 0; threadIndex < 50; threadIndex++) {
			Pipe<Long> inputPipe = new Pipe<Long>(4096, true, -1L);
			Pipe<Long> outputPipe = new Pipe<Long>(4096, mainOutput);
			inputPipe.put((long) threadIndex);
			TwoPipes<Long> inputOutput = new TwoPipes<Long>(inputPipe, outputPipe, -1L);
			pipes.add(inputOutput);
			Thread t = new Thread(() -> {
				try {
					runProgram(inputOutput);
				} catch (InterruptedException ie) {
				}
			});
			threads.add(t);
		}
		for (Thread t : threads)
			t.start();

		if (part2) {
			final Pipe<Long> inputPipe = new Pipe<Long>(256, true, -1L);
			final Pipe<Long> outputPipe = new Pipe<Long>(256, mainOutput);
			TwoPipes<Long> natPipes = new TwoPipes<Long>(inputPipe, outputPipe, -1L);
			pipes.add(natPipes);

			new Thread(() -> {
				try {
					Long natX = 0L;
					Long natY = 0L;
					Long prevSentY = null;
					boolean initialized = false;
					while (true) {
						Long x = inputPipe.get();
						if (x != -1L) {
							Long y = inputPipe.get();
							natX = x;
							natY = y;
							initialized = true;
						} else {
							if (!initialized)
								continue;
							boolean idle = true;

							for (int i = 0; i < 50; i++)
								idle = idle && pipes.get(i).isIdle();

							if (idle) {
								if (natY.equals(prevSentY)) {
									System.out.println(natY);
									System.exit(0);
								}
								mainOutput.putN(new Long[] { 0L, natX, natY }, 3);
								prevSentY = natY;
								initialized = false;
							}
						}

					}
				} catch (InterruptedException ie) {

				}
			}).start();
		}
		while (true) {
			Long o = mainOutput.get();
			Long x = mainOutput.get();
			Long y = mainOutput.get();
			if (!part2 && o == 255L) {
				System.out.println(y);
				break;
			}
			Pipe<Long> i = pipes.get(o == 255L ? 50 : o.intValue()).getInputPipe();
			i.putN(new Long[] { x, y }, 2);
		}
		for (Thread t : threads) {
			t.interrupt();
			t.join();
		}
	}

	static class TwoPipes<T> {
		final private Pipe<T> inputPipe;
		final private Pipe<T> outputPipe;
		private T emptyElement;
		private volatile int idleCount = 0;

		public TwoPipes(Pipe<T> inputPipe, Pipe<T> outputPipe, T emptyElement) {
			this.inputPipe = inputPipe;
			this.outputPipe = outputPipe;
			this.emptyElement = emptyElement;
		}

		synchronized public void put(T e) throws InterruptedException {
			idleCount = 0;
			outputPipe.put(e);
		}

		synchronized public T get() throws InterruptedException {
			T r = inputPipe.get();
			if (r.equals(emptyElement))
				idleCount++;
			else
				idleCount = 0;
			return r;
		}

		// This seems to give the correct answer of 13334 for this input
		synchronized public boolean isIdle() {
			boolean r = idleCount > 1000;
			return r;
		}

		synchronized Pipe<T> getInputPipe() {
			return inputPipe;
		}

		synchronized Pipe<T> getOutputPipe() {
			return outputPipe;
		}

	}

	private static class Pipe<T> {
		private T[] elements;
		private int size = 0;
		private int inIndex = 0;
		private int outIndex = 0;
		private boolean nonBlocking = false;
		private T emptyElement;
		private Pipe<T> mainOut = null;

		@SuppressWarnings("unchecked")
		private T[] lastThree = (T[]) new Object[3];
		private int lastThreeIndex = 0;

		@SuppressWarnings("unchecked")
		Pipe(int capacity) {
			this.elements = (T[]) new Object[capacity];
		}

		Pipe(int capacity, boolean nonBlocking, T emptyElement) {
			this(capacity);
			this.nonBlocking = nonBlocking;
			this.emptyElement = emptyElement;
		}

		Pipe(int capacity, Pipe<T> mainOut) {
			this(capacity);
			this.mainOut = mainOut;
		}

		synchronized T get() throws InterruptedException {
			if (nonBlocking && size == 0)
				return emptyElement;
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
			if (mainOut != null) {
				lastThree[lastThreeIndex] = e;
				lastThreeIndex++;
				if (lastThreeIndex == 3) {
					mainOut.putN(lastThree, 3);
					lastThreeIndex = 0;
				}
			}
			notifyAll();
		}

		synchronized void putN(T[] es, int n) throws InterruptedException {
			while (size >= elements.length - n)
				wait();
			for (int i = 0; i < n; i++) {
				elements[inIndex] = es[i];
				inIndex = (inIndex + 1) % elements.length;
			}
			size += n;
			notifyAll();
		}

	}

}
