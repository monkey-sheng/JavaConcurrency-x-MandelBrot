package concurrency;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class LessBlockageThreading
{
    // Determine the position and zoom level of the image of the Mandelbrot set
    private static final double X_CENTRE   = 0;
    private static final double Y_CENTRE   = 0.75;
    
    // this effectively sets the zoom level of the whole image
    private static double MANDELBROT_SIZE = 1;
    
    public static double X_START = X_CENTRE - MANDELBROT_SIZE/2;
    public static double Y_START = Y_CENTRE - MANDELBROT_SIZE/2;
    
    // maximum number of iterations
    public static final int ITERATION_MAX = 255;
    
    // size of the image (the window size)
    public static final int IMAGE_SIZE   = 800;
    public static double SCALE = MANDELBROT_SIZE/IMAGE_SIZE;
    
    public static int BLOCK_SIZE = IMAGE_SIZE / 10;
    
    public JFrame frame;
    public BufferedImage image;
    public Graphics2D graphics;
    
    // hold the data needed for plotting
    private int[][] plotData = new int[IMAGE_SIZE][IMAGE_SIZE];
    private double[][][] positionData = new double[IMAGE_SIZE][IMAGE_SIZE][2];
    
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
    
    
    public LessBlockageThreading() {
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
                // frame.repaint();
            }
        }
    }
    
    List<Future<?>> futures = new ArrayList<>();
    public void run()
    {
        ExecutorService threads = Executors.newFixedThreadPool(10);
            
        for (int i = 0; i < IMAGE_SIZE; i += BLOCK_SIZE)
        {
            for (int j = 0; j < IMAGE_SIZE; j += BLOCK_SIZE)
            {
                Future<?> f = threads.submit(new PlotBlockJob(i, j, BLOCK_SIZE, SCALE, ITERATION_MAX));
                futures.add(f);
            }
        }

        boolean allDone = false;
        while (!allDone)
        {
            allDone = true;
            for (var future : futures)
            {
                if (!(future.isDone()))
                {
                    allDone = false;
                    break;
                }
            }
        }

//        threads.shutdown();
//        // await termination, then repaint
//        while (true)
//        {
//            try
//            {
//                if (threads.awaitTermination(1000, TimeUnit.MILLISECONDS))
//                    break;
//            }
//            catch (InterruptedException e)
//            {
//                e.printStackTrace();
//            }
//        }

        for (int i = 0; i < IMAGE_SIZE; i++)
        {
            for (int j = 0; j < IMAGE_SIZE; j++)
            {
                int gray = plotData[i][j];
                graphics.setColor(new Color(gray, gray, gray));
                double[] position = positionData[i][j];
                graphics.fill(new Rectangle2D.Double(position[0], position[1], SCALE, SCALE));
            }
        }


        frame.repaint();
    }
    
    public static void main(String[] args)  {
        long start = System.currentTimeMillis();
        new LessBlockageThreading().run();
//        for (double i = 1; i > 0.01 ; i-=0.05)
//        {
//            var plot = new LessBlockageThreading();
//            plot.MANDELBROT_SIZE = i;
//            plot.run();
//        }
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }
    
    class PlotBlockJob implements Runnable
    {
        double xStart; double yStart; int blockSize;
        double scale; int iterationMax;
        
        public PlotBlockJob(double xStart, double yStart, int blockSize,
                            double scale, int iterationMax)
        {
            this.xStart = xStart;  this.yStart = yStart;  this.blockSize = blockSize;
            this.scale = scale;  this.iterationMax = iterationMax;
        }
        @Override
        public void run()
        {
            for (int i = 0; i < blockSize; i++)
            {
                for (int j = 0; j < blockSize; j++)
                {
                    int xPos = (int)xStart + i;
                    int yPos = (int)yStart + j;
                    double x0 = X_START + scale * (xPos);
                    double y0 = Y_START + scale * (yPos);
                    int gray = iterationMax - mandelbrot(x0, y0, iterationMax);
                    //Color color = new Color(gray, gray, gray);
                    
                    plotData[xPos][yPos] = gray;
                    positionData[xPos][yPos] = new double[]{x0, y0};
                }
            }
        }
    }
}
