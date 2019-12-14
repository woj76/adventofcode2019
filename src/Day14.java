import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.List;
import java.util.TreeMap;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class Day14 {

	private static Map<String,List<Spec>> reactions = new TreeMap<String,List<Spec>>();
	private static Map<String,Integer> amounts = new TreeMap<String,Integer>();
	
	private static Map<String, Long> requirements  = new TreeMap<String,Long>();
	
	private static void updateRequirement(String name, long amount) {
		if(requirements.containsKey(name)) 
		    requirements.put(name, requirements.get(name)+amount);
		else
		   requirements.put(name, amount);		
	}
	
	private static void addRequirement(String name, long amount) {
		if(name.equals("ORE")) {
			return;
		}
		int specAmount = amounts.get(name);
		long rep = amount / specAmount;
		if(amount % specAmount != 0) rep++;
		updateRequirement(name, -rep*specAmount);
		
		List<Spec> chemicals = reactions.get(name);
		for(Spec s : chemicals) {
			updateRequirement(s.name, rep*s.amount);
			if(requirements.get(s.name) > 0)
				addRequirement(s.name, requirements.get(s.name));
		}
}
	
	public static void main(String[] args) throws FileNotFoundException {
		boolean part2 = false;

		Scanner scanner = new Scanner(new File("data/data14.txt"));
		while (scanner.hasNext()) {
			StringTokenizer st1 = new StringTokenizer(scanner.nextLine(), "=>");
			StringTokenizer st2 = new StringTokenizer(st1.nextToken().trim(), ",");
			List<Spec> req = new ArrayList<Spec>();
			while(st2.hasMoreTokens()) {
				req.add(Spec.fromString(st2.nextToken()));
			}
			Spec s = Spec.fromString(st1.nextToken());
			reactions.put(s.name, req);
			amounts.put(s.name, s.amount);
		}
		scanner.close();
		if(!part2) {
		addRequirement("FUEL", 1);
		System.out.println(requirements.get("ORE"));
		}else{
			long oreLimit = 1000000000000L;
			long fuel = oreLimit / 136771;
			System.out.println(fuel);
			fuel = 8194491;
			while(true) {
				requirements.clear();
				addRequirement("FUEL", fuel);
				if(requirements.get("ORE") <= oreLimit)
					break;
				fuel--;
			}
			System.out.println(fuel);
		}
//		System.out.println(Integer.MAX_VALUE);
//		System.out.println(1000000000000L);
	}

	
	private static class Spec implements Comparable<Spec> {
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
		
		public boolean equals(Object o) {
			if(o == null || !(o instanceof Spec)) return false;
			return compareTo((Spec)o) == 0;
		}
		
		@Override
		public int compareTo(Spec o) {
			int r = name.compareTo(o.name);
			if(r == 0) {
				return amount.compareTo(o.amount);
			}else
			return r;
		}
	}
}
