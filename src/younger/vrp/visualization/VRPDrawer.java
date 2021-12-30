package younger.vrp.visualization;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

// import java.awt.Graphics2D;
// import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;

import younger.vrp.algrithm.ALNSSolution;
import younger.vrp.alns.config.VRPCategory;
import younger.vrp.instance.Node;
import younger.vrp.instance.Route;

public class VRPDrawer extends JPanel {
    private static final long serialVersionUID = 1L;
    private double[][] x_routes;
    private double[][] y_routes;
    double x_min, y_min;
    double x_diff, y_diff;
    int[] route_id;
    private ArrayList<Integer[]> demands = new ArrayList<>();
    private final Random random = new Random(1004);
    private Color[] routeColor;
    private VRPCategory vrpCate;
    private JFrame myFrame;

    public static VRPDrawer draw_sol(ALNSSolution sol, int screenPositionIndex) {
        VRPDrawer vd = new VRPDrawer(sol);
        String sol_kind = null;
        if (screenPositionIndex == 1) {
            sol_kind = "Init";
        } else if (screenPositionIndex == 2) {
            sol_kind = "Stage-1";
        } else if (screenPositionIndex == 3) {
            sol_kind = "Stage-2";
        } else if (screenPositionIndex == 4) {
            sol_kind = "Optimal";
        }
        sol_kind += String.format(" -- Route(%d), arc(%f)", sol.routes.size(), sol.costs.getArc());
        // sol_kind += String.format("Route(%d), cost(%.3f), colinear(%d)", sol.getNumberOfRoute(), sol.getCost().getDistance(), sol.getCoLineCount());

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

        vd.myFrame = new JFrame(sol_kind);
        vd.myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        vd.myFrame.setBounds(xPosition * frameWidth, yPosition * frameHeight, frameWidth, frameHeight);
        vd.myFrame.getContentPane().add(main_pane);
        vd.myFrame.setVisible(true);

        return vd;
    }

    public VRPDrawer(ALNSSolution sol) {
        this.vrpCate = sol.getVrpCate();
        List<Route> routes = sol.routes;

        routeColor = new Color[routes.size()];
        route_id = new int[routes.size()];
        for (int i = 0; i < routes.size(); i++) {
            routeColor[i] = new Color((float) random.nextDouble(), (float) random.nextDouble(), (float) random.nextDouble());
            route_id[i] = routes.get(i).getId();
        }

        /* Get routes */
        x_routes = new double[routes.size()][];
        y_routes = new double[routes.size()][];
        int iRoute = 0;
        for (Route route : routes) {
            int iNode = 0;
            List<Node> nodes = route.getRoute();
            x_routes[iRoute] = new double[nodes.size()];
            y_routes[iRoute] = new double[nodes.size()];
            for (Node node : nodes) {
                double xCord = node.getX();
                double yCord = node.getY();
                x_routes[iRoute][iNode] = xCord;
                y_routes[iRoute][iNode] = yCord;

                /* Get big demand */
                if (node.getDemand() > 500) {
                    demands.add(new Integer[] { (int) node.getDemand(), iRoute, iNode });
                }
                iNode += 1;
            }
            iRoute += 1;
        }

        x_min = Arrays.stream(x_routes).map(route -> Arrays.stream(route).reduce(360, Double::min))
                .reduce(Double::min).get();
        x_diff = Arrays.stream(x_routes).map(route -> Arrays.stream(route).reduce(0, Double::max)).reduce(0D,
                Double::max) - x_min;
        y_min = Arrays.stream(y_routes).map(route -> Arrays.stream(route).reduce(360, Double::min))
                .reduce(Double::min).get();
        y_diff = Arrays.stream(y_routes).map(route -> Arrays.stream(route).reduce(0, Double::max)).reduce(0D,
                Double::max) - y_min;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        int width = this.getWidth();
        int height = this.getHeight();
        int radius = width / 400;
        // int radius = (int) (Math.log(width + 1) / 1.5);

        /* Draw routes */
        for (int i = 0; i < x_routes.length; i++) {
            int[] x_coord = Arrays.stream(x_routes[i])
                    .map((num) -> 1 / 20.0 * width + 18 / 20.0 * width * (num - x_min) / x_diff)
                    .mapToInt(num -> (int) num).toArray();
            int[] y_coord = Arrays.stream(y_routes[i])
                    .map((num) -> 1 / 20.0 * height + 18 / 20.0 * height * (num - y_min) / y_diff)
                    .map(num -> height - num).mapToInt(num -> (int) num).toArray();

            g.setColor(routeColor[i]);

            if (vrpCate.isAddDepotToEnd()) {
                g.drawPolyline(x_coord, y_coord, x_coord.length);
                for (int j = 0; j < x_coord.length; j++) {
                    g.fillOval(x_coord[j] - radius, y_coord[j] - radius, 2 * radius, 2 * radius);
                }
            } else {
                /* don't draw Goto first and Goback depot */
                g.drawPolyline(Arrays.copyOfRange(x_coord, 1, x_coord.length - 1),
                        Arrays.copyOfRange(y_coord, 1, y_coord.length - 1), x_coord.length - 2);
                for (int j = 1; j < x_coord.length - 1; j++) {
                    g.fillOval(x_coord[j] - radius, y_coord[j] - radius, 2 * radius, 2 * radius);
                }

                // /*  Only draw Goto first */
                // g.drawPolyline(Arrays.copyOfRange(x_coord, 0, x_coord.length - 1),
                //         Arrays.copyOfRange(y_coord, 0, y_coord.length - 1), x_coord.length - 1);
                // for (int j = 0; j < x_coord.length - 1; j++) {
                //     g.fillOval(x_coord[j] - radius, y_coord[j] - radius, 2 * radius, 2 * radius);
                // }
            }

            // g.setColor(new Color(0, 0, 0));
            g.setColor(Color.GRAY);
            // g.setFont(new Font("DejaVuSansMono NF", Font.PLAIN, 15));
            g.setFont(new Font("", Font.PLAIN, 12));
            if (x_coord.length > 2) {
                g.drawString(Integer.toString(route_id[i]), x_coord[1], y_coord[1]);
            }

            /* Draw depot */
            if (0 == i) {
                // Graphics2D g2 = (Graphics2D) g;
                // g2.setColor(Color.RED);
                // g2.setStroke(new BasicStroke(3.0f));
                g.setColor(Color.LIGHT_GRAY);
                g.fillRect(x_coord[i] - radius, y_coord[i] - radius, 2 * radius, 2 * radius);
            }
        }

        /* Draw demands */
        g.setColor(Color.BLACK);
        g.setFont(new Font("", Font.BOLD, 13));
        demands.stream().forEach(d -> {
            int[] x_coord = Arrays.stream(x_routes[d[1]])
                    .map((num) -> 1 / 20.0 * width + 18 / 20.0 * width * (num - x_min) / x_diff)
                    .mapToInt(num -> (int) num).toArray();
            int[] y_coord = Arrays.stream(y_routes[d[1]])
                    .map((num) -> 1 / 20.0 * height + 18 / 20.0 * height * (num - y_min) / y_diff)
                    .map(num -> height - num).mapToInt(num -> (int) num).toArray();
            g.drawString(Integer.toString(d[0]), x_coord[d[2]], y_coord[d[2]]);
        });
    }

    public void saveImage(String title) {
        Container myContainer = this.myFrame.getContentPane();
        BufferedImage bImage = new BufferedImage(myContainer.getWidth(), myContainer.getHeight(), BufferedImage.TYPE_INT_ARGB);
        myContainer.paint(bImage.createGraphics());

        File output = new File(title + ".png");
        File outputDir = output.getParentFile();
        if (!outputDir.exists()) {
            outputDir.mkdir();
        }

        try {
            javax.imageio.ImageIO.write(bImage, "PNG", output);
        } catch (Exception e) {
            System.err.println("## Saving image goes wrong ##");
            System.err.println(e);
        }
    }
}
