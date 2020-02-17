package concurrency;

import static org.junit.Assert.assertTrue;

import java.awt.image.BufferedImage;

import org.junit.Test;

public class ThreadedMandelbrotTest {

    @Test
    public void testMain() {
        BasicMandelbrot basicMandelbrot = new BasicMandelbrot();
        basicMandelbrot.run();
        //ThreadedMandelbrot threadedMandelbrot = new ThreadedMandelbrot();
        //threadedMandelbrot.run();
        var lessBlockage = new LessBlockageThreading();
        lessBlockage.run();
        assertTrue(compareImages(basicMandelbrot.image, lessBlockage.image));

        //assertTrue(compareImages(basicMandelbrot.image, threadedMandelbrot.image));
    }

    private boolean compareImages(BufferedImage image1, BufferedImage image2) {
        if (image1.getWidth() != image2.getWidth() || image1.getHeight() != image2.getHeight()) {
            return false;
        }

        int width = image1.getWidth();
        int height = image1.getHeight();
        int badPixelCount = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (image1.getRGB(x, y) != image2.getRGB(x, y)) {
                    // Allow a few bad pixels as we're working with floating point calculations
                    badPixelCount++;
                    if (badPixelCount > 5) {
                        return false;
                    }
                }
            }
        }

        return true;
    }
}
