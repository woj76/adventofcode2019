import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class Day20 {

	public static void main(String[] args) throws IOException {

		boolean part2 = true;
		FileInputStream in = new FileInputStream(new File("data/data20.txt"));
		Map<Pair, Character> map = new TreeMap<Pair, Character>();
		Set<Pair> letters = new TreeSet<Pair>();
		int x = 0;
		int y = 0;
		while (in.available() > 0) {
			char c = (char) in.read();
			if (c == '\n') {
				y++;
				x = 0;
			} else {
				if (c == '.' || Character.getType(c) == Character.UPPERCASE_LETTER) {
					Pair p = new Pair(x, y);
					map.put(p, c);
					if (c != '.')
						letters.add(p);
				}
				x++;
			}
		}
		in.close();

		Map<String, List<Pair>> gates = new TreeMap<String, List<Pair>>();
		for (Pair p : letters) {
			if (!map.containsKey(p))
				continue;
			Pair attach = null;
			Pair n = new Pair(p.x, p.y + 1);
			String key = null;
			if (map.containsKey(n) && letters.contains(n)) {
				key = "" + map.get(p) + map.get(n);
				map.remove(p);
				map.remove(n);
				Pair n1 = new Pair(p.x, p.y - 1);
				Pair n2 = new Pair(p.x, p.y + 2);
				if (map.containsKey(n1))
					attach = n1;
				else
					attach = n2;
			} else {
				n = new Pair(p.x + 1, p.y);
				if (map.containsKey(n) && letters.contains(n)) {
					key = "" + map.get(p) + map.get(n);
					map.remove(p);
					map.remove(n);
					Pair n1 = new Pair(p.x - 1, p.y);
					Pair n2 = new Pair(p.x + 2, p.y);
					if (map.containsKey(n1))
						attach = n1;
					else
						attach = n2;
				}
			}
			if (attach != null && key != null) {
				List<Pair> pairs = null;
				if (!gates.containsKey(key)) {
					pairs = new ArrayList<Pair>();
					gates.put(key, pairs);
				} else {
					pairs = gates.get(key);
				}
				pairs.add(attach);
			}
		}

		Pair start = gates.get("AA").get(0);
		gates.remove("AA");
		Pair end = gates.get("ZZ").get(0);
		gates.remove("ZZ");

		map.remove(start);
		map.remove(end);
		map.put(start, '.');
		map.put(end, '.');
		Map<Pair, Pair> points = new TreeMap<Pair, Pair>();
		// FIXME This is probably dataset dependent
		int levels = gates.size() - 2;
		for (Pair p : map.keySet()) {
			points.put(p, p);
			if (part2)
				for (int level = 0; level < levels; level++) {
					Pair pl = new Pair(p.x, p.y, level + 1);
					points.put(pl, pl);
				}
		}

		for (Pair p : points.keySet())
			for (Pair n : p.potentialNeighbours())
				if (points.containsKey(n))
					p.neighbours.add(points.get(n));

		for (String gate : gates.keySet()) {
			Pair p1 = points.get(gates.get(gate).get(0));
			Pair p2 = points.get(gates.get(gate).get(1));

			if (part2) {
				if (p1.y < 20 || p1.y > 100 || p1.x < 20 || p1.x > 100) {
					p1 = points.get(gates.get(gate).get(1));
					p2 = points.get(gates.get(gate).get(0));
				}
				for (int level = 0; level < levels; level++) {
					p1 = points.get(new Pair(p1.x, p1.y, level));
					p2 = points.get(new Pair(p2.x, p2.y, level + 1));
					p1.neighbours.add(p2);
					p2.neighbours.add(p1);
				}
			} else {
				p1.neighbours.add(p2);
				p2.neighbours.add(p1);
			}
		}

		System.out.println(findPath(start, end, points, 0, new TreeSet<Pair>()));
	}

	private static int findPath(Pair current, Pair end, Map<Pair, Pair> map, int currCost, Set<Pair> visited) {
		if (current.equals(end))
			return currCost;
		visited.add(current);
		int minCost = Integer.MAX_VALUE;
		for (Pair next : current.neighbours) {
			if (visited.contains(next))
				continue;
			Set<Pair> newVisited = new TreeSet<Pair>();
			newVisited.addAll(visited);
			int nextCost = findPath(next, end, map, currCost + 1, newVisited);
			if (minCost > nextCost)
				minCost = nextCost;
		}
		return minCost;
	}

	private static class Pair implements Comparable<Pair> {
		int x, y;
		int level = 0;
		List<Pair> neighbours = new ArrayList<Pair>();

		Pair(int x, int y) {
			this.x = x;
			this.y = y;
		}

		Pair(int x, int y, int level) {
			this.x = x;
			this.y = y;
			this.level = level;
		}

		@Override
		public boolean equals(Object other) {
			if (other == null)
				return false;
			return compareTo((Pair) other) == 0;
		}

		Pair[] potentialNeighbours() {
			return new Pair[] { new Pair(x, y - 1, level), new Pair(x + 1, y, level), new Pair(x, y + 1, level),
					new Pair(x - 1, y, level) };
		}

		@Override
		public int compareTo(Pair o) {
			if (level > o.level)
				return 1;
			if (level < o.level)
				return -1;
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

}
