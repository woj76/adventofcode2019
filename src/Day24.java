import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Day24 {

	private static int cycleLife(boolean[][] map) {
		int[][] counts = new int[5][5];
		for (int y = 0; y < 5; y++)
			for (int x = 0; x < 5; x++) {
				int count = 0;
				if (x - 1 >= 0 && map[x - 1][y])
					count++;
				if (x + 1 < 5 && map[x + 1][y])
					count++;
				if (y - 1 >= 0 && map[x][y - 1])
					count++;
				if (y + 1 < 5 && map[x][y + 1])
					count++;
				counts[x][y] = count;
			}
		int diversity = 0;
		int diversityIndex = 0;
		for (int y = 0; y < 5; y++)
			for (int x = 0; x < 5; x++) {
				int count = counts[x][y];
				if (map[x][y] && count != 1)
					map[x][y] = false;
				else if (!map[x][y] && (count == 1 || count == 2))
					map[x][y] = true;
				if (map[x][y])
					diversity |= (1 << diversityIndex);
				diversityIndex++;
			}
		return diversity;
	}
	
	private static void multiCycleLife(List<boolean[][]> maps) {
		List<int[][]> counts = new ArrayList<int[][]>();
		int x=0;
		int y=0;
		for(int i=0;i<maps.size();i++) {
			int[][] count = new int[5][5];
			counts.add(count);
			boolean[][] prev = i==0 ? null : maps.get(i-1);
			boolean[][] current = maps.get(i);
			boolean[][] next = i == maps.size() - 1 ? null : maps.get(i+1);
			for (y = 0; y < 5; y++)
				for (x = 0; x < 5; x++) {
					if(x == 2 && y==2) continue;
					int c = 0;
					if (x - 1 >= 0 && current[x - 1][y])
						c++;
					if (x + 1 < 5 && current[x + 1][y])
						c++;
					if (y - 1 >= 0 && current[x][y - 1])
						c++;
					if (y + 1 < 5 && current[x][y + 1])
						c++;
					if(next != null && x == 0 && next[1][2]) c++;
					if(next != null && x == 4 && next[3][2]) c++;
					if(next != null && y == 0 && next[2][1]) c++;
					if(next != null && y == 4 && next[2][3]) c++;
					count[x][y] = c;
				}
			if(prev != null) {
				int j=0;
				x = 2; y = 1;
				for(j=0;j<5;j++)
					if(prev[j][0]) count[x][y]++;
				y = 3;
				for(j=0;j<5;j++)
					if(prev[j][4]) count[x][y]++;
				x = 1; y = 2;
				for(j=0;j<5;j++)
					if(prev[0][j]) count[x][y]++;
				x = 3;
				for(j=0;j<5;j++)
					if(prev[4][j]) count[x][y]++;
			}
		}
		
		for(int i=0;i<maps.size();i++) {
			boolean[][] current = maps.get(i);
			int[][] count = counts.get(i);
			for (y = 0; y < 5; y++)
				for (x = 0; x < 5; x++) {
					int c = count[x][y];
					if (current[x][y] && c != 1)
						current[x][y] = false;
					else if (!current[x][y] && (c == 1 || c == 2))
						current[x][y] = true;
				}			
		}		
	}

	public static void main(String[] args) throws IOException {
		boolean part2 = true;

		FileInputStream in = new FileInputStream(new File("data/data24.txt"));
		boolean[][] map = new boolean[5][5];

		int x = 0;
		int y = 0;
		while (in.available() > 0) {
			char c = (char) in.read();
			if (c == '\n') {
				y++;
				x = 0;
			} else if (c == '#' || c == '.') {
				map[x][y] = c == '#';
				x++;
			}
		}
		in.close();

		if (!part2) {
			Set<Integer> divers = new TreeSet<Integer>();
			int d = 0;
			while (true) {
				d = cycleLife(map);
				if (divers.contains(d)) {
					break;
				}
				divers.add(d);
			}
			System.out.println(d);
		} else {
			List<boolean[][]> maps = new ArrayList<boolean[][]>();
			maps.add(map);
			int r = 0;
			while(r < 200) {
				maps.add(0, new boolean[5][5]);
				maps.add(new boolean[5][5]);
				multiCycleLife(maps);
				r++;
			}
			int bugCount = 0;
			for(boolean[][] m : maps)
				for(y=0;y<5;y++)
					for(x=0; x<5; x++)
						if(m[x][y]) bugCount++;

			System.out.println(bugCount);
		}
	}
}
