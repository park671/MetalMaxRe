package me.afoolslove.metalmaxre;

import me.afoolslove.metalmaxre.utils.FileUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Img2Code {

    static int startIdx = 75;

    static List<Integer> palette = new ArrayList<>() {
        {
            add(123);
        }
    };

    private static int addColor(int argb) {
        if (!palette.contains(argb)) {
            palette.add(argb);
        }
        return palette.indexOf(argb);
    }

    public static void main(String[] args) throws Throwable {
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            System.out.print("input path:");
            String path = scanner.nextLine();
            BufferedImage bufferedImage = ImageIO.read(new File(path));
            int startX, startY, endX, endY;
            System.out.print("file name:");
            String fileName = scanner.nextLine();

            //x - width, y - height
            endX = endY = 0;
            startX = bufferedImage.getWidth();
            startY = bufferedImage.getHeight();
            for (int i = 0; i < bufferedImage.getHeight(); i++) {
                for (int j = 0; j < bufferedImage.getWidth(); j++) {
                    int color = bufferedImage.getRGB(j, i);
                    if (color != Color.BLACK.getRGB()) {
                        if (j < startX) {
                            startX = j;
                        }
                        if (i < startY) {
                            startY = i;
                        }
                        if (j > endX) {
                            endX = j;
                        }
                        if (i > endY) {
                            endY = i;
                        }
                    }
                }
            }
            int subWidth = (endX - startX);
            int subHeight = (endY - startY);
            System.out.println("width=" + subWidth + ", height=" + subHeight);
            StringBuilder bitmap_c_file = new StringBuilder();
            StringBuilder bitmap_head_file = new StringBuilder();

            bitmap_head_file.append("#ifndef METALMAX_").append(fileName.toUpperCase()).append("_H\n");
            bitmap_head_file.append("#define METALMAX_").append(fileName.toUpperCase()).append("_H\n");
            bitmap_head_file.append("extern unsigned char ").append(fileName.toLowerCase())
                    .append("[")
                    .append(subHeight)
                    .append("][")
                    .append(subWidth)
                    .append("];\n");

            bitmap_c_file.append("#include \"").append(fileName.toLowerCase())
                    .append(".h\"\n")
                    .append("unsigned char ").append(fileName.toLowerCase())
                    .append("[")
                    .append(subHeight)
                    .append("][")
                    .append(subWidth)
                    .append("]={\n");

            for (int i = startY; i < endY; i++) {
                bitmap_c_file.append("{");
                for (int j = startX; j < endX; j++) {
                    int colorIdx = addColor(bufferedImage.getRGB(j, i));
                    bitmap_c_file.append(colorIdx + startIdx).append(",");
                }
                bitmap_c_file.append("},\n");
            }
            bitmap_c_file.append("};\n");
            FileUtils.writeFile("./bitmaps/" + fileName.toLowerCase() + ".cpp", bitmap_c_file.toString());

            bitmap_head_file.append("#endif");
            FileUtils.writeFile("./bitmaps/" + fileName.toLowerCase() + ".h", bitmap_head_file.toString());
        }//over!
        System.out.println("palette:");
        StringBuilder paletteSb = new StringBuilder();
        for (int i = 0; i < palette.size(); i++) {
            paletteSb.append("add(").append(palette.get(i)).append(");\n");
        }
        System.out.println(paletteSb);
    }
}
