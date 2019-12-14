import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.List;
import java.util.TreeMap;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class Day14 {

	private static Map<String, List<Spec>> reactions = new TreeMap<String, List<Spec>>();
	private static Map<String, Integer> amounts = new TreeMap<String, Integer>();

	private static Map<String, Long> requirements = new TreeMap<String, Long>();

	private static void updateRequirement(String name, long amount) {
		if (requirements.containsKey(name))
			requirements.put(name, requirements.get(name) + amount);
		else
			requirements.put(name, amount);
	}

	private static void addRequirement(String name, long amount) {
		if (name.equals("ORE"))
			return;
		int specAmount = amounts.get(name);
		long rep = amount / specAmount;
		if (amount % specAmount != 0)
			rep++;
		updateRequirement(name, -rep * specAmount);

		for (Spec s : reactions.get(name)) {
			updateRequirement(s.name, rep * s.amount);
			long needed = requirements.get(s.name);
			if (needed > 0)
				addRequirement(s.name, needed);
		}
	}

	public static void main(String[] args) throws FileNotFoundException {
		boolean part2 = true;

		Scanner scanner = new Scanner(new File("data/data14.txt"));
		while (scanner.hasNext()) {
			StringTokenizer st1 = new StringTokenizer(scanner.nextLine(), "=>");
			StringTokenizer st2 = new StringTokenizer(st1.nextToken().trim(), ",");
			List<Spec> req = new ArrayList<Spec>();
			while (st2.hasMoreTokens()) {
				req.add(Spec.fromString(st2.nextToken()));
			}
			Spec s = Spec.fromString(st1.nextToken());
			reactions.put(s.name, req);
			amounts.put(s.name, s.amount);
		}
		scanner.close();
		addRequirement("FUEL", 1);
		long orePart1 = requirements.get("ORE");
		if (!part2) {
			System.out.println(orePart1);
		} else {
			long oreLimit = 1000000000000L;
			long fuel = oreLimit / orePart1;
			while (true) {
				requirements.clear();
				addRequirement("FUEL", fuel);
				long inc = (oreLimit - requirements.get("ORE")) / orePart1;
				if (inc == 0L)
					break;
				fuel += inc;
			}
			System.out.println(fuel);
		}
	}

	private static class Spec {
		private String name;
		private Integer amount;

		Spec(String name, Integer amount) {
			this.name = name;
			this.amount = amount;
		}

		static Spec fromString(String spec) {
			StringTokenizer st = new StringTokenizer(spec.trim(), " ");
			int num = Integer.parseInt(st.nextToken());
			String name = st.nextToken();
			return new Spec(name, num);
		}
	}
}
