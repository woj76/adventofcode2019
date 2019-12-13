public class Day04 {

	public static void main(String[] args) {
		boolean part2 = true;

		int count = 0;

		for (int pw = 382345; pw <= 843167; pw++) {
			String s = "" + pw;

			boolean pair = false;
			boolean decreasing = false;

			for (int i = 1; i < s.length(); i++) {
				char c1 = s.charAt(i - 1);
				char c2 = s.charAt(i);
				if (c1 == c2) {
					if (!part2
							|| ((i - 2 < 0 || s.charAt(i - 2) != c1) && (i + 1 >= s.length() || s.charAt(i + 1) != c2)))
						pair = true;
				}
				if (c2 < c1)
					decreasing = true;
			}
			if (pair && !decreasing) {
				count++;
			}

		}
		System.out.println(count);
	}

}
