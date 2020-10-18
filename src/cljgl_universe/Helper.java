package cljgl_universe;

import java.util.Arrays;
import java.util.Deque;
import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;


public class Helper {
    public int[] lightIndex4;
    public int[] lightIndex16;
    public int[] lightIndex64;
    public int[] lightIndex256;

    public int[] darkIndex4;
    public int[] darkIndex16;
    public int[] darkIndex64;
    public int[] darkIndex256;

    int index4Width;
    int index16Width;
    int index64Width;
    int index256Width;
    
    HashMap<Integer, Deque<Integer>> lightIndex4Points;
    HashMap<Integer, Deque<Integer>> darkIndex4Points;

    /*class Point {
        public float x, y;
        public Point(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }*/

    Deque<Integer>[] lightIndex64Points;
    Deque<Integer>[] darkIndex64Points;

    int size;
    int numLightPoints, numDarkPoints;
    public float[] lightPointsXA, darkPointsXA, lightPointsYA, darkPointsYA;
    public float[] lightPointsXB, darkPointsXB, lightPointsYB, darkPointsYB;

    boolean bufferSwitch = true;

    int index64Size;

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

        index4Width = size / 4;
        index16Width = size / 16;
        index64Width = size / 64;
        index256Width = size / 256;

        lightIndex4Points = new HashMap<Integer, Deque<Integer>>();
        darkIndex4Points = new HashMap<Integer, Deque<Integer>>();

        index64Size = (size/64) * (size/64);
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
       /* for (int i = 0; i < numLightPoints/2; i++) {
            lightPointsX[i] = ThreadLocalRandom.current().nextFloat() * (float)size/10;
            lightPointsY[i] = ThreadLocalRandom.current().nextFloat() * (float)size/10;
        }
        for (int i = numLightPoints/2; i < numLightPoints; i++) {
            lightPointsX[i] = (float)size*5/10 + ThreadLocalRandom.current().nextFloat() * (float)size/10;
            lightPointsY[i] = (float)size*5/10 + ThreadLocalRandom.current().nextFloat() * (float)size/10;
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

        //lightIndex4Points.clear();
        //darkIndex4Points.clear();
        //ArrayDeque<Integer>(10)

        index64Size = (size/64) * (size/64);
        for (int i = 0; i < index64Size; i++) {
            lightIndex64Points[i].clear();
            darkIndex64Points[i].clear();
        }

        //Arrays.fill(lightIndex4, 0);
        //Arrays.fill(lightIndex16, 0);
        Arrays.fill(lightIndex64, 0);
        //Arrays.fill(lightIndex256, 0);

        //Arrays.fill(darkIndex4, 0);
        //Arrays.fill(darkIndex16, 0);
        Arrays.fill(darkIndex64, 0);
        //Arrays.fill(darkIndex256, 0);

        for (int i = 0; i < numLightPoints; i++) {
            //lightIndex4[((int)lightPointsX[i] / 4) + (((int)lightPointsY[i] / 4) * index4Width)] += 1;
            //lightIndex16[((int)lightPointsX[i] / 16) + (((int)lightPointsY[i] / 16) * index16Width)] += 1;
            int i64 = ((int)lightPointsX[i] / 64) + (((int)lightPointsY[i] / 64) * index64Width);
            lightIndex64[i64] += 1;
            lightIndex64Points[i64].add(i);
            
            //lightIndex256[((int)lightPointsX[i] / 256) + (((int)lightPointsY[i] / 256) * index256Width)] += 1;
        }
        for (int i = 0; i < numDarkPoints; i++) {
            //darkIndex4[((int)darkPointsX[i] / 4) + (((int)darkPointsY[i] / 4) * index4Width)] -= 1;
            //darkIndex16[((int)darkPointsX[i] / 16) + (((int)darkPointsY[i] / 16) * index16Width)] -= 1;
            int i64 = ((int)darkPointsX[i] / 64) + (((int)darkPointsY[i] / 64) * index64Width);
            darkIndex64[i64] += 1;
            darkIndex64Points[i64].add(i);
            
            //darkIndex256[((int)darkPointsX[i] / 256) + (((int)darkPointsY[i] / 256) * index256Width)] -= 1;
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

    public ArrayList<Integer> bounds(int x) {
        ArrayList<Integer> ret = new ArrayList<Integer>();
        //ret.add(lowerBound(x, 4));
        //ret.add(higherBound(x, 4));

        ret.add(lowerBound(x, 16));
        ret.add(higherBound(x, 16));

        ret.add(lowerBound(x, 64));
        ret.add(higherBound(x, 64));

        ret.add(lowerBound(x, 256));
        ret.add(higherBound(x, 256));
        /*for (int divisor = 4; divisor < 512; divisor *= 4) {
            ret.add(lowerBound(x, divisor));
            ret.add(higherBound(x, divisor));
        }*/
        return ret;
    }

    void calcBlock16(float[] e, float x, float y, int bx, int by, int[] index16, int[] index4) {
        if (index16[bx + (by * index16Width)] != 0) {
            bx *= 4;
            by *= 4;
            int ix, v;
            float dx, dy, h, h3;
            for (int iy = by; iy < by + 4; iy++) {
                for (ix = bx; ix < bx + 4; ix++) {
                    v = index4[ix + (iy * index4Width)];
                    if (v != 0) {
                        dx = (float)(ix * 4) + 2.0f - x;
                        dy = (float)(iy * 4) + 2.0f - y;
                        h = (float)Math.sqrt(dx * dx + dy * dy);
                        if (h <= 8.5f) continue;
                        h3 = h * h * h;
                        e[0] += (float)v * 100.0f * Math.max(-0.01f, Math.min(0.01f, dx / h3));
                        e[1] += (float)v * 100.0f * Math.max(-0.01f, Math.min(0.01f, dy / h3));
                    }
                }
            }
        }
    }

    void calcBlock64(float[] e, float x, float y, int bx, int by, int[] index64) {
        //bx *= 4;
        //by *= 4;
        int ix, v;
        float dx, dy, h, h3;

        v = index64[bx + (by * index64Width)];
        if (v != 0) {
            dx = (float)(bx * 64) + 32.0f - x;
            dy = (float)(by * 64) + 32.0f - y;
            h = (float)Math.sqrt(dx * dx + dy * dy);
            if (h <= 8.5f) return;
            h3 = h * h * h;
            e[0] += (float)v * 100.0f * Math.max(-0.01f, Math.min(0.01f, dx / h3));
            e[1] += (float)v * 100.0f * Math.max(-0.01f, Math.min(0.01f, dy / h3));
        }
    }

    void calcBlock16d(float[] e, float x, float y, int bx, int by, int[] index16, int[] index4) {
        if (index16[bx + (by * index16Width)] != 0) {
            bx *= 4;
            by *= 4;
            int ix, v;
            float dx, dy, h, h3;
            for (int iy = by; iy < by + 4; iy++) {
                for (ix = bx; ix < bx + 4; ix++) {
                    v = index4[ix + (iy * index4Width)];
                    if (v != 0) {
                        dx = (float)(ix * 4) + 2.0f - x;
                        dy = (float)(iy * 4) + 2.0f - y;
                        h = (float)Math.sqrt(dx * dx + dy * dy);
                        if (h == 0.0f) continue;
                        h3 = h * h * h;
                        e[0] += (float)v * 100.0f * Math.max(-0.01f, Math.min(0.01f, dx / h3));
                        e[1] += (float)v * 100.0f * Math.max(-0.01f, Math.min(0.01f, dy / h3));
                    }
                }
            }
        }
    }

    void calcBlock64d(float[] e, float x, float y, int bx, int by, int[] index64) {
        //bx *= 4;
        //by *= 4;
        int ix, v;
        float dx, dy, h, h3;

        v = index64[bx + (by * index64Width)];
        if (v != 0) {
            dx = (float)(bx * 64) + 32.0f - x;
            dy = (float)(by * 64) + 32.0f - y;
            h = (float)Math.sqrt(dx * dx + dy * dy);
            if (h == 0.0f) return;
            h3 = h * h * h;
            e[0] += (float)v * 100.0f * Math.max(-0.01f, Math.min(0.01f, dx / h3));
            e[1] += (float)v * 100.0f * Math.max(-0.01f, Math.min(0.01f, dy / h3));
        }
    }


    public void calcLightPointsIndex() {
        float[] lightPointsX = getLightPointsX();
        float[] lightPointsY = getLightPointsY();
        float[] darkPointsX = getDarkPointsX();
        float[] darkPointsY = getDarkPointsY();

        float[] lightPointsXWrite = bufferSwitch ? lightPointsXB : lightPointsXA;
        float[] lightPointsYWrite = bufferSwitch ? lightPointsYB : lightPointsYA;
        float[] darkPointsXWrite = bufferSwitch ? darkPointsXB : darkPointsXA;
        float[] darkPointsYWrite = bufferSwitch ? darkPointsYB : darkPointsYA;

        float[] e = new float[2];
        float x, y, dx, dy, h, h3;
        int i, j, jx, jy, ix, iy, lbx, lby, hbx, hby, ry, k;

        for (i = 0; i < numLightPoints; i++) {
            x = lightPointsX[i];
            y = lightPointsY[i];
            e[0] = 0.0f;
            e[1] = 0.0f;

            ix = (int)x;
            iy = (int)y;
            lbx = lowerBound(ix, 16);
            hbx = higherBound(ix, 16);
            lby = lowerBound(iy, 16);
            hby = higherBound(iy, 16);
            for (jy = lby; jy < hby; jy++) {
                for (jx = lbx; jx < hbx; jx++) {
                    calcBlock16(e, x, y, jx, jy, lightIndex16, lightIndex4);
                }
            }

           /*lbx = 0;//lowerBound(ix, 16);
            hbx = size;//higherBound(ix, 16);
            lby = 0;//lowerBound(iy, 16);
            hby = size;//higherBound(iy, 16);*/
            lbx = lbx / 4;
            hbx = hbx / 4;
            lby = lby / 4;
            hby = hby / 4;
            for (jy = 0; jy < size/64; jy++) {
                for (jx = 0; jx < size/64; jx++) {
                    if (jy < lby || jy >= hby || jx < lbx || jx >= hbx) {
                        calcBlock64(e, x, y, jx, jy, lightIndex64);
                    }
                }
            }

            e[0] = 4.0f * e[0] / (1.0f + Math.abs(e[0]));
            e[1] = 4.0f * e[1] / (1.0f + Math.abs(e[1]));


            for (j = 0; j < numDarkPoints; j++) {
                if (j != i) {
                    dx = darkPointsX[j] - x;
                    dy = darkPointsY[j] - y;
                    h = (float)Math.sqrt(dx * dx + dy * dy);
                    if (h == 0.0f) continue;
                    h3 = h * h * h;
                    e[0] += 200.0f * Math.max(-0.1f, Math.min(0.1f, dx / h3));
                    e[1] += 200.0f * Math.max(-0.1f, Math.min(0.1f, dy / h3));
                }
            }
            
            lightPointsXWrite[i] = Math.max(2.0f, Math.min((float)size - 2.0f, x + e[0]));
            lightPointsYWrite[i] = Math.max(2.0f, Math.min((float)size - 2.0f, y + e[1]));
        }
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
                    if (h <= 3.0f) continue;
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



    public void calcLightPointsIndex2() {
        float[] lightPointsX = getLightPointsX();
        float[] lightPointsY = getLightPointsY();
        float[] darkPointsX = getDarkPointsX();
        float[] darkPointsY = getDarkPointsY();

        float[] lightPointsXWrite = bufferSwitch ? lightPointsXB : lightPointsXA;
        float[] lightPointsYWrite = bufferSwitch ? lightPointsYB : lightPointsYA;
        float[] darkPointsXWrite = bufferSwitch ? darkPointsXB : darkPointsXA;
        float[] darkPointsYWrite = bufferSwitch ? darkPointsYB : darkPointsYA;

        float[] e = new float[2];
        float x, y, dx = 0.0f, dy = 0.0f, h, h3, bx, by, ex, ey, thetaMod = 0.0f;
        int i, j, jx, jy, ix, iy, lbx, lby, hbx, hby, ry, k, pidx, v;

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
                                if (h < 5.0f) {                                    
                                    //ex += 1.0f * dx * h3;
                                    //ey += 1.0f * dy * h3;
                                } else {
                                    ex += 20.0f * dx * h3;
                                    ey += 20.0f * dy * h3;
                                }
                            }
                        }
                        /*for(Iterator itr = darkIndex64Points[jx + jy*index64Width].iterator(); itr.hasNext();) {	      
                            pidx = (Integer)itr.next();
                            if (pidx != i) {
                                dx = darkPointsX[pidx] - x;
                                dy = darkPointsY[pidx] - y;
                                h = (float)Math.sqrt(dx * dx + dy * dy);
                                //if (h < 15.0f) h = 20.0f;
                                if (h > 100.0f) continue;
                                h3 = h * h * h;
                                if (h < 4.0f) {
                                    ex += dx / 5.0f;
                                    ey += dy / 5.0f;
                                } else {
                                    h3 *= 2.6;
                                    ex += 185.0f * dx / h3;
                                    ey += 185.0f * dy / h3;
                                }
                            }
                        }*/
                    }  
                }
            }

            float theta = (float)Math.atan2(ey, ex);
            float h2 = (float)Math.sqrt(ex*ex + ey*ey);
            thetaMod = 2.0f * thetaMod / (1.0f + Math.abs(thetaMod));
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
                                //if (h < 15.0f) h = 20.0f;
                                if (h > 100.0f) continue;
                                h3 = h * h * h;
                                if (h < 4.0f) {
                                    ex += dx / 5.0f;
                                    ey += dy / 5.0f;
                                } else {
                                    ex += 60.0f * dx / h3;
                                    ey += 60.0f * dy / h3;
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

    public void calcDarkPointsIndex2() {
        float[] lightPointsX = getLightPointsX();
        float[] lightPointsY = getLightPointsY();
        float[] darkPointsX = getDarkPointsX();
        float[] darkPointsY = getDarkPointsY();

        float[] darkPointsXWrite = bufferSwitch ? darkPointsXB : darkPointsXA;
        float[] darkPointsYWrite = bufferSwitch ? darkPointsYB : darkPointsYA;


        float[] e = new float[2];
        float x, y, dx = 0.0f, dy = 0.0f, h, h3, bx, by, ex, ey, cx, cy;
        int i, j, jx, jy, ix, iy, lbx, lby, hbx, hby, ry, k, pidx, v;

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
                                //if (h < 15.0f) h = 20.0f;
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
                                //if (h < 15.0f) h = 20.0f;
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

            /*for (j = 0; j < numLightPoints; j++) {
                if (j != i) {
                    dx = lightPointsX[j] - x;
                    dy = lightPointsY[j] - y;
                    h = (float)Math.sqrt(dx * dx + dy * dy);
                    h3 = h * h * h;
                    if (h < 1.0f) {
                        ex -= 32.0f * dx * h3;
                        ey -= 32.0f * dy * h3;
                    } else {
                        ex -= 32.0f * dx / h3;
                        ey -= 32.0f * dy / h3;
                    }
                }
            }*/
            
            darkPointsXWrite[i] = Math.max(14.0f, Math.min((float)size - 14.0f, ex));
            darkPointsYWrite[i] = Math.max(14.0f, Math.min((float)size - 14.0f, ey));
        }
    }    

    public void calcLightPoints2() {
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
                    h3 = h * h * h;
                    if (h < 1.0f) {
                        ex += 35.0f * dx * h3;
                        ey += 35.0f * dy * h3;
                    } else {
                        ex += 35.0f * dx / h3;
                        ey += 35.0f * dy / h3;
                    }                  
                }
            }
            ex = 3.0f * ex / (0.9f + Math.abs(ex));
            ey = 3.0f * ey / (0.9f + Math.abs(ey));
            for (j = 0; j < numDarkPoints; j++) {
                if (j != i) {
                    dx = darkPointsX[j] - x;
                    dy = darkPointsY[j] - y;
                    h = (float)Math.sqrt(dx * dx + dy * dy);
                    if (h > 100.0f) continue;
                    h3 = h * h * h;
                                       
                    if (h < 4.0f) {
                        ex += dx;
                        ey += dy;
                    } else {
                        h3 *= 2;
                        ex += 185.0f * dx / h3;
                        ey += 185.0f * dy / h3;
                    }
                }
            }
            //ex = 3.0f * ex / (0.9f + Math.abs(ex));
            //ey = 3.0f * ey / (0.9f + Math.abs(ey));
            
            lightPointsXWrite[i] = Math.max(14.0f, Math.min((float)size - 14.0f, x + ex));
            lightPointsYWrite[i] = Math.max(14.0f, Math.min((float)size - 14.0f, y + ey));
        }
    }

    public void calcDarkPoints2() {
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
        
        cy = (float)size / 2.0f;

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
            
            for (j = 0; j < numDarkPoints; j++) {
                if (j != i) {
                    dx = darkPointsX[j] - x;
                    dy = darkPointsY[j] - y;
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
            for (j = 0; j < numLightPoints; j++) {
                if (j != i) {
                    dx = lightPointsX[j] - x;
                    dy = lightPointsY[j] - y;
                    h = (float)Math.sqrt(dx * dx + dy * dy);
                    h3 = h * h * h;
                    if (h < 1.0f) {
                        ex -= 32.0f * dx * h3;
                        ey -= 32.0f * dy * h3;
                    } else {
                        ex -= 32.0f * dx / h3;
                        ey -= 32.0f * dy / h3;
                    }
                }
            }
            darkPointsXWrite[i] = Math.max(14.0f, Math.min((float)size - 14.0f, ex));
            darkPointsYWrite[i] = Math.max(14.0f, Math.min((float)size - 14.0f, ey));
        }
    }

    public void calcDarkPointsIndex() {
        float[] lightPointsX = getLightPointsX();
        float[] lightPointsY = getLightPointsY();
        float[] darkPointsX = getDarkPointsX();
        float[] darkPointsY = getDarkPointsY();

        float[] lightPointsXWrite = bufferSwitch ? lightPointsXB : lightPointsXA;
        float[] lightPointsYWrite = bufferSwitch ? lightPointsYB : lightPointsYA;
        float[] darkPointsXWrite = bufferSwitch ? darkPointsXB : darkPointsXA;
        float[] darkPointsYWrite = bufferSwitch ? darkPointsYB : darkPointsYA;


        float[] e = new float[2];
        float x, y, dx, dy, h, h3, cx, cy;
        int i, j, jx, jy, ix, iy, lbx, lby, hbx, hby, ry, k;
        cx = (float)size / 2.0f;
        cy = (float)size / 2.0f;

        for (i = 0; i < numDarkPoints; i++) {
            x = darkPointsX[i];
            y = darkPointsY[i];
            e[0] = x - ((x - cx) / 148.0f);
            e[1] = y - ((y - cy) / 148.0f);

            ix = (int)x;
            iy = (int)y;
            lbx = lowerBound(ix, 16);
            hbx = higherBound(ix, 16);
            lby = lowerBound(iy, 16);
            hby = higherBound(iy, 16);
            for (jy = lby; jy < hby; jy++) {
                for (jx = lbx; jx < hbx; jx++) {
                    calcBlock16d(e, x, y, jx, jy, darkIndex16, darkIndex4);
                }
            }

            lbx = lbx / 4;
            hbx = hbx / 4;
            lby = lby / 4;
            hby = hby / 4;
            for (jy = 0; jy < size/64; jy++) {
                for (jx = 0; jx < size/64; jx++) {
                    if (jy < lby || jy >= hby || jx < lbx || jx >= hbx) {
                        calcBlock64d(e, x, y, jx, jy, darkIndex64);
                    }
                }
            }


            darkPointsXWrite[i] = Math.max(2.0f, Math.min((float)size - 2.0f, e[0]));
            darkPointsYWrite[i] = Math.max(2.0f, Math.min((float)size - 2.0f, e[1]));
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
            ex = x - ((x - cx) / 1000.0f);
            ey = y - ((y - cy)  / 1000.0f);
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

    public static void main(String[] args) 
    { 
        // Initializing an deque 
        Deque<Integer> de_que = new ArrayDeque<Integer>(10); 
   
        // add() method to insert 
        de_que.add(10); 
        de_que.add(20); 
        de_que.add(30); 
        de_que.add(40); 
        de_que.add(50); 
        for (Integer element : de_que) 
        { 
            System.out.println("Element : " + element); 
        } 
   
        System.out.println("Using clear() "); 
  
        // clear() method 
        de_que.clear(); 
   
        // addFirst() method to insert at start 
        de_que.addFirst(564); 
        de_que.addFirst(291); 
   
        // addLast() method to insert at end 
        de_que.addLast(24); 
        de_que.addLast(14); 
   
        System.out.println("Above elements are removed now"); 
   
        // Iterator() : 
        System.out.println("Elements of deque using Iterator :"); 
        for(Iterator itr = de_que.iterator(); itr.hasNext();) 
        { 
            System.out.println(itr.next()); 
        } 
   
        // descendingIterator() : to reverse the deque order 
        System.out.println("Elements of deque in reverse order :"); 
        for(Iterator dItr = de_que.descendingIterator();  
                                               dItr.hasNext();) 
        { 
            System.out.println(dItr.next()); 
        } 
   
        // element() method : to get Head element 
        System.out.println("\nHead Element using element(): " + 
                                             de_que.element()); 
   
        // getFirst() method : to get Head element 
        System.out.println("Head Element using getFirst(): " +  
                                               de_que.getFirst()); 
   
        // getLast() method : to get last element 
        System.out.println("Last Element using getLast(): " +  
                                                de_que.getLast()); 
   
        // toArray() method : 
        Object[] arr = de_que.toArray(); 
        System.out.println("\nArray Size : " + arr.length); 
   
        System.out.print("Array elements : "); 
        for(int i=0; i<arr.length ; i++) 
            System.out.print(" " + arr[i]); 
              
        // peek() method : to get head 
        System.out.println("\nHead element : " + de_que.peek()); 
          
        // poll() method : to get head 
        System.out.println("Head element poll : " + de_que.poll()); 
          
        // push() method : 
        de_que.push(265); 
        de_que.push(984); 
        de_que.push(2365); 
          
        // remove() method : to get head 
        System.out.println("Head element remove : " + de_que.remove()); 
          
        System.out.println("The final array is: "+de_que); 
    } 
}