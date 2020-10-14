package cljgl_universe;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class Helper {
    public int[] lightIndex4;
    public int[] lightIndex16;
    public int[] lightIndex64;
    public int[] lightIndex256;

    public int[] darkIndex4;
    public int[] darkIndex16;
    public int[] darkIndex64;
    public int[] darkIndex256;

    int size;
    int numLightPoints, numDarkPoints;
    public float[] lightPointsXA, darkPointsXA, lightPointsYA, darkPointsYA;
    public float[] lightPointsXB, darkPointsXB, lightPointsYB, darkPointsYB;

    boolean bufferSwitch = true;

    public Helper(int size, int numLightPoints, int numDarkPoints) {
        this.size = size;
        this.numLightPoints = numLightPoints;
        this.numDarkPoints = numDarkPoints;

        this.lightIndex4 = new int[(size * size) / (4 * 4)];
        this.lightIndex16 = new int[(size * size) / (16 * 16)];
        this.lightIndex64 = new int[(size * size) / (64 * 64)];
        this.lightIndex256 = new int[(size * size) / (256 * 256)];

        this.darkIndex4 = new int[(size * size) / (4 * 4)];
        this.darkIndex16 = new int[(size * size) / (16 * 16)];
        this.darkIndex64 = new int[(size * size) / (64 * 64)];
        this.darkIndex256 = new int[(size * size) / (256 * 256)];

        this.lightPointsXA = new float[numLightPoints * 2];
        this.lightPointsYA = new float[numLightPoints * 2];
        this.darkPointsXA = new float[numDarkPoints * 2];
        this.darkPointsYA = new float[numDarkPoints * 2];

        this.lightPointsXB = new float[numLightPoints * 2];
        this.lightPointsYB = new float[numLightPoints * 2];
        this.darkPointsXB = new float[numDarkPoints * 2];
        this.darkPointsYB = new float[numDarkPoints * 2];
    }

    public float[] getLightPointsX() {
        return bufferSwitch ? lightPointsXA : lightPointsXB;
    }

    public float[] getLightPointsY() {
        return bufferSwitch ? lightPointsYA : lightPointsYB;
    }

    public float[] getDarkPointsX() {
        return bufferSwitch ? darkPointsXA : darkPointsXB;
    }

    public float[] getDarkPointsY() {
        return bufferSwitch ? darkPointsYA : darkPointsYB;
    }

    public void randomizeLightPoints() {
        float[] lightPointsX = getLightPointsX();
        float[] lightPointsY = getLightPointsY();

        for (int i = 0; i < numLightPoints; i++) {
            lightPointsX[i] = ThreadLocalRandom.current().nextFloat() * (float)size;
            lightPointsY[i] = ThreadLocalRandom.current().nextFloat() * (float)size;
        }
    }

    public void randomizeDarkPoints() {
        float[] darkPointsX = getDarkPointsX();
        float[] darkPointsY = getDarkPointsY();

        for (int i = 0; i < numDarkPoints; i++) {
            darkPointsX[i] = ThreadLocalRandom.current().nextFloat() * (float)size;
            darkPointsY[i] = ThreadLocalRandom.current().nextFloat() * (float)size;
        }
    }

    public void indexPoints() {
        float[] lightPointsX = getLightPointsX();
        float[] lightPointsY = getLightPointsY();
        float[] darkPointsX = getDarkPointsX();
        float[] darkPointsY = getDarkPointsY();

        int index4Width = size / 4;
        int index16Width = size / 16;
        int index64Width = size / 64;
        int index256Width = size / 256;

        Arrays.fill(lightIndex4, 0);
        Arrays.fill(lightIndex16, 0);
        Arrays.fill(lightIndex64, 0);
        Arrays.fill(lightIndex256, 0);

        Arrays.fill(darkIndex4, 0);
        Arrays.fill(darkIndex16, 0);
        Arrays.fill(darkIndex64, 0);
        Arrays.fill(darkIndex256, 0);

        for (int i = 0; i < numLightPoints; i++) {
            lightIndex4[((int)lightPointsX[i] / 4) + (((int)lightPointsY[i] / 4) * index4Width)] += 1;
            lightIndex16[((int)lightPointsX[i] / 16) + (((int)lightPointsY[i] / 16) * index16Width)] += 1;
            lightIndex64[((int)lightPointsX[i] / 64) + (((int)lightPointsY[i] / 64) * index64Width)] += 1;
            lightIndex256[((int)lightPointsX[i] / 256) + (((int)lightPointsY[i] / 256) * index256Width)] += 1;
        }
        for (int i = 0; i < numDarkPoints; i++) {
            darkIndex4[((int)darkPointsX[i] / 4) + (((int)darkPointsY[i] / 4) * index4Width)] += 1;
            darkIndex16[((int)darkPointsX[i] / 16) + (((int)darkPointsY[i] / 16) * index16Width)] += 1;
            darkIndex64[((int)darkPointsX[i] / 64) + (((int)darkPointsY[i] / 64) * index64Width)] += 1;
            darkIndex256[((int)darkPointsX[i] / 256) + (((int)darkPointsY[i] / 256) * index256Width)] += 1;
        }
    }

    public void switchBuffers() {
        bufferSwitch = !bufferSwitch;
    }

    public void calcLightPoints() {
        float[] lightPointsX = getLightPointsX();
        float[] lightPointsY = getLightPointsY();
        float[] darkPointsX = getDarkPointsX();
        float[] darkPointsY = getDarkPointsY();

        float[] lightPointsXWrite = bufferSwitch ? lightPointsXB : lightPointsXA;
        float[] lightPointsYWrite = bufferSwitch ? lightPointsYB : lightPointsYA;
        float[] darkPointsXWrite = bufferSwitch ? darkPointsXB : darkPointsXA;
        float[] darkPointsYWrite = bufferSwitch ? darkPointsYB : darkPointsYA;

        float x, y, dx, dy, ex, ey, h, h3, cx, cy;
        int i, j;
        cx = (float)size / 2.0f;
        cy = (float)size / 2.0f;

        for (i = 0; i < numLightPoints; i++) {
            x = lightPointsX[i];
            y = lightPointsY[i];
            ex = 0.0f;
            ey = 0.0f;
            for (j = 0; j < numLightPoints; j++) {
                if (j != i) {
                    dx = lightPointsX[j] - x;
                    dy = lightPointsY[j] - y;
                    h = (float)Math.sqrt(dx * dx + dy * dy);
                    if (h == 0.0f) continue;
                    h3 = h * h * h;
                    ex += 100.0f * Math.max(-0.01f, Math.min(0.01f, dx / h3));
                    ey += 100.0f * Math.max(-0.01f, Math.min(0.01f, dy / h3));
                }
            }
            ex = 4.0f * ex / (1.0f + Math.abs(ex));
            ey = 4.0f * ey / (1.0f + Math.abs(ey));
            for (j = 0; j < numDarkPoints; j++) {
                if (j != i) {
                    dx = darkPointsX[j] - x;
                    dy = darkPointsY[j] - y;
                    h = (float)Math.sqrt(dx * dx + dy * dy);
                    if (h == 0.0f) continue;
                    h3 = h * h * h;
                    ex += 200.0f * Math.max(-0.1f, Math.min(0.1f, dx / h3));
                    ey += 200.0f * Math.max(-0.1f, Math.min(0.1f, dy / h3));
                }
            }
            //ex = ex - ((x - cx) / 400.0f);
            //ey = ey - ((y - cy) / 400.0f);
            
            lightPointsXWrite[i] = Math.max(2.0f, Math.min((float)size - 2.0f, x + ex));
            lightPointsYWrite[i] = Math.max(2.0f, Math.min((float)size - 2.0f, y + ey));
        }
    }

    public void calcDarkPoints() {
        float[] lightPointsX = getLightPointsX();
        float[] lightPointsY = getLightPointsY();
        float[] darkPointsX = getDarkPointsX();
        float[] darkPointsY = getDarkPointsY();

        float[] lightPointsXWrite = bufferSwitch ? lightPointsXB : lightPointsXA;
        float[] lightPointsYWrite = bufferSwitch ? lightPointsYB : lightPointsYA;
        float[] darkPointsXWrite = bufferSwitch ? darkPointsXB : darkPointsXA;
        float[] darkPointsYWrite = bufferSwitch ? darkPointsYB : darkPointsYA;

        float x, y, dx, dy, ex, ey, h, h3, cx, cy;
        int i, j;
        cx = (float)size / 2.0f;
        cy = (float)size / 2.0f;

        for (i = 0; i < numDarkPoints; i++) {
            x = darkPointsX[i];
            y = darkPointsY[i];
            ex = x - ((x - cx) / 148.0f);
            ey = y - ((y - cy) / 148.0f);
            for (j = 0; j < numDarkPoints; j++) {
                if (j != i) {
                    dx = darkPointsX[j] - x;
                    dy = darkPointsY[j] - y;
                    h = (float)Math.sqrt(dx * dx + dy * dy);
                    if (h == 0.0f) continue;
                    h3 = h * h * h;
                    ex -= 100.0f * Math.max(-0.01f, Math.min(0.01f, dx / h3));
                    ey -= 100.0f * Math.max(-0.01f, Math.min(0.01f, dy / h3));
                }
            }
            for (j = 0; j < numLightPoints; j++) {
                if (j != i) {
                    dx = lightPointsX[j] - x;
                    dy = lightPointsY[j] - y;
                    h = (float)Math.sqrt(dx * dx + dy * dy);
                    if (h == 0.0f) continue;
                    h3 = h * h * h;
                    ex -= 70.0f * Math.max(-2.0f, Math.min(2.0f, dx / h3));
                    ey -= 70.0f * Math.max(-2.0f, Math.min(2.0f, dy / h3));
                }
            }
            darkPointsXWrite[i] = Math.max(2.0f, Math.min((float)size - 2.0f, ex));
            darkPointsYWrite[i] = Math.max(2.0f, Math.min((float)size - 2.0f, ey));
        }
    }

    public static float foo() {
        return ThreadLocalRandom.current().nextFloat() * 100.0f;
    }
}