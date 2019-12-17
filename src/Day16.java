import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Day16 {

	public static void main(String[] args) throws IOException {
		boolean part2 = true;
		List<Integer> input = new ArrayList<Integer>();
		FileInputStream in = new FileInputStream(new File("data/data16.txt"));
		while (in.available() > 0) {
			int c = in.read();
			if (c >= 0x30 && c <= 0x39)
				input.add(c - 0x30);
		}
		in.close();

		int rep = part2 ? 10000 : 1;
		int offset = 0;
		if (part2) {
			String offsetString = "";
			for (int i = 0; i < 7; i++)
				offsetString += input.get(i);
			offset = Integer.parseInt(offsetString);
		}

		int[] inputArray = new int[input.size() * rep - offset];
		int x = 0;
		int ix = offset % input.size();
		while (x < inputArray.length) {
			inputArray[x++] = input.get(ix++);
			if (ix == input.size())
				ix = 0;
		}

		int phase = 0;
		while (phase < 100) {

			int[] outputArray = new int[inputArray.length];
			if (part2) {
				outputArray[outputArray.length - 1] = inputArray[inputArray.length - 1];
				for (int o = inputArray.length - 2; o >= 0; o--)
					outputArray[o] = (outputArray[o + 1] + inputArray[o]) % 10;
			} else {
				for (int o = 0; o < inputArray.length; o++) {
					int r = 0;
					int patlen = (o + 1) * 4;
					int j = o;
					while (j < inputArray.length) {
						for (int k = j, l = j + patlen / 2; k <= j + o || l <= j + patlen / 2 + o; k++, l++) {
							if (k < inputArray.length)
								r += inputArray[k];
							if (l < inputArray.length)
								r -= inputArray[l];
						}
						j += patlen;
					}
					r = r % 10;
					if (r < 0)
						r = -r;
					outputArray[o] = r;
				}
			}
			inputArray = outputArray;
			phase++;
		}
		for (int i = 0; i < 8; i++)
			System.out.print(inputArray[i]);
		System.out.println();
	}
}
