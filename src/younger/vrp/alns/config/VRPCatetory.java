package younger.vrp.alns.config;

public enum VRPCatetory {

    CVRPTW(true), OVRPTW(false);

    private boolean addDepotToEnd;

    VRPCatetory(boolean addDepotToEnd) {
        this.setAddDepotToEnd(addDepotToEnd);
    }

    public boolean isAddDepotToEnd() {
        return addDepotToEnd;
    }

    private void setAddDepotToEnd(boolean addDepotToEnd) {
        this.addDepotToEnd = addDepotToEnd;
    }

}