package younger.vrp.instance;

public class Expense extends Cost {
    private double price;

    public Expense() {
        super();
        this.price = 0;
    }

    public Expense(Expense expense) {
        super(expense);
        this.price = expense.price;
    }

    /**
     * Set the total money of the system
     * <p>1 car , 400 yuan , 6000 meter</p>
     */
    public void total_to_fare() {
        total *= 400 / 6000.;
        total += price;
    }

    public void total_to_arc() {
        total -= price;
        total /= 400 / 6000.;
    }

    @Override
    public String toString() {
        String result = String.format(
                "[ total=%.1f, price=%.1f | distance=%.1f, arc=%.1f | time=%.1f, time_vio=%.1f | load=%.0f, load_vio=%.0f | nodeNum_vio=%d ]",
                total, price, dist, arc, time, timeVio, load, loadVio, nodeNumVio);
        return result;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
