import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Day02 {

	private static int readAndRunProgram(int p1, int p2) throws FileNotFoundException {

		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(new File("data/data02.txt")).useDelimiter(",|\n");
		int[] memory = new int[4096];
		int i = 0;
		while (scanner.hasNextInt())
			memory[i++] = scanner.nextInt();
		scanner.close();

		memory[1] = p1;
		memory[2] = p2;
		int pc = 0;
		while (true) {
			int opCode = memory[pc];
			if (opCode == 99)
				break;
			if (opCode == 1 || opCode == 2) {
				int data1 = memory[memory[pc + 1]];
				int data2 = memory[memory[pc + 2]];
				int res = opCode == 1 ? data1 + data2 : data1 * data2;
				memory[memory[pc + 3]] = res;
			} else {
				System.out.println("Unrecognized op-code!");
				System.exit(1);
			}
			pc += 4;
		}
		return memory[0];
	}

	public static void main(String[] args) throws FileNotFoundException {
		boolean part2 = true;
		if (!part2) {
			System.out.println(readAndRunProgram(12, 2));
		} else {
			int target = 19690720;
			for (int p1 = 0; p1 < 100; p1++)
				for (int p2 = 0; p2 < 100; p2++) {
					if (readAndRunProgram(p1, p2) == target) {
						System.out.println((100 * p1 + p2));
						System.exit(0);
					}
				}
		}
	}

}
