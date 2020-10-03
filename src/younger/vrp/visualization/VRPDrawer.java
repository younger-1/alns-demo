package younger.vrp.visualization;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import java.awt.Graphics;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;

import younger.vrp.algrithm.MyALNSSolution;
import younger.vrp.instance.Node;
import younger.vrp.instance.Route;

public class VRPDrawer extends JPanel {
    private static final long serialVersionUID = 1L;
    private double[][] x_routes;
    private double[][] y_routes;
    double x_min;
    double y_min;
    double x_diff;
    double y_diff;
    private final Random r = new Random(1004);
    Color[] routeColor;

    public static void draw_sol(MyALNSSolution sol, int screenPositionIndex) {
        VRPDrawer vd = new VRPDrawer(sol);
        String sol_kind = switch (screenPositionIndex) {
            case 1 -> "Init Solution";
            case 2 -> "Stage-1 Solution";
            case 3 -> "Stage-2 Solution";
            case 4 -> "Optimal Solution";
            default -> "Unknow";
        };
        // Border myBorder = BorderFactory.createMatteBorder(5, 5, 5, 5, Color.CYAN);
        Border myBorder = BorderFactory.createTitledBorder(sol_kind);
        vd.setBorder(myBorder);

        JPanel main_pane = new JPanel();
        main_pane.setOpaque(true);
        main_pane.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        main_pane.setLayout(new BorderLayout(100, 100));
        main_pane.add(vd, BorderLayout.CENTER);

        int colNum = 2;
        int rowNum = 2;
        int xPosition = (screenPositionIndex - 1) % colNum;
        int yPosition = (screenPositionIndex - 1) / colNum;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int frameWidth = (int) screenSize.getWidth() / colNum;
        int frameHeight = (int) screenSize.getHeight() / rowNum;

        JFrame myFrame = new JFrame("ALNS_VRP -- " + sol_kind);
        myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        myFrame.setBounds(xPosition * frameWidth, yPosition * frameHeight, frameWidth, frameHeight);
        myFrame.getContentPane().add(main_pane);
        myFrame.setVisible(true);
    }

    public VRPDrawer(MyALNSSolution sol) {
        List<Route> routes = sol.routes;

        routeColor = new Color[routes.size()];
        for (int i = 0; i < routeColor.length; i++) {
            routeColor[i] = new Color((float) r.nextDouble(), (float) r.nextDouble(), (float) r.nextDouble());
        }

        x_routes = new double[routes.size()][];
        y_routes = new double[routes.size()][];
        int i = 0;
        for (Route route : routes) {
            int j = 0;
            List<Node> nodes = route.getRoute();
            x_routes[i] = new double[nodes.size()];
            y_routes[i] = new double[nodes.size()];
            for (Node node : nodes) {
                double xCord = node.getX();
                double yCord = node.getY();
                x_routes[i][j] = xCord;
                y_routes[i][j] = yCord;
                j += 1;
            }
            i += 1;
        }
        x_min = Arrays.stream(x_routes).map(route -> Arrays.stream(route).reduce(Double::min).getAsDouble())
                .reduce(Double::min).get();
        x_diff = Arrays.stream(x_routes).map(route -> Arrays.stream(route).reduce(0, Double::max)).reduce(0D,
                Double::max) - x_min;
        y_min = Arrays.stream(y_routes).map(route -> Arrays.stream(route).reduce(Double::min).getAsDouble())
                .reduce(Double::min).get();
        y_diff = Arrays.stream(y_routes).map(route -> Arrays.stream(route).reduce(0, Double::max)).reduce(0D,
                Double::max) - y_min;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        int width = this.getWidth();
        int height = this.getHeight();
        int radius = width / 220;
        // int radius = (int) (Math.log(width + 1) / 1.5);

        int valid_route_index = 0;
        for (int i = 0; i < x_routes.length; i++) {
            int[] x_coord = Arrays.stream(x_routes[i])
                    .map((num) -> 1 / 20.0 * width + 18 / 20.0 * width * (num - x_min) / x_diff)
                    .mapToInt(num -> (int) num).toArray();
            int[] y_coord = Arrays.stream(y_routes[i])
                    .map((num) -> 1 / 20.0 * height + 18 / 20.0 * height * (num - y_min) / y_diff)
                    .mapToInt(num -> (int) num).toArray();
            if (x_coord.length > 2) {
                valid_route_index += 1;
                g.setColor(routeColor[i]);
                g.drawPolyline(x_coord, y_coord, x_coord.length);
                for (int j = 0; j < x_coord.length; j++) {
                    g.fillOval(x_coord[j] - radius, y_coord[j] - radius, 2 * radius, 2 * radius);
                }
                g.setColor(new Color(0, 0, 0));
                g.drawString(Integer.toString(valid_route_index), x_coord[1], y_coord[1]);
            }
        }
    }
}
