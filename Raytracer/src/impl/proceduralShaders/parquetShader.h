/*
 * This Header was written by Gilles Baatz, please do NOT consider this part for the grading.
 */

#ifndef PARQUETSHADER_H
#define PARQUETSHADER_H

#include "proceduralShaders.h"

/**
 * a shader which can be used to shade a wooden parquet
 * the original file was found here:
 * http://www.cgl.uwaterloo.ca/~tjlahey/cgl_talks/apr12_2000/shaders/DWParquetTile.sl
 * @param _outDir
 * @param _inDir
 * @return
 */
class ParquetShader : public RenderManLikeShader {
#define boxstep(a,b,x) (clamp(((x)-(a))/((b)-(a)),0,1))
#define MINFILTERWIDTH 1.0e-7

public:

    float plankspertile,plankvary,groovewidth;
    bool turnPlanks;

    ParquetShader(){
plankspertile = 5;
plankvary = 0.8;
turnPlanks = true;
groovewidth = 0.01;
    }

    virtual float4 getDiffuseCoefficient() const{
        float Ka = 1, Kd = 0.75, Ks = .15, roughness = .025;
        float4 specularcolor = float4::rep(1);
        float ringscale = 25, grainscale = 55;
        float txtscale = 2;
        float4 lightwood = float4(0.57, 0.292, 0.125, 0);
        float4 darkwood = float4(0.275, 0.15, 0.06, 0);
        float4 groovecolor = float4(.05, .04, .015, 0);
        float plankwidth = 1;
        float grainy = 1, wavy = 0.08;

        float s = position.x;
        float t = position.y;

        float r, r2;
        Vector Nf;
        float whichrow, whichplank;
        float swidth, twidth, fwidth, ss, tt, w, h, fade, ttt;
        float4 Ct, woodcolor;
        float groovy;
        float PGWIDTH, PGHEIGHT, GWF, GHF;
        float tilewidth, whichtile, tmp, planklength;

        PGWIDTH = plankwidth + groovewidth;
        planklength = PGWIDTH * plankspertile - groovewidth;
        PGHEIGHT = planklength + groovewidth;
        GWF = groovewidth * 0.5 / PGWIDTH;
        GHF = groovewidth * 0.5 / PGHEIGHT;

        /* Determine how wide in s-t space one pixel projects to */
//        swidth = .1; //(max(abs(Du(s) * du) + abs(Dv(s) * dv), MINFILTERWIDTH) / PGWIDTH) * txtscale;
//        twidth = .1; //(max(abs(Du(t) * du) + abs(Dv(t) * dv), MINFILTERWIDTH) / PGHEIGHT) * txtscale;
        swidth = .01; //(max(abs(Du(s) * du) + abs(Dv(s) * dv), MINFILTERWIDTH) / PGWIDTH) * txtscale;
        twidth = .01; //(max(abs(Du(t) * du) + abs(Dv(t) * dv), MINFILTERWIDTH) / PGHEIGHT) * txtscale;
        fwidth = std::max(swidth, twidth);

        Nf = getNormal(); //faceforward(normalize(N), I);

        ss = (txtscale * s) / PGWIDTH;
        whichrow = floor(ss);
        tt = (txtscale * t) / PGHEIGHT;
        whichplank = floor(tt);
        if (turnPlanks && mod(whichrow / plankspertile + whichplank, 2) >= 1) {
            ss = txtscale * t / PGWIDTH;
            whichrow = floor(ss);
            tt = txtscale * s / PGHEIGHT;
            whichplank = floor(tt);
            tmp = swidth;
            swidth = twidth;
            twidth = tmp;
        }
        ss -= whichrow;
        tt -= whichplank;
        whichplank += 20 * (whichrow + 10);

        /*
         * Figure out where the grooves are.  The value groovy is 0 where there
         * are grooves, 1 where the wood grain is visible.  Do some simple
         * antialiasing.
         */
        if (swidth >= 1)
            w = 1 - 2 * GWF;
        else w = clamp(boxstep(GWF - swidth, GWF, ss), std::max(1 - GWF / swidth, (float) 0), 1)
            - clamp(boxstep(1 - GWF - swidth, 1 - GWF, ss), 0, 2 * GWF / swidth);
        if (twidth >= 1)
            h = 1 - 2 * GHF;
        else h = clamp(boxstep(GHF - twidth, GHF, tt), std::max(1 - GHF / twidth, (float) 0), 1)
            - clamp(boxstep(1 - GHF - twidth, 1 - GHF, tt), 0, 2 * GHF / twidth);
        /* This would be the non-antialiased version:
         * w = step (GWF,ss) - step(1-GWF,ss);
         * h = step (GHF,tt) - step(1-GHF,tt);
         */
        groovy = w*h;


        /*
         * Add the ring patterns
         */
        fade = smoothstep(1 / ringscale, 8 / ringscale, fwidth);
        if (fade < 0.999) {
            
            ttt = tt / 4 + whichplank / 28.38 + wavy * noise(8 * ss, tt / 4, 0);
            r = ringscale * noise(ss - whichplank, ttt, 0);
            r -= floor(r);
            r = 0.3 + 0.7 * smoothstep(0.2, 0.55, r) * (1 - smoothstep(0.75, 0.8, r));
            r = (1 - fade) * r + 0.65 * fade;

            /*
             * Multiply the ring pattern by the fine grain
             */
            fade = smoothstep(2 / grainscale, 8 / grainscale, fwidth);
            if (fade < 0.999) {
                r2 = 1.3 - noise(ss*grainscale, (tt * grainscale / 4), 0);
                r2 = grainy * r2 * r2 + (1 - grainy);
                r *= (1 - fade) * r2 + (0.75 * fade);
            } else r *= 0.75;
        } else r = 0.4875;


        /* Mix the light and dark wood according to the grain pattern */
        woodcolor = mix(lightwood, darkwood, r);

        /* Add plank-to-plank variation in overall color */
        woodcolor *= float4::rep((1 - plankvary / 2 + plankvary * noise(whichplank + 0.5, 0, 0)));

        Ct = mix(groovecolor, woodcolor, groovy);
        return Ct;
    }

    _IMPLEMENT_CLONE(ParquetShader);
};



#endif  /* PARQUETSHADER_H */

