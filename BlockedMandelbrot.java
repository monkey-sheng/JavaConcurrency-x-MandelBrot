package concurrency;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class BlockedMandelbrot
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
    public static final int IMAGE_SIZE   = 800;
    public static final double SCALE = MANDELBROT_SIZE/IMAGE_SIZE;
    
    // used in plotBlock()
    public static int BLOCK_SIZE = IMAGE_SIZE / 10;
    
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
    
    
    public BlockedMandelbrot() {
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
    
    /**
     * This method will plot a blockSize by blockSize square piece of the image starting at position (xStart, yStart) on the image canvas, with the parameters iterationMax and scale defined as in BasicMandelbrot
     * @param xStart starting x position of the block
     * @param yStart starting y position of the block
     * @param blockSize side length of the block square
     * @param scale same as in BasicMandelbrot
     * @param iterationMax same as in BasicMandelbrot
     */
    public void plotBlock(double xStart, double yStart, int blockSize,
                          double scale, int iterationMax)
    {
        for (int i = 0; i < blockSize; i++)
        {
            for (int j = 0; j < blockSize; j++)
            {
                double x0 = X_START + scale * (xStart + i);
                double y0 = Y_START + scale * (yStart + j);
                int gray = iterationMax - mandelbrot(x0, y0, iterationMax);
                Color color = new Color(gray, gray, gray);
                graphics.setColor(color);
                graphics.fill(new Rectangle2D.Double(x0, y0, scale, scale));
                frame.repaint();
            }
        }
    }
    
    public void run()
    {
        for (int i = 0; i < IMAGE_SIZE; i+=BLOCK_SIZE)
        {
            for (int j = 0; j < IMAGE_SIZE; j+=BLOCK_SIZE)
            {
                plotBlock(i, j, BLOCK_SIZE, SCALE, ITERATION_MAX);
            }
        }
        
    }
    
    public static void main(String[] args)  {
        new BlockedMandelbrot().run();
    }
}
