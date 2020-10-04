package younger.vrp.alns.config;

public enum VRPCatatory {

    CVRPTW(true), OVRPTW(false);

    private boolean addDepotToEnd;

    VRPCatatory(boolean addDepotToEnd) {
        this.setAddDepotToEnd(addDepotToEnd);
    }

    public boolean isAddDepotToEnd() {
        return addDepotToEnd;
    }

    private void setAddDepotToEnd(boolean addDepotToEnd) {
        this.addDepotToEnd = addDepotToEnd;
    }

}