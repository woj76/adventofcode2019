import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class Day01 {

	public static void main(String[] args) throws FileNotFoundException {

		boolean part2 = true;

		Scanner scanner = new Scanner(new File("data/data01.txt"));

		int totalFuel = 0;
		while (scanner.hasNextInt()) {
			int fuel = (scanner.nextInt() / 3) - 2;
			if (part2) {
				int addFuel = (fuel / 3) - 2;
				while (addFuel >= 0) {
					fuel += addFuel;
					addFuel = addFuel / 3 - 2;
				}
			}
			totalFuel += fuel;
		}
		scanner.close();

		System.out.println(totalFuel);

	}
}
