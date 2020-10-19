package cljgl_universe;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class UniverseEngine {
    public int[] lightIndex64;
    public int[] darkIndex64;

    Deque<Integer>[] lightIndex64Points;
    Deque<Integer>[] darkIndex64Points;

    int size, index64Width, index64Size, numLightPoints, numDarkPoints;

    public float[] lightPointsXA, darkPointsXA, lightPointsYA, darkPointsYA;
    public float[] lightPointsXB, darkPointsXB, lightPointsYB, darkPointsYB;
    public float[] lightPointsForce;

    boolean bufferSwitch = true;

    public UniverseEngine(int size, int numLightPoints, int numDarkPoints) {
        this.size = size;
        this.numLightPoints = numLightPoints;
        this.numDarkPoints = numDarkPoints;

        this.lightIndex64 = new int[(size * size) / (64 * 64)];
        this.darkIndex64 = new int[(size * size) / (64 * 64)];

        this.lightPointsXA = new float[numLightPoints * 2];
        this.lightPointsYA = new float[numLightPoints * 2];
        this.darkPointsXA = new float[numDarkPoints * 2];
        this.darkPointsYA = new float[numDarkPoints * 2];

        this.lightPointsXB = new float[numLightPoints * 2];
        this.lightPointsYB = new float[numLightPoints * 2];
        this.darkPointsXB = new float[numDarkPoints * 2];
        this.darkPointsYB = new float[numDarkPoints * 2];

        this.lightPointsForce = new float[numLightPoints];

        index64Width = size / 64;
        index64Size = index64Width * index64Width;

        lightIndex64Points = (Deque<Integer>[])new ArrayDeque[index64Size];
        darkIndex64Points = (Deque<Integer>[])new ArrayDeque[index64Size];
        for (int i = 0; i < index64Size; i++) {
            lightIndex64Points[i] = new ArrayDeque<Integer>(100);
            darkIndex64Points[i] = new ArrayDeque<Integer>(100);
        }
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
        /*for (int i = 0; i < numLightPoints/2; i++) {
            lightPointsX[i] = (float)size*2/10 + ThreadLocalRandom.current().nextFloat() * (float)size/10;
            lightPointsY[i] = (float)size*2/10 + ThreadLocalRandom.current().nextFloat() * (float)size/10;
        }
        for (int i = numLightPoints/2; i < numLightPoints; i++) {
            lightPointsX[i] = (float)size*7/10 + ThreadLocalRandom.current().nextFloat() * (float)size/10;
            lightPointsY[i] = (float)size*7/10 + ThreadLocalRandom.current().nextFloat() * (float)size/10;
        }*/
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

        index64Size = (size/64) * (size/64);
        for (int i = 0; i < index64Size; i++) {
            lightIndex64Points[i].clear();
            darkIndex64Points[i].clear();
        }
        Arrays.fill(lightIndex64, 0);
        Arrays.fill(darkIndex64, 0);

        Arrays.fill(lightPointsForce, 0.0f);

        for (int i = 0; i < numLightPoints; i++) {
            int i64 = ((int)lightPointsX[i] / 64) + (((int)lightPointsY[i] / 64) * index64Width);
            lightIndex64[i64] += 1;
            lightIndex64Points[i64].add(i);
        }
        for (int i = 0; i < numDarkPoints; i++) {
            int i64 = ((int)darkPointsX[i] / 64) + (((int)darkPointsY[i] / 64) * index64Width);
            darkIndex64[i64] += 1;
            darkIndex64Points[i64].add(i);
        }
    }

    public void switchBuffers() {
        bufferSwitch = !bufferSwitch;
    }

    public int lowerBound(int x, int divisor) {
        int x2, m, z, d = 0;
        switch(divisor) {
            case 4: d = 12; break;
            case 16: d = 48; break;
            case 64: d = 64; break;
            case 256: d = 196; break;
        }
        x2 = x - d;
        m = x2 % divisor;
        z = x2 / divisor;

        if (m < divisor / 4) {
            z--;
        } 
        if (z < 0) {
            z = 0;
        }
        if (z >= size / divisor) {
            z = size / divisor;
        }
        return z;
    }

    public int higherBound(int x, int divisor) {
        int x2, m, z, d = 0;
        switch(divisor) {
            case 4: d = 12; break;
            case 16: d = 48; break; //32
            case 64: d = 64; break;
            case 256: d = 196; break;
        }
        x2 = x + d;
        m = x2 % divisor;
        z = 1 + (x2 / divisor);

        if (m >= 3 * divisor / 4) {
            z++;
        }
        if (z < 0) {
            z = 0;
        }
        if (z >= size / divisor) {
            z = size / divisor;
        }
        return z;
    }


    public void calcLightPoints() {
        float[] lightPointsX = getLightPointsX();
        float[] lightPointsY = getLightPointsY();
        float[] darkPointsX = getDarkPointsX();
        float[] darkPointsY = getDarkPointsY();

        float[] lightPointsXWrite = bufferSwitch ? lightPointsXB : lightPointsXA;
        float[] lightPointsYWrite = bufferSwitch ? lightPointsYB : lightPointsYA;

        float x, y, dx = 0.0f, dy = 0.0f, h, h3, ex, ey, thetaMod = 0.0f, forceTotal;
        int i, jx, jy, ix, iy, lbx, lby, hbx, hby, pidx, v;

        for (i = 0; i < numLightPoints; i++) {
            x = lightPointsX[i];
            y = lightPointsY[i];
            ex = 0.0f;
            ey = 0.0f;
            ix = (int)x;
            iy = (int)y;
            lbx = lowerBound(ix, 64);
            hbx = higherBound(ix, 64);
            lby = lowerBound(iy, 64);
            hby = higherBound(iy, 64);
            thetaMod = 0.0f;
            forceTotal = 0.0f;

            for (jy = 0; jy < size/64; jy++) {
                for (jx = 0; jx < size/64; jx++) {
                    if (jy < lby || jy >= hby || jx < lbx || jx >= hbx) {
                        v = lightIndex64[jx + (jy * index64Width)];
                        dx = (float)(jx * 64) + 32.0f - x;
                        dy = (float)(jy * 64) + 32.0f - y;
                        h = (float)Math.sqrt(dx * dx + dy * dy);
                        h3 = h * h * h;
                        ex += (float)v * 10.0f * dx / h3;
                        ey += (float)v * 10.0f * dy / h3;
                    } else {
                        for(Iterator itr = lightIndex64Points[jx + jy*index64Width].iterator(); itr.hasNext();) {	      
                            pidx = (Integer)itr.next();
                            if (pidx != i) {
                                dx = lightPointsX[pidx] - x;
                                dy = lightPointsY[pidx] - y;
                                h = (float)Math.sqrt(dx * dx + dy * dy);
                                if (h < 1.0f) h = 1.0f;
                                h3 = 1.0f / (h * h * h);
                                thetaMod += h3 /  100.0f;
                                forceTotal += 0.25f / (h * h);
                                if (h >= 5.0f) { 
                                    ex += 20.0f * dx * h3;
                                    ey += 20.0f * dy * h3;
                                }
                            }
                        }
                    }  
                }
            }

            lightPointsForce[i] = 500.0f * forceTotal / (1.0f + Math.abs(0.01f * forceTotal));;

            float theta = (float)Math.atan2(ey, ex);
            float h2 = (float)Math.sqrt(ex*ex + ey*ey);
            thetaMod = 12.0f * thetaMod / (1.0f + Math.abs(4.0f * thetaMod));
            h2 = 4.0f * h2 / (1.0f + Math.abs(h2));

            theta += thetaMod;
            ey = (float)Math.sin(theta) * h2;
            ex = (float)Math.cos(theta) * h2;

            for (jy = 0; jy < size/64; jy++) {
                for (jx = 0; jx < size/64; jx++) {
                    if (jy < lby || jy >= hby || jx < lbx || jx >= hbx) {
                    } else {
                        for(Iterator itr = darkIndex64Points[jx + jy*index64Width].iterator(); itr.hasNext();) {	      
                            pidx = (Integer)itr.next();
                            if (pidx != i) {
                                dx = darkPointsX[pidx] - x;
                                dy = darkPointsY[pidx] - y;
                                h = (float)Math.sqrt(dx * dx + dy * dy);
                                if (h > 120.0f) continue;
                                h3 = h * h * h;
                                if (h < 4.0f) {
                                    ex += dx / 5.0f;
                                    ey += dy / 5.0f;
                                } else {
                                    ex += 63.0f * dx / h3;
                                    ey += 63.0f * dy / h3;
                                }
                            }
                        }
                    }  
                }
            }         
            lightPointsXWrite[i] = Math.max(14.0f, Math.min((float)size - 14.0f, x + ex));
            lightPointsYWrite[i] = Math.max(14.0f, Math.min((float)size - 14.0f, y + ey));
        }
    }

    public void calcDarkPoints() {
        float[] lightPointsX = getLightPointsX();
        float[] lightPointsY = getLightPointsY();
        float[] darkPointsX = getDarkPointsX();
        float[] darkPointsY = getDarkPointsY();

        float[] darkPointsXWrite = bufferSwitch ? darkPointsXB : darkPointsXA;
        float[] darkPointsYWrite = bufferSwitch ? darkPointsYB : darkPointsYA;

        float x, y, dx = 0.0f, dy = 0.0f, h, h3, ex, ey, cx, cy;
        int i, jx, jy, ix, iy, lbx, lby, hbx, hby, pidx, v;

        for (i = 0; i < numDarkPoints; i++) {
            x = darkPointsX[i];
            y = darkPointsY[i];
            ex = x;
            ey = y;
            cx = (x - (float)(size / 2)) / 290.0f;
            cy = (y - (float)(size / 2)) / 290.0f;
            if (cx > 0) {
                ex = x - cx*cx; 
            }
            if (cy > 0) {
                ey = y - cy*cy; 
            }
            if (cx < 0) {
                ex = x + cx*cx; 
            }
            if (cy < 0) {
                ey = y + cy*cy; 
            }

            ix = (int)x;
            iy = (int)y;
            lbx = lowerBound(ix, 64);
            hbx = higherBound(ix, 64);
            lby = lowerBound(iy, 64);
            hby = higherBound(iy, 64);

            for (jy = 0; jy < size/64; jy++) {
                for (jx = 0; jx < size/64; jx++) {
                    if (jy < lby || jy >= hby || jx < lbx || jx >= hbx) {
                        v = darkIndex64[jx + (jy * index64Width)];
                        dx = (float)(jx * 64) + 32.0f - x;
                        dy = (float)(jy * 64) + 32.0f - y;
                        h = (float)Math.sqrt(dx * dx + dy * dy);
                        h3 = h * h * h;
                        ex -= (float)v * 35.0f * dx / h3;
                        ey -= (float)v * 35.0f * dy / h3;

                        v = lightIndex64[jx + (jy * index64Width)];
                        if (h < 1.0f) {
                            ex -= (float)v * 32.0f * dx * h3;
                            ey -= (float)v * 32.0f * dy * h3;
                        } else {
                            ex -= (float)v * 35.0f * dx / h3;
                            ey -= (float)v * 35.0f * dy / h3;
                        }
                    } else {
                        for(Iterator itr = darkIndex64Points[jx + jy*index64Width].iterator(); itr.hasNext();) {	      
                            pidx = (Integer)itr.next();
                            if (pidx != i) {
                                dx = darkPointsX[pidx] - x;
                                dy = darkPointsY[pidx] - y;
                                h = (float)Math.sqrt(dx * dx + dy * dy);
                                h3 = h * h * h;
                                if (h < 1.0f) {
                                    ex -= dx;
                                    ey -= dy;
                                } else {
                                    ex -= 35.0f * dx / h3;
                                    ey -= 35.0f * dy / h3;
                                }
                            }
                        }
                        for(Iterator itr = lightIndex64Points[jx + jy*index64Width].iterator(); itr.hasNext();) {	      
                            pidx = (Integer)itr.next();
                            if (pidx != i) {
                                dx = lightPointsX[pidx] - x;
                                dy = lightPointsY[pidx] - y;
                                h = (float)Math.sqrt(dx * dx + dy * dy);
                                h3 = h * h * h;
                                if (h < 1.0f) {
                                    ex -= 32.0f * dx * h3;
                                    ey -= 32.0f * dy * h3;
                                } else {
                                    ex -= 35.0f * dx / h3;
                                    ey -= 35.0f * dy / h3;
                                }
                            }
                        }
                    }  
                }
            }  
            darkPointsXWrite[i] = Math.max(14.0f, Math.min((float)size - 14.0f, ex));
            darkPointsYWrite[i] = Math.max(14.0f, Math.min((float)size - 14.0f, ey));
        }
    }    

    private static float hue2rgb(float p, float q, float h) {
        if (h < 0) {
            h += 1;
        }
        if (h > 1) {
            h -= 1;
        }
        if (6 * h < 1) {
            return p + ((q - p) * 6 * h);
        }
        if (2 * h < 1) {
            return q;
        }
        if (3 * h < 2) {
            return p + ((q - p) * 6 * ((2.0f / 3.0f) - h));
        }
        return p;
    }

    static public int[] hslColor(float h, float s, float l) {
        float q, p, r, g, b;
        int[] color = new int[3];
        
        if (s == 0) {
            r = g = b = l; // achromatic
        } else {
            q = l < 0.5 ? (l * (1 + s)) : (l + s - l * s);
            p = 2 * l - q;
            r = hue2rgb(p, q, h + 1.0f / 3);
            g = hue2rgb(p, q, h);
            b = hue2rgb(p, q, h - 1.0f / 3);
        }
        color[0] = Math.round(r * 255);
        color[1] = Math.round(g * 255);
        color[2] = Math.round(b * 255);
        return color;
    }


    public static void main(String[] args) 
    {   
        System.out.println("test"); 
    } 
}