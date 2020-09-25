package zll.vrptw.algrithm;

public class Cost {
	public double total;
	public double cost;
	public double time;
	public double load;

	public double loadViolation;
	public double timeViolation;
	public int maxCustomerNumViolation;

	public Cost() {
		total = 0;
		cost = 0;
		load = 0;
		time = 0;

		loadViolation = 0;
		timeViolation = 0;
		maxCustomerNumViolation = 0;
	}

	public Cost(Cost cost) {
		this.total = cost.total;
		this.cost = cost.cost;
		this.load = cost.load;
		this.time = cost.time;

		this.loadViolation = cost.loadViolation;
		this.timeViolation = cost.timeViolation;
		this.maxCustomerNumViolation = cost.maxCustomerNumViolation;
	}

	@Override
	public String toString() {
		String result = "[ total=" + total + ", cost=" + cost + ", load=" + load + ", time=" + time
				+ ", customerNum violation=" + maxCustomerNumViolation + ", time windows violation=" + timeViolation
				+ ", load violation=" + loadViolation;
		return result + " ]";
	}

	/**
	 * Set the total cost based on alpha, beta
	 * 
	 * @param alpha
	 * @param beta
	 */
	public void calculateTotalCost(double alpha, double beta) {
		total = cost + alpha * loadViolation + beta * timeViolation;
	}

	public void calculateTotalCost(double alpha, double beta, double gamma) {
		total = cost + alpha * loadViolation + beta * timeViolation + gamma * maxCustomerNumViolation;
	}

	public void calculateTotalCost() {
		total = cost + loadViolation + timeViolation;
	}

	public void setLoadViol(double capacityviol) {
		this.loadViolation = capacityviol;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public double getTotal() {
		return total;
	}

	public double getLoadViolation() {
		return loadViolation;
	}

	public double getTimeViolation() {
		return timeViolation;
	}

	public double getLoad() {
		return load;
	}

	public void setLoad(double load) {
		this.load = load;
	}

	public double getCost() {
		return cost;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	public void setTimeViolation(double timeViolation) {
		this.timeViolation = timeViolation;
	}
}
