import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Day05 {

	private static int readAndRunProgram(int input) throws FileNotFoundException {

		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(new File("data/data05.txt")).useDelimiter(",|\n");
		int[] memory = new int[4096];
		int i = 0;
		while (scanner.hasNextInt())
			memory[i++] = scanner.nextInt();
		scanner.close();

		int lastOutput = -1;

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
				memory[index0] = input;
				pc += 2;
			} else if (opCodeBase == 4) {
				lastOutput = memory[index0];
				// System.out.println(lastOutput);
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
		return lastOutput;
	}

	public static void main(String[] args) throws FileNotFoundException {
		boolean part2 = true;
		int input = part2 ? 5 : 1;
		System.out.println(readAndRunProgram(input));
	}

}
