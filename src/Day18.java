import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class Day18 {

	public static void main(String[] args) throws IOException {
		boolean part2 = true;
		
		FileInputStream in = new FileInputStream(new File("data/data18.txt"));
		Map<Pair, Pair> map = new TreeMap<Pair, Pair>();
		int x = 0;
		int y = 0;
		Pair center = null;
		while (in.available() > 0) {
			char c = (char) in.read();
			if (c == '\n') {
				y++;
				x = 0;
			} else {
				if (c != '#') {
					Pair p = new Pair(x, y);
					if (Character.getType(c) == Character.LOWERCASE_LETTER)
						p.key = c;
					else if (Character.getType(c) == Character.UPPERCASE_LETTER)
						p.door = Character.toLowerCase(c);
					else if (c == '@') {
						p.key = '@';
						center = p;
					}
					map.put(p, p);
				}
				x++;
			}
		}
		in.close();

		// This is optional, but should speed up things a bit
		while (true) {
			List<Pair> deadEnds = new ArrayList<Pair>();
			for (Pair p : map.keySet()) {
				int nc = 0;
				for (Pair n : p.potentialNeighbours()) {
					if (map.containsKey(n))
						nc++;
				}
				if (nc == 1 && map.get(p).key == null)
					deadEnds.add(p);
			}
			if (deadEnds.size() == 0)
				break;
			for (Pair p : deadEnds)
				map.remove(p);
		}

		if(!part2) {
			connectMap(map);		
			Set<Character> keySet = getKeySet(map);
			// 4228 is the answer to first part
			System.out.println(findPath('@', getDistancePairs(map, keySet, getKeyPositions(map)), keySet, new TreeMap<String, Integer>()));
		}else{
			map.remove(center);
			map.remove(new Pair(center.x-1, center.y));
			map.remove(new Pair(center.x+1, center.y));
			map.remove(new Pair(center.x, center.y-1));
			map.remove(new Pair(center.x, center.y+1));
			
			List<Map<Pair,Pair>> maps = new ArrayList<Map<Pair,Pair>>();
			maps.add(new TreeMap<Pair,Pair>());
			maps.add(new TreeMap<Pair,Pair>());
			maps.add(new TreeMap<Pair,Pair>());
			maps.add(new TreeMap<Pair,Pair>());
			
			for(Pair p : map.keySet()) {
				int mapIndex = -1;
				if(p.x < center.x && p.y < center.y) mapIndex = 0;
				else if(p.x > center.x && p.y < center.y) mapIndex = 1;
				else if(p.x > center.x && p.y > center.y) mapIndex = 2;
				else if(p.x < center.x && p.y > center.y) mapIndex = 3;
				maps.get(mapIndex).put(p,p);
			}
			Pair c0 = new Pair(center.x-1,center.y-1);
			c0.key = '@';
			Pair c1 = new Pair(center.x+1,center.y-1);
			c1.key = '@';
			Pair c2 = new Pair(center.x+1,center.y+1);
			c2.key = '@';
			Pair c3 = new Pair(center.x-1,center.y+1);
			c3.key = '@';
			maps.get(0).put(c0, c0);
			maps.get(1).put(c1, c1);
			maps.get(2).put(c2, c2);
			maps.get(3).put(c3, c3);
			
			int costs = 0;
			for(Map<Pair,Pair> m : maps) {
				connectMap(m);
				removeOtherDoors(m);
				Set<Character> ks = getKeySet(m);
				costs += findPath('@', getDistancePairs(m, ks, getKeyPositions(m)), ks, new TreeMap<String, Integer>());				
			}
			// 1858 is the answer to the second part
			System.out.println(costs);
		}

	}

	private static void removeOtherDoors(Map<Pair,Pair> map) {
		Set<Character> keys = getKeySet(map);
		for (Pair p : map.keySet()) {
			p = map.get(p);
			if(p.door != null && !keys.contains(p.door)) p.door = null;
		}		
	}
	
	private static void connectMap(Map<Pair,Pair> map) {
		for (Pair p : map.keySet()) {
			p = map.get(p);
			for (Pair n : p.potentialNeighbours())
				if (map.containsKey(n))
					p.neighbours.add(map.get(n));
		}		
	}
	
	private static Set<Character> getKeySet(Map<Pair,Pair> map) {
		Set<Character> result = new TreeSet<Character>();
		for(Pair p : map.keySet()) {
			p = map.get(p);
			if(p.key != null) result.add(p.key);
		}
		return result;
	}
	
	private static Map<Character, Pair> getKeyPositions(Map<Pair,Pair> map) {
		Map<Character, Pair> result = new TreeMap<Character, Pair>();

		for (Pair p : map.keySet()) {
			p = map.get(p);
			if (p.key != null) 
				result.put(p.key, p);
		}
		return result;
	}
	
	private static Map<String,TwoPath> getDistancePairs(Map<Pair,Pair> map, Set<Character> keySet, Map<Character, Pair> keyPositions) {
		Map<String, TwoPath> result = new TreeMap<String, TwoPath>();
		Character[] keyCharacters = keySet.toArray(new Character[keySet.size()]);

		for (int firstKey = 0; firstKey < keyCharacters.length; firstKey++) {
			for (int secondKey = firstKey + 1; secondKey < keyCharacters.length; secondKey++) {
				Character fromKey = keyCharacters[firstKey];
				Character toKey = keyCharacters[secondKey];

				SearchState start = new SearchState();
				start.node = map.get(keyPositions.get(fromKey));
				start.cost = 0;
				start.visited = new TreeSet<Pair>();
				start.doors = new TreeSet<Character>();
				List<SearchState> searchQueue = new LinkedList<SearchState>();
				searchQueue.add(start);
				int minCost = 0;
				int queueIndex = 0;
				Set<Character> minDoors = null;
				while (queueIndex < searchQueue.size()) {
					SearchState current = searchQueue.get(queueIndex);
					if (current.node.key != null && current.node.key.equals(toKey)) {
						minDoors = current.doors;
						minCost = current.cost;
						break;
					}
					Set<Pair> visited = current.visited;
					Set<Character> doors = current.doors;
					visited.add(current.node);
					if (current.node.door != null)
						doors.add(current.node.door);
					for (Pair next : current.node.neighbours) {
						if (next == null || visited.contains(next))
							continue;
						SearchState nextState = new SearchState();
						nextState.cost = current.cost + 1;
						nextState.visited = new TreeSet<Pair>(visited);
						nextState.doors = new TreeSet<Character>(doors);
						nextState.node = next;
						searchQueue.add(nextState);
					}
					queueIndex++;
				}
				String fromtoKey = String.valueOf(new char[] {fromKey, toKey});
				TwoPath tp = new TwoPath();
				tp.distance = minCost;
				tp.doors = minDoors;
				result.put(fromtoKey, tp);
			}
		}		
		return result;
	}
	
	private static class TwoPath {
		int distance;
		Set<Character> doors = new TreeSet<Character>();
	}

	private static class SearchState {
		Pair node;
		int cost;
		Set<Pair> visited;
		Set<Character> doors;
	}

	private static int findPath(Character currentKey, Map<String, TwoPath> paths, Set<Character> remainingKeys,
			Map<String, Integer> cache) {

		remainingKeys.remove(currentKey);

		if (remainingKeys.isEmpty())
			return 0;

		String cachedKey = String.valueOf(currentKey);
		for(Character c : remainingKeys)
			cachedKey += String.valueOf(c);
		Integer cachedCost = cache.get(cachedKey);
		if (cachedCost != null)
			return cachedCost;

		int minCost = Integer.MAX_VALUE;
		for (Character nextKey : remainingKeys) {			
			TwoPath nextPath = paths.get(currentKey < nextKey ? String.valueOf(new char[] {currentKey, nextKey}) : String.valueOf(new char[] {nextKey, currentKey}));
			boolean doorReachable = true;
			for (Character door : nextPath.doors) {
				if (remainingKeys.contains(door)) {
					doorReachable = false;
					break;
				}
			}
			if (!doorReachable)
				continue;
			int nextPathCost = nextPath.distance + findPath(nextKey, paths, new TreeSet<Character>(remainingKeys), cache);
			if (minCost > nextPathCost)
				minCost = nextPathCost;
		}
		cache.put(cachedKey, minCost);
		return minCost;
	}

	private static class Pair implements Comparable<Pair> {
		int x, y;
		Character key = null;
		Character door = null;
		List<Pair> neighbours = new ArrayList<Pair>();

		Pair(int x, int y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public boolean equals(Object other) {
			return compareTo((Pair) other) == 0;
		}

		Pair[] potentialNeighbours() {
			return new Pair[] { new Pair(x, y - 1), new Pair(x + 1, y), new Pair(x, y + 1), new Pair(x - 1, y) };
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

}
