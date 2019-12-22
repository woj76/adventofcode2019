import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Day22 {

	private static BigInteger dealNewPosition(BigInteger deckSize, BigInteger position, boolean reverse) {
		return deckSize.subtract(BigInteger.ONE).subtract(position);
	}

	private static BigInteger cutNPosition(BigInteger deckSize, BigInteger position, BigInteger n, boolean reverse) {
		BigInteger result = null;
		if (reverse) {
			result = position.add(n);
		} else {
			result = position.subtract(n);
		}
		if (result.compareTo(deckSize) > 0)
			result = result.subtract(deckSize);
		if (result.compareTo(BigInteger.ZERO) < 0)
			result = result.add(deckSize);
		return result;
	}

	private static BigInteger dealNPosition(BigInteger deckSize, BigInteger position, BigInteger n, boolean reverse) {
		if (reverse) {
			return position.multiply(n.modInverse(deckSize)).mod(deckSize);
		} else {
			return position.multiply(n).mod(deckSize);
		}
	}

	public static void main(String[] args) throws FileNotFoundException {
		boolean part2 = true;

		List<Integer> shuffles = new ArrayList<Integer>();
		List<BigInteger> params = new ArrayList<BigInteger>();
		Scanner scanner = new Scanner(new File("data/data22.txt"));
		while (scanner.hasNext()) {
			String sCmd = scanner.nextLine();
			if (sCmd.startsWith("deal into new stack")) {
				shuffles.add(0);
				params.add(BigInteger.valueOf(2));
			} else if (sCmd.startsWith("deal with increment ")) {
				int n = Integer.parseInt(sCmd.substring("deal with increment ".length()));
				shuffles.add(1);
				params.add(BigInteger.valueOf(n));
			} else if (sCmd.startsWith("cut ")) {
				int n = Integer.parseInt(sCmd.substring("cut ".length()));
				shuffles.add(2);
				params.add(BigInteger.valueOf(n));
			}
		}
		scanner.close();
		
		BigInteger deckSize = part2 ? new BigInteger("119315717514047") : BigInteger.valueOf(10007);
		BigInteger position = part2 ? BigInteger.valueOf(2020) : BigInteger.valueOf(2019);
		
		if (!part2) {
			for (int s = 0; s < shuffles.size(); s++) {
				switch (shuffles.get(s)) {
				case 0:
					position = dealNewPosition(deckSize, position, false);
					break;
				case 1:
					position = dealNPosition(deckSize, position, params.get(s), false);
					break;
				case 2:
					position = cutNPosition(deckSize, position, params.get(s), false);
					break;
				}
			}
			System.out.println(position);
		} else {
			BigInteger shift0 = BigInteger.ZERO;
			BigInteger shift1 = BigInteger.ONE;
			for (int s = shuffles.size()-1; s >= 0; s--) {
				switch (shuffles.get(s)) {
				case 0:
					shift0 = dealNewPosition(deckSize, shift0, true);
					shift1 = dealNewPosition(deckSize, shift1, true);
					break;
				case 1:
					shift0 = dealNPosition(deckSize, shift0, params.get(s), true);
					shift1 = dealNPosition(deckSize, shift1, params.get(s), true);
					break;
				case 2:
					shift0 = cutNPosition(deckSize, shift0, params.get(s), true);
					shift1 = cutNPosition(deckSize, shift1, params.get(s), true);
					break;
				}
			}
			position = BigInteger.valueOf(2020);
			BigInteger rep = new BigInteger("101741582076661");
			Pair ab = new Pair();
			ab.b = shift0.mod(deckSize);
			ab.a = shift1.subtract(shift0).mod(deckSize);
			ab = calcMany(ab, rep, deckSize);
			System.out.println(position.multiply(ab.a).mod(deckSize).add(ab.b).mod(deckSize));
		}
	}
	
	private static Pair calcMany(Pair ab, BigInteger rep, BigInteger mod) {
		if(rep.equals(BigInteger.ONE))
			return ab;
		if(rep.mod(BigInteger.valueOf(2)).equals(BigInteger.ZERO)) {
			BigInteger newA = ab.a.multiply(ab.a).mod(mod);
			BigInteger newB = ab.a.multiply(ab.b).add(ab.b).mod(mod);
			ab.a = newA;
			ab.b = newB;
			return calcMany(ab, rep.divide(BigInteger.valueOf(2)), mod);
		}else {
			BigInteger a = ab.a;
			BigInteger b = ab.b;
			Pair cd = calcMany(ab, rep.subtract(BigInteger.ONE), mod);
			BigInteger newA = a.multiply(cd.a).mod(mod);
			BigInteger newB = a.multiply(cd.b).add(b).mod(mod);
			ab.a = newA;
			ab.b = newB;
			return ab;
		}
	}

	private static class Pair {
		BigInteger a, b;
	}

}
