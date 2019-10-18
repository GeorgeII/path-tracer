import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Main {
    private static final int imageWidth = 512;
    private static final int imageHeight = 512;
    private static final int numPixels = imageHeight * imageWidth;
    private static final double aspectRatio = (double)imageWidth / (double)imageHeight;
    private static List<Vector> pixels;

    private static final int samplesPerPixel = 10;
    private static final int numBounces = 5;
    private static final double rayBounceEps = 0.001;
    private static final Vector rayMissColor = new Vector(50.0, 50.0, 50.0);

    private static final Vector cameraPos = new Vector(0.0,0.0, -10.0);
    private static final Vector cameraLookAt = new Vector(0.0, 0.0, 0.0);
    private static double nearPlaneDistance = 0.1;
    private static final double cameraVerticalFOV = 40.0 * Math.PI / 180.0;
    private static final double cameraHorizontalFOV = cameraVerticalFOV * aspectRatio;
    private static final double windowTop = Math.tan(cameraVerticalFOV / 2.0) * nearPlaneDistance;
    private static final double windowRight = Math.tan(cameraHorizontalFOV / 2.0) * nearPlaneDistance;

    private static Vector cameraFwd = cameraLookAt.substract(cameraPos).normalize();
    private static Vector cameraRight = new Vector(0.0, 1.0, 0.0).crossProduct(cameraFwd);
    private static Vector cameraUp = cameraFwd.crossProduct(cameraRight);

    private static List<Sphere> spheres;
    private static List<Quad> quads;

    private static void initializeScenes() {
        pixels = new ArrayList<>();
        for (int i = 0; i < numPixels; i++) {
            pixels.add(new Vector());
        }

        spheres = new ArrayList<>();
        quads = new ArrayList<>();

        // light
        spheres.add(new Sphere(
                new Vector(4.0, 4.0, 6.0),
                0.5,
                new Vector(100.0, 100.0, 100.0),
                new Vector(0.0, 0.0, 0.0)
                )
        );

        // ball
        spheres.add(new Sphere(
                new Vector(0.0, 0.0, 4.0),
                2.0,
                new Vector(0.0, 0.0, 0.0),
                new Vector(0.5, 0.5, 0.5)
                )
        );

        // floor
        quads.add(new Quad(
                new Vector(-4.0, -3.0, -4.0),
                new Vector(-4.0, 2.0, -4.0),
                new Vector(-4.0, 2.0, 12.0),
                new Vector(-4.0, -3.0, 12.0),
                new Vector(0.0, 0.0, 0.0),
                new Vector(0.1, 0.9, 0.1)
                )
        );

        // green wall
        quads.add(new Quad(
                new Vector(-15.0, -2.0, 15.0),
                new Vector(15.0, -2.0, 15.0),
                new Vector(15.0, -2.0, -15.0),
                new Vector(-15.0, -2.0, -15.0),
                new Vector(0.0, 0.0, 0.0),
                new Vector(0.9, 0.1, 0.1)
                )
        );
    }

    private boolean closestIntersection(Vector rayPos, Vector rayDir, RayHitInfo info) {
        boolean ret = false;
        for (Sphere s : spheres) {
            ret |= s.isIntersect(rayPos, rayDir, info);
        }
        for (Quad q : quads) {
            ret |= q.isIntersect(rayPos, rayDir, info);
        }

        return ret;
    }



    private Vector L_out(RayHitInfo info, Vector outDir, int bouncesLeft) {
        if (bouncesLeft == 0)
            return rayMissColor;

        final Material material = info.getMaterial();

        Vector ret = material.getEmissive();

        // add random samples
        Vector newRayDir = info.getSurfaceNormal().cosineSampleHemisphere();
        RayHitInfo newRayInfo = new RayHitInfo(); // ??? debatable decision ???
        if (closestIntersection(info.getIntersectionPoint().add(newRayDir.multiply(rayBounceEps)), newRayDir, newRayInfo)) {
            ret = ret.add(L_out(newRayInfo, newRayDir.inverse(), bouncesLeft-1).multiply(material.getDiffuse()));
        }
        else {
            ret = ret.add(rayMissColor.multiply(material.getDiffuse()));
        }

        //System.out.println(ret.x + " " + ret.y + " " + ret.z);
        return ret;
    }

    private Vector L_in(Vector rayPos, Vector rayDir) {
        RayHitInfo info = new RayHitInfo();
        if (!closestIntersection(rayPos, rayDir, info))
            return rayMissColor;

        Vector ret = L_out(info, rayDir.inverse(), numBounces);
        //System.out.println(ret.x + " " + ret.y + " " + ret.z);
        return ret;
    }

    private Vector renderPixel(double u, double v) {
        u = u * 2.0 - 1.0;
        v = v * 2.0 - 1.0;

        Vector rayStart = cameraPos.add(cameraFwd.multiply(nearPlaneDistance));
        rayStart = rayStart.add(cameraRight.multiply(windowRight).multiply(u));
        rayStart = rayStart.add(cameraUp.multiply(windowTop).multiply(v));
        Vector rayDir = rayStart.substract(cameraPos).normalize();

        return L_in(rayStart, rayDir);
    }

    private double clamp(double v, double min, double max) {
        if (v < min)
            return min;
        else if (v > max)
            return max;
        else return v;
    }

    private boolean saveImage(String file) {
        List<Vector> outPixels = new ArrayList<>();
        for (int i = 0; i < numPixels; i++) {
            outPixels.add(new Vector());
        }

        for (int i = 0; i < numPixels; i++) {
            Vector src = pixels.get(i);
            Vector dest = outPixels.get(i);

            // apply gamma correction
            Vector correctedPixel = new Vector(
                    Math.pow(src.x, 1.0 / 2.2),
                    Math.pow(src.y, 1.0 / 2.2),
                    Math.pow(src.z, 1.0 / 2.2)
            );

            // clamp and convert
            // ???debatable modifying of correctedPixel???
            dest.x = (int) clamp(correctedPixel.z * 255.0, 0.0, 255.0);
            dest.y = (int) clamp(correctedPixel.y * 255.0, 0.0, 255.0);
            dest.z = (int) clamp(correctedPixel.x * 255.0, 0.0, 255.0);
        }




        for (int x = 0; x < imageHeight; x++) {

            for (int y = 0; y < imageWidth; y++) {
                Vector pixel = outPixels.get(x * imageHeight + y);

                for (int i = 0; i < samplesPerPixel; i++) {
                    Random rand = new Random();
                    double jitterX = rand.nextDouble();
                    double jitterY = rand.nextDouble();
                    double u = (x + jitterX) / (double) imageWidth;
                    double v = (y + jitterY) / (double) imageHeight;

                    Vector sample = new Vector();
                    sample = renderPixel(u, v);
                    //System.out.println(sample.x + " " + sample.y + " " + sample.z);
                    pixel = pixel.add(sample.substract(pixel).divide(i + 1.0));
                    outPixels.set(x * imageHeight + y, pixel);

                    //System.out.println(pixel.x + " " + pixel.y + " " + pixel.z);
                }
            }
        }


        for (Vector pixel : outPixels) {
            System.out.println(pixel.x + " " + pixel.y + " " + pixel.z);
        }


        BufferedImage img = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);

        Collections.reverse(outPixels);
        for (int i = 0; i < numPixels; i++) {

            int a = 0;
            int r = (int) outPixels.get(i).x;
            int g = (int) outPixels.get(i).y;
            int b = (int) outPixels.get(i).z;
            //System.out.println(r + " " + g + " " + b);
            int p = (a<<24) | (r<<16) | (g<<8) | b;
            img.setRGB(i / imageWidth, i % imageWidth, p);

            //System.out.println(tmp);
        }




        File fileFromString = new File(file);
        try {
            ImageIO.write(img, "bmp", fileFromString);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    public static void main(String[] args) {
        initializeScenes();

        String file = "src/test.bmp";
        Main main = new Main();
        main.saveImage(file);

    }
}
