import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.TreeMap;

public class Day06 {

	private static Map<String, String> orbits = new TreeMap<String, String>();

	private static List<String> getOrbits(String planet) {
		List<String> orbList = new LinkedList<String>();
		while (true) {
			planet = orbits.get(planet);
			if (planet == null)
				break;
			orbList.add(planet);
		}
		return orbList;
	}

	public static void main(String[] args) throws FileNotFoundException {

		boolean part2 = true;

		Scanner scanner = new Scanner(new File("data/data06.txt"));
		while (scanner.hasNext()) {
			StringTokenizer st = new StringTokenizer(scanner.nextLine(), ")");
			String a = st.nextToken().trim();
			String b = st.nextToken().trim();
			orbits.put(b, a);
		}
		scanner.close();

		if (!part2) {
			int sum = 0;
			for (String planet : orbits.keySet())
				sum += getOrbits(planet).size();
			System.out.println(sum);
		} else {
			int pathLength = Integer.MAX_VALUE;
			int dist1 = 0;
			List<String> sanList = getOrbits("SAN");
			for (String p : getOrbits("YOU")) {
				int dist2 = sanList.indexOf(p);
				if (dist2 != -1) {
					int dist = dist1 + dist2;
					if (dist < pathLength)
						pathLength = dist;
				}
				dist1++;
			}
			System.out.println(pathLength);
		}
	}

}
