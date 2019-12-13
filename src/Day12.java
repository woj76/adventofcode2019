import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

public class Day12 {

	public static void main(String[] args) throws FileNotFoundException {

		boolean part2 = true;

		List<int[]> planets = new ArrayList<int[]>();

		Scanner scanner = new Scanner(new File("data/data12.txt"));
		while (scanner.hasNext()) {
			String line = scanner.nextLine().trim();
			line = line.substring(1, line.length() - 1);
			StringTokenizer st = new StringTokenizer(line, ",");
			int x = Integer.parseInt(st.nextToken().trim().substring(2));
			int y = Integer.parseInt(st.nextToken().trim().substring(2));
			int z = Integer.parseInt(st.nextToken().trim().substring(2));
			planets.add(new int[] { x, y, z, 0, 0, 0 });
		}
		scanner.close();
		if (!part2) {
			for (int i = 0; i < 1000; i++) {
				setSpeeds(planets, 0);
				for (int[] p : planets) {
					for (int j = 0; j < 3; j++)
						p[j] += p[j + 3];
				}
			}
			int energy = 0;
			for (int[] p : planets) {
				int pe = 0;
				int ke = 0;
				for (int j = 0; j < 3; j++) {
					pe += p[j] < 0 ? -p[j] : p[j];
					ke += p[j + 3] < 0 ? -p[j + 3] : p[j + 3];
				}
				energy += (pe * ke);
			}
			System.out.println(energy);
		} else {
			int xc = 0;
			int yc = 0;
			int zc = 0;
			Set<String> xs = new TreeSet<String>();
			Set<String> ys = new TreeSet<String>();
			Set<String> zs = new TreeSet<String>();
			int count = 0;
			while (true) {
				String xl = "";
				String yl = "";
				String zl = "";
				for (int[] p : planets) {
					xl += p[0] + " " + p[3] + " ";
					yl += p[1] + " " + p[4] + " ";
					zl += p[2] + " " + p[5] + " ";
				}
				if (xc == 0 && xs.contains(xl)) {
					xc = count;
				} else {
					xs.add(xl);
				}
				if (yc == 0 && ys.contains(yl)) {
					yc = count;
				} else {
					ys.add(yl);
				}
				if (zc == 0 && zs.contains(zl)) {
					zc = count;
				} else {
					zs.add(zl);
				}
				if (xc > 0 && yc > 0 && zc > 0)
					break;
				setSpeeds(planets, 0);
				for (int[] p : planets) {
					for (int j = 0; j < 3; j++)
						p[j] += p[j + 3];
				}
				count++;
			}
			BigInteger bx = BigInteger.valueOf(xc);
			BigInteger by = BigInteger.valueOf(yc);
			BigInteger bz = BigInteger.valueOf(zc);
			BigInteger xy = bx.multiply(by).divide(bx.gcd(by));
			System.out.println(xy.multiply(bz).divide(xy.gcd(bz)));
		}
	}

	private static void setSpeeds(List<int[]> planets, int index) {
		if (index == planets.size() - 1)
			return;
		for (int i = index + 1; i < planets.size(); i++) {
			int[] p1 = planets.get(index);
			int[] p2 = planets.get(i);
			for (int j = 0; j < 3; j++) {
				if (p1[j] > p2[j]) {
					p1[j + 3]--;
					p2[j + 3]++;
				} else if (p1[j] < p2[j]) {
					p1[j + 3]++;
					p2[j + 3]--;
				}
			}
		}
		setSpeeds(planets, index + 1);
	}
}
