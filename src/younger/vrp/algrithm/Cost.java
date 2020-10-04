package younger.vrp.algrithm;

public class Cost {
	public double total;
	public double cost;
	public double time;
	public double load;

	public double loadVio;
	public double timeVio;
	public int nodeNumVio;

	public Cost() {
		total = 0;
		cost = 0;
		load = 0;
		time = 0;

		loadVio = 0;
		timeVio = 0;
		nodeNumVio = 0;
	}

	public Cost(Cost cost) {
		this.total = cost.total;
		this.cost = cost.cost;
		this.load = cost.load;
		this.time = cost.time;

		this.loadVio = cost.loadVio;
		this.timeVio = cost.timeVio;
		this.nodeNumVio = cost.nodeNumVio;
	}

	@Override
	public String toString() {
		String result = String.format(
				"[ total=%.1f, cost=%.1f, load=%.1f, time=%.1f, customerNum violation=%d, time windows violation=%.1f, load violation=%.1f",
				total, cost, load, time, nodeNumVio, timeVio, loadVio);
		return result;
	}

	/**
	 * Set the total cost based on alpha, beta
	 * 
	 * @param alpha
	 * @param beta
	 */
	public void calculateTotalCost(double alpha, double beta) {
		total = cost + alpha * loadVio + beta * timeVio;
	}

	public void calculateTotalCost(double alpha, double beta, double gamma) {
		total = cost + alpha * loadVio + beta * timeVio + gamma * nodeNumVio;
	}

	public void calculateTotalCost() {
		total = cost + loadVio + timeVio;
	}

	public void setLoadViol(double capacityviol) {
		this.loadVio = capacityviol;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public double getTotal() {
		return total;
	}

	public double getLoadViolation() {
		return loadVio;
	}

	public double getTimeViolation() {
		return timeVio;
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
		this.timeVio = timeViolation;
	}
}
