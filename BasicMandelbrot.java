package concurrency;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class BasicMandelbrot
{
    // Determine the position and zoom level of the image of the Mandelbrot set
    private static final double X_CENTRE   = 0;
    private static final double Y_CENTRE   = 0.75;
    
    // this effectively sets the zoom level of the whole image
    private static final double MANDELBROT_SIZE = 1;
    
    public static final double X_START = X_CENTRE - MANDELBROT_SIZE/2;
    public static final double Y_START = Y_CENTRE - MANDELBROT_SIZE/2;
    
    // maximum number of iterations
    public static final int ITERATION_MAX = 255;
    
    // size of the image (the window size)
    public static final int IMAGE_SIZE   = 1600;
    public static final double SCALE = MANDELBROT_SIZE/IMAGE_SIZE;
    
    public JFrame frame;
    public BufferedImage image;
    public Graphics2D graphics;
    
    // return number of iterations to check if c = x + iy is in Mandelbrot set
    public static int mandelbrot(double x0, double y0, int maxIterations) {
        double zx = x0;
        double zy = y0;
        for (int t = 0; t < maxIterations; t++) {
            if (zx * zx + zy * zy > 4.0) {
                return t;
            }
            double tmp = zx * zx - zy * zy + x0;
            zy = 2.0 * zx * zy + y0;
            zx = tmp;
        }
        return maxIterations;
    }
    
    
    public BasicMandelbrot() {
        // Set up the graphics to display the image
        frame = new JFrame("Mandelbrot");
        image  = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_INT_RGB);
        graphics  = image.createGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, IMAGE_SIZE, IMAGE_SIZE);
        ImageIcon icon = new ImageIcon(image);
        JLabel draw = new JLabel(icon);
        
        frame.setContentPane(draw);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        
        graphics.scale(1/SCALE, 1/SCALE);
        graphics.translate(-X_START, -Y_START);
    }
    
    public void run() {
        for (int i = 0; i < IMAGE_SIZE; i++) {
            for (int j = 0; j < IMAGE_SIZE; j++) {
                double x0 = X_START + SCALE * i;
                double y0 = Y_START + SCALE * j;
                int gray = ITERATION_MAX - mandelbrot(x0, y0, ITERATION_MAX);
                
                // TODO do some colour mapping here if needed
                
                Color color = new Color(gray, gray, gray);
                graphics.setColor(color);
                graphics.fill(new Rectangle2D.Double(x0, y0, SCALE, SCALE));
                // TODO might wanna do repaint outside of the outer for loop
                // frame.repaint();
            }
        }
        frame.repaint();
    }
    
    public static void main(String[] args)  {
        long start = System.currentTimeMillis();
        new BasicMandelbrot().run();
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }
}
