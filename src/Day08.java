import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Day08 {

	private static final int X = 25;
	private static final int Y = 6;

	private static List<int[][]> layers = new ArrayList<int[][]>();
	private static List<int[]> counts = new ArrayList<int[]>();

	public static void main(String[] args) throws IOException {
		boolean part2 = true;

		FileInputStream fin = new FileInputStream(new File("data/data08.txt"));
		int x = 0;
		int y = 0;
		int[][] layer = new int[X][Y];
		// part 1
		int[] count = new int[3];
		int zeroIndex = 0;
		int zeroCount = Integer.MAX_VALUE;
		int layerIndex = 0;

		while (fin.available() > 0) {
			char ch = (char) fin.read();
			if (!Character.isDigit(ch))
				break;
			int c = ch - 0x30;
			layer[x][y] = c;
			// part1
			count[c]++;
			x++;
			if (x == X) {
				x = 0;
				y++;
				if (y == Y) {
					y = 0;
					layers.add(layer);
					layer = new int[X][Y];
					// part1
					counts.add(count);
					if (count[0] < zeroCount) {
						zeroCount = count[0];
						zeroIndex = layerIndex;
					}
					count = new int[3];
					//
					layerIndex++;
				}
			}

		}
		fin.close();
		if (!part2) {
			int[] c = counts.get(zeroIndex);
			System.out.println(c[1] * c[2]);
		} else {
			for (int j = 0; j < Y; j++) {
				for (int i = 0; i < X; i++) {
					int k = -1;
					int pixel = 0;
					do {
						k++;
						pixel = layers.get(k)[i][j];
					} while (pixel == 2 && k + 1 < layerIndex);
					System.out.print(pixel == 1 ? "*" : " ");
				}
				System.out.println();
			}

		}
	}

}
