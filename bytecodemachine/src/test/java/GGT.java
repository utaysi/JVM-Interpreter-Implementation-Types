

public class GGT {

	public static void main() {
		int a = 9;
		int b = 6;
		while (a != b) {
			if (a > b) {
				a = a - b;
			} else {
				b = b - a;
			}
		}
		int ergebnis = a;
	}

}
