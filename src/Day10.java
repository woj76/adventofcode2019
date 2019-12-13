import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Day10 {

	private static int gcd(int a, int b) {
		if (a == 0)
			return b < 0 ? -b : b;
		if (b == 0)
			return a < 0 ? -a : a;
		return gcd(b, a % b);
	}

	private static int canView(int x1, int y1, int x2, int y2, boolean[][] map) {
		if (!map[x2][y2])
			return 0;
		int dx = x2 - x1;
		int dy = y2 - y1;
		int d = gcd(dx, dy);
		if (d == 0)
			return 0;
		dx /= d;
		dy /= d;
		x1 += dx;
		y1 += dy;
		while (x1 != x2 || y1 != y2) {
			if (map[x1][y1])
				return 0;
			x1 += dx;
			y1 += dy;
		}
		return 1;
	}

	public static void main(String[] args) throws IOException {

		FileInputStream fin = new FileInputStream(new File("data/data10.txt"));

		List<List<Boolean>> lines = new ArrayList<List<Boolean>>();
		List<Boolean> line = new ArrayList<Boolean>();

		while (fin.available() > 0) {
			switch ((char) fin.read()) {
			case '#':
				line.add(true);
				break;
			case '.':
				line.add(false);
				break;
			case '\n':
				lines.add(line);
				line = new ArrayList<Boolean>();
				break;
			default:
				break;
			}
		}
		fin.close();
		int ySize = lines.size();
		int xSize = lines.get(0).size();
		boolean[][] map = new boolean[xSize][ySize];
		int x = 0;
		int y = 0;
		for (List<Boolean> l : lines) {
			x = 0;
			for (Boolean b : l)
				map[x++][y] = b;
			y++;
		}

		int maxView = Integer.MIN_VALUE;
		int mx = 0;
		int my = 0;
		for (x = 0; x < xSize; x++)
			for (y = 0; y < ySize; y++) {
				if (!map[x][y])
					continue;
				int view = 0;
				for (int i = 0; i < xSize; i++)
					for (int j = 0; j < ySize; j++) {
						view += canView(x, y, i, j, map);
					}
				if (view > maxView) {
					maxView = view;
					mx = x;
					my = y;
				}
			}
		System.out.println("Part 1: " + maxView);
		Set<Slant> slants = new TreeSet<Slant>();
		for (int i = 0; i < xSize; i++) {
			for (int j = 0; j < ySize; j++) {
				int dx = i - mx;
				int dy = j - my;
				int d = gcd(dx, dy);
				if (d != 0) {
					dx /= d;
					dy /= d;
					slants.add(new Slant(dx, dy));
				}
			}
		}
		int shots = 0;
		while (true) {
			for (Slant s : slants) {
				x = mx + s.dx;
				y = my + s.dy;
				boolean shot = false;
				while (x >= 0 && x < xSize && y >= 0 && y < ySize) {
					if (map[x][y]) {
						shot = true;
						map[x][y] = false;
						shots++;
						break;
					}
					x += s.dx;
					y += s.dy;
				}
				if (shots == 200) {
					System.out.println("Part 2: " + (100 * x + y));
					break;
				}
				if (shot)
					continue;
			}
			if (shots == 200)
				break;
		}
	}

	private static class Slant implements Comparable<Slant> {
		int dx, dy;

		Slant(int dx, int dy) {
			this.dx = dx;
			this.dy = dy;
		}

		@Override
		public boolean equals(Object other) {
			return compareTo((Slant) other) == 0;
		}

		@Override
		public int compareTo(Slant o) {
			if (dx == o.dx && dy == o.dy)
				return 0;
			else if (dy < 0 && dx >= 0) {
				if (o.dy < 0 && o.dx >= 0 && 10000 * o.dx / o.dy > 10000 * dx / dy)
					return 1;
				return -1;
			} else if (dy >= 0 && dx > 0) {
				if (o.dy < 0 && o.dx >= 0)
					return 1;
				if (o.dy >= 0 && o.dx > 0 && 10000 * dy / dx > 10000 * o.dy / o.dx)
					return 1;
				return -1;
			} else if (dy > 0 && dx <= 0) {
				if (o.dy <= 0 && o.dx < 0)
					return -1;
				if (o.dy > 0 && o.dx <= 0 && 10000 * o.dx / o.dy < 10000 * dx / dy)
					return -1;
				return 1;
			} else { // dy <= 0 && dx < 0
				if (o.dy <= 0 && o.dx < 0 && 10000 * dy / dx < 10000 * o.dy / o.dx)
					return -1;
				return 1;
			}
		}

	}

}
