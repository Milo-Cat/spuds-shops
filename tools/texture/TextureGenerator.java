import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple (and crude) tool for making textures.
 * Put planks images in the source file and edit the woodTypes array
 **/
public class TextureGenerator {

    static String[] woodTypes = {
            "acacia",
            "bamboo",
            "birch",
            "cherry",
            "crimson",
            "dark_oak",
            "jungle",
            "mangrove",
            "oak",
            "spruce",
            "warped"
    };
    static String[] guiTypes = {
            "customer",
            "owner_trade",
            "settings",
            "storage",
            "button_back"
    };

    static String targetDir = "tools/texture/";

    public static void main(String[] args) {

        BufferedImage filterColours = getImg("filter_colours");
        var filter = new SourceImage(filterColours, null).sorted;

        boolean runTests = false;
        boolean doneOne = false;
        for (String guiType : guiTypes) {

            for (String wood : woodTypes) {

                BufferedImage gui = getImg(guiType);
                SourceImage guiSource = new SourceImage(gui, filter);

                BufferedImage image = getImg("source/" + wood + "_planks");

                SourceImage sourceImage = new SourceImage(image, filter);

                int size = sourceImage.sorted.size();
                BufferedImage outImg = new BufferedImage(
                        size,
                        size,
                        BufferedImage.TYPE_INT_ARGB);


                for (int x = 0; x < size; x++) {
                    int c = sourceImage.sorted.get(x);

                    for (int y = 0; y < size; y++) {
                        outImg.setRGB(x, y, c);
                    }
                }

                Map<Integer, Integer> replaceMap = new HashMap<>();

                if (guiSource.sorted.size() - 3 > sourceImage.sorted.size()) {
                    System.out.println("Wood " + wood + " Does not have enough colours!");
                    for (int i = 0; i < guiSource.sorted.size(); i++) {
                        System.out.println("Wood " + wood + " Does not have enough colours!");
                    }
                }

                replaceMap.put(guiSource.sorted.get(0), sourceImage.GREY_EDGE);
                replaceMap.put(guiSource.sorted.get(1), sourceImage.GREY_1);
                replaceMap.put(guiSource.sorted.get(2), sourceImage.GREY_2);

                for (int i = 3; i < guiSource.sorted.size(); i++) {
                    replaceMap.put(guiSource.sorted.get(i), sourceImage.sorted.get(i - 3));
                }

                BufferedImage result = replaceColours(gui, replaceMap);


                if (!doneOne&runTests) {
                    try {
                        File outputFile = new File(targetDir + "generated/" + wood + ".png");

                        ImageIO.write(outImg, "png", outputFile);
                    } catch (IOException e) {
                        System.out.println("Error while writing image: " + e.getMessage());
                    }
                }
                try {
                    File outputFile = new File(targetDir + "generated/" + guiType + "_" + wood + ".png");

                    ImageIO.write(result, "png", outputFile);
                } catch (IOException e) {
                    System.out.println("Error while writing image: " + e.getMessage());
                }
            }
            doneOne = true;


            if(runTests) {
                BufferedImage gui = getImg(guiType);
                SourceImage guiSource = new SourceImage(gui, filter);

                int size = guiSource.sorted.size();
                BufferedImage outImg = new BufferedImage(
                        size,
                        size,
                        BufferedImage.TYPE_INT_ARGB);

                for (int x = 0; x < size; x++) {
                    int c = guiSource.sorted.get(x);

                    for (int y = 0; y < size; y++) {
                        outImg.setRGB(x, y, c);
                    }
                }
                try {
                    File outputFile = new File(targetDir + "generated/" + guiType + ".png");

                    ImageIO.write(outImg, "png", outputFile);
                } catch (IOException e) {
                    System.out.println("Error while writing image: " + e.getMessage());
                }
            }
        }

    }

    public static BufferedImage getImg(String dir) {

        BufferedImage image = null;
        try {
            File inputFile = new File(targetDir + dir + ".png");

            System.out.println(inputFile.getAbsolutePath());

            image = ImageIO.read(inputFile);
            System.out.println("Image " + dir + " read successfully.");
        } catch (IOException e) {
            System.out.println("Error while reading image " + dir + ": " + e.getMessage());
        }
        return image;
    }


    public static double humanBrightness(int argb) {
        int r = (argb >> 16) & 0xFF;
        int g = (argb >> 8) & 0xFF;
        int b = argb & 0xFF;

        return 0.2126 * r + 0.7152 * g + 0.0722 * b;
    }

    static void printRGB(int argb) {
        int r = (argb >> 16) & 0xFF;
        int g = (argb >> 8) & 0xFF;
        int b = argb & 0xFF;
        System.out.printf("#%02X%02X%02X%n", r, g, b);
    }

    static BufferedImage replaceColours(BufferedImage img, Map<Integer, Integer> replaceMap) {

        int w = img.getWidth();
        int h = img.getHeight();

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {

                int argb = img.getRGB(x, y);

                Integer newColour = replaceMap.get(argb);
                if (newColour != null) {
                    img.setRGB(x, y, newColour);
                }
            }
        }

        return img;

    }


}