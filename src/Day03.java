import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

public class Day03 {

	private static class Pair implements Comparable<Pair> {
		int x, y, z;

		Pair(int x, int y, int z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}

		@Override
		public boolean equals(Object other) {
			return compareTo((Pair) other) == 0;
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

	private static void createPath(Set<Pair> path, List<String> wireList) {
		int x = 0;
		int y = 0;
		int z = 0;

		for (String wire : wireList) {
			char dir = wire.charAt(0);
			int steps = Integer.parseInt(wire.substring(1));
			for (int i = 0; i < steps; i++) {
				switch (dir) {
				case 'R':
					x++;
					break;
				case 'L':
					x--;
					break;
				case 'U':
					y++;
					break;
				case 'D':
					y--;
					break;
				default:
				}
				z++;
				path.add(new Pair(x, y, z));
			}
		}
	}

	public static void main(String[] args) throws FileNotFoundException {

		boolean part2 = true;

		Scanner scanner = new Scanner(new File("data/data03.txt"));
		StringTokenizer w1 = new StringTokenizer(scanner.nextLine(), ",");
		StringTokenizer w2 = new StringTokenizer(scanner.nextLine(), ",");
		scanner.close();

		List<String> wire1List = new ArrayList<String>();
		List<String> wire2List = new ArrayList<String>();
		while (w1.hasMoreTokens())
			wire1List.add(w1.nextToken());
		while (w2.hasMoreTokens())
			wire2List.add(w2.nextToken());

		Set<Pair> path1 = new TreeSet<Pair>();
		Set<Pair> path2 = new TreeSet<Pair>();
		createPath(path1, wire1List);
		createPath(path2, wire2List);

		List<Pair> inter = new ArrayList<Pair>();
		path1.retainAll(path2);
		path2.retainAll(path1);
		Iterator<Pair> it1 = path1.iterator();
		Iterator<Pair> it2 = path2.iterator();
		while (it1.hasNext() && it2.hasNext()) {
			Pair p1 = it1.next();
			Pair p2 = it2.next();
			inter.add(new Pair(p1.x, p1.y, p1.z + p2.z));
		}

		int distance = Integer.MAX_VALUE;
		for (Pair p : inter) {
			int d = part2 ? p.z : (p.x > 0 ? p.x : -p.x) + (p.y > 0 ? p.y : -p.y);
			if (d < distance)
				distance = d;
		}

		System.out.println(distance);

	}

}
