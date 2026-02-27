import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SourceImage {

    static int BASE_GREY_EDGE = -13158601;
    static int BASE_GREY_1 = -12172220;
    static int BASE_GREY_2 = -11843255;

    final int GREY_EDGE;
    final int GREY_1;
    final int GREY_2;
    final List<Integer> sorted;

    static double BASE_BRIGHTNESS = 119.76914285714285;

    SourceImage(BufferedImage image, List<Integer> filter){

        sorted = getColours(image, filter);

        int totaladded = 0;
        double brightness = 0;
        for (int x = 0; x < sorted.size(); x++) {
            int c = sorted.get(x);
            if (x > 2) {
                brightness += TextureGenerator.humanBrightness(c);
                totaladded++;
            }
        }
        brightness = brightness / totaladded;

        GREY_EDGE = scale(BASE_GREY_EDGE, brightness);
        GREY_1 = scale(BASE_GREY_1, brightness);
        GREY_2 = scale(BASE_GREY_2, brightness);
    }

    static int scale(int argb, double imgBrightness) {
        double factor = imgBrightness/BASE_BRIGHTNESS;
        int a = (argb >> 24) & 0xFF;
        int r = (argb >> 16) & 0xFF;
        int g = (argb >> 8)  & 0xFF;
        int b = argb & 0xFF;

        r = (int)(r * factor);
        g = (int)(g * factor);
        b = (int)(b * factor);

        r = Math.min(255, Math.max(0, r));
        g = Math.min(255, Math.max(0, g));
        b = Math.min(255, Math.max(0, b));

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    static List<Integer> getColours(BufferedImage image, List<Integer> filter){
        Set<Integer> colours = new HashSet<>();

        boolean doFilter = filter != null;

        int w = image.getWidth();
        int h = image.getHeight();

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int argb = image.getRGB(x, y);

                int a = (argb >> 24) & 0xFF;
                if (a == 0) continue; // skip fully transparent

                if(doFilter){
                    if(filter.contains(argb)) continue;
                }

                colours.add(argb);
            }
        }

        List<Integer> sorted = new ArrayList<>(colours);

        sorted.sort((c1, c2) -> {
            int r1 = (c1 >> 16) & 0xFF, g1 = (c1 >> 8) & 0xFF, b1 = c1 & 0xFF;
            int r2 = (c2 >> 16) & 0xFF, g2 = (c2 >> 8) & 0xFF, b2 = c2 & 0xFF;

            double b1v = 0.2126*r1 + 0.7152*g1 + 0.0722*b1;
            double b2v = 0.2126*r2 + 0.7152*g2 + 0.0722*b2;

            return Double.compare(b1v, b2v);
        });

        return sorted;
    }
}
