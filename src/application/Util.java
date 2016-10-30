/*******************************************************          
* Name: DiamondApp                                     *
* Date: Summer 2016                                    *
*                                                      *   
* Author: Bastian, Taylor                              *   
*                                                      *   
* Purpose:  Demonstration of 3d Diamond Tiling         *
*                                                      *                                                                                               
*******************************************************/

package application;

public class Util {

	private int i;
	private int j;
	private int t;
	private int thyme;
	private int k1;
	private int k2;
	private int tau = 12;
	private int upper_bound_T = 10;
	private int lower_bound_i = 1;
	private int lower_bound_j = 1;
	private int upper_bound_i = 16;
	private int upper_bound_j = 16;
	private String data="";

	public void generateData() {
        this.data="";
		for (thyme = (int) ceild(3, tau) - 3; thyme <= intDiv(3 * upper_bound_T, tau); thyme++) {
			// The next two loops iterate within a tile wavefront.
			int k1_lb = (int) ceild((double) (3 * lower_bound_j + 2 + (thyme - 2) * tau), (double) (tau * 3));
			int k1_ub = (int) intDiv((double) (3 * upper_bound_j + (thyme + 2) * tau), (double) (tau * 3));

			int k2_lb = (int) intDiv((2 * thyme - 2) * tau - 3 * upper_bound_i + 2, tau * 3);
			int k2_ub = (int) intDiv((2 + 2 * thyme) * tau - 2 - 3 * lower_bound_i, tau * 3);

			for (int x = k2_lb; x <= k2_ub; x++) {
				for (k1 = k1_lb; k1 <= k1_ub; k1++) {
					k2 = x - k1;

					// Loop over time within a tile.
					for (t = (int) Math.max(1, intDiv(thyme * tau - 1, 3) + 1); t < Math.min(upper_bound_T + 1,
							tau + intDiv(thyme * tau, 3)); t++) {
						// Loops over spatial dimensions within tile.
						for (i = Math.max(lower_bound_i,
								Math.max(-2 * tau - k1 * tau - k2 * tau + 2 * t + 2,
										(thyme - k1 - k2) * tau - t)); i <= Math.min(upper_bound_i,
												Math.min(tau + (thyme - k1 - k2) * tau - t - 1,
														-k1 * tau - k2 * tau + 2 * t)); i += 1) {
							for (j = Math.max(lower_bound_j,
									Math.max(k1 * tau - t, -tau - k2 * tau + t - i + 1)); j <= Math.min(upper_bound_j,
											Math.min(tau + k1 * tau - t - 1, -k2 * tau + t - i)); j += 1) {
								this.data+=("~"+t+","+i+","+j+"\n");
							} // for j
						} // for i
					} // for t
				} // for k2
			} // for k1
			//Add color change to string
			this.data+=("~\n");
		} // for thyme
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public int getTau() {
		return tau;
	}

	public void setTau(int tau) {
		this.tau = tau;
	}

	public int getUpper_bound_T() {
		return upper_bound_T;
	}

	public void setUpper_bound_T(int upper_bound_T) {
		this.upper_bound_T = upper_bound_T;
	}

	public int getUpper_bound_i() {
		return upper_bound_i;
	}

	public void setUpper_bound_i(int upper_bound_i) {
		this.upper_bound_i = upper_bound_i;
	}

	public int getUpper_bound_j() {
		return upper_bound_j;
	}

	public void setUpper_bound_j(int upper_bound_j) {
		this.upper_bound_j = upper_bound_j;
	}

	private double intDiv(double i, double j) {
		if ((i) % (j) >= 0) {
			return i / j;
		} else {
			return ((i) / (j)) - 1;
		}
	}

	private double intMod(double i, double j) {
		if ((i) % (j) >= 0) {
			return i % j;
		} else {
			return ((i) % (j)) + j;
		}
	}

	private double ceild(double n, double d) {
		if (intMod(n, d) > 0) {
			return intDiv(n, d) + 1;
		} else {
			return intDiv(n, d);
		}
	}
}
