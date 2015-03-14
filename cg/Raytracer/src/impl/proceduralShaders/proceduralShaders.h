/*
 * This Header was written by Gilles Baatz, please do NOT consider this part for the grading.
 */

#ifndef PROCEDURALSHADERS_H
#define PROCEDURALSHADERS_H

#include "../phong_shaders.h"

/**
 * Some usefull functions to make procedural shaders in a RenderMan-like way.
 * The functions were copied from the book "Texturing and Modeling A Procedural Approach" by David S. Ebert et al.
 */
class RenderManFunctions {
#define TABSIZE 256
#define TABMASK (TABSIZE-1)
#define PERM(x) perm[(x) & TABMASK]
#define INDEX(ix,iy,iz) PERM((ix)+PERM((iy)+PERM(iz)))

#define FLOOR(x) ((int)(x) - ((x) < 0 && (x) != (int)(x)))

#define RANDMASK 0xfffffff
#define RANDNBR ((random() & RANDMASK)/(float) RANDMASK)

private:
    float gradientTab[TABSIZE * 3];

    static unsigned char perm[TABSIZE];

    void gradientTabInit(int seed) {
        float *table = gradientTab;
        float z, r, theta;
        int i;
        srandom(seed);
        for (i = 0; i < TABSIZE; i++) {
            z = 1. - 2. * RANDNBR;
            /* r is radius of x,y circle */
            r = sqrtf(1 - z * z);
            /* theta is angle in (x,y) */
            theta = 2 * M_PI * RANDNBR;
            *table++ = r * cosf(theta);
            *table++ = r * sinf(theta);
            *table++ = z;
        }
    }

    float glattice(int ix, int iy, int iz,
            float fx, float fy, float fz) const {
        const float *g = &gradientTab[ INDEX(ix, iy, iz) *3];
        return g[0] * fx + g[1] * fy + g[2] * fz;
    }


public:

    RenderManFunctions() {
        gradientTabInit(665);
    }

    float clamp(float x, float a, float b) const {
        return (x < a ? a : (x > b ? b : x));
    }

#define LERP(t,x0,xl) ((x0) + (t)*((xl)-(x0)))
#define SMOOTHSTEP(x) ((x)*(x)*(3 - 2*(x)))

    float noise(float x, float y, float z) const {
        int ix, iy, iz;
        float fx0, fx1, fy0, fy1, fz0, fz1;
        float wx, wy, wz;
        float vx0, vx1, vy0, vy1, vz0, vz1;

        ix = FLOOR(x);
        fx0 = x - ix;
        fx1 = fx0 - 1;
        wx = SMOOTHSTEP(fx0);
        iy = FLOOR(y);
        fy0 = y - iy;
        fy1 = fy0 - 1;
        wy = SMOOTHSTEP(fy0);
        iz = FLOOR(z);
        fz0 = z - iz;
        fz1 = fz0 - 1;
        wz = SMOOTHSTEP(fz0);

        vx0 = glattice(ix, iy, iz, fx0, fy0, fz0);
        vx1 = glattice(ix + 1, iy, iz, fx1, fy0, fz0);
        vy0 = LERP(wx, vx0, vx1);
        vx0 = glattice(ix, iy + 1, iz, fx0, fy1, fz0);
        vx1 = glattice(ix + 1, iy + 1, iz, fx1, fy1, fz0);
        vy1 = LERP(wx, vx0, vx1);
        vz0 = LERP(wy, vy0, vy1);
        vx0 = glattice(ix, iy, iz + 1, fx0, fy0, fz1);
        vx1 = glattice(ix + 1, iy, iz + 1, fx1, fy0, fz1);
        vy0 = LERP(wx, vx0, vx1);
        vx0 = glattice(ix, iy + 1, iz + 1, fx0, fy1, fz1);
        vx1 = glattice(ix + 1, iy + 1, iz + 1, fx1, fy1, fz1);
        vy1 = LERP(wx, vx0, vx1);
        vz1 = LERP(wy, vy0, vy1);

        return LERP(wz, vz0, vz1);
    }

    float noise(float4 f) const {
        return noise(f.x, f.y, f.z);
    }

    float noise(Point p) const {
        return noise(p.x, p.y, p.z);
    }



    /* Coefficients of basis matrix. */
#define CROO -0.5
#define CR01 1.5
#define CR02 -1.5
#define CR03 0.5
#define CR10 1.0
#define CR11 -2.5
#define CR12 2.0
#define CR13 -0.5
#define CR20 -0.5
#define CR21 0.0
#define CR22 0.5
#define CR23 0.0
#define CR30 0.0
#define CR31 1.0
#define CR32 0.0
#define CR33 0.0

    float spline(float x, int nknots, float *knot) const {
        int span;
        int nspans = nknots - 3;
        float cO, c1, c2, c3; /* coefficients of the cubic.*/
        if (nspans < 1) {/* illegal */
            fprintf(stderr, "Spline has too few knots.\n");
            return 0;
        }
        /* Find the appropriate 4-point span of the spline. */
        x = clamp(x, 0, 1) * nspans;
        span = (int) x;
        if (span >= nknots - 3)
            span = nknots - 3;
        x -= span;
        knot += span;
        /* Evaluate the span cubic at x using Horner’s rule. */
        c3 = CROO * knot[0] + CR01 * knot[1] + CR02 * knot[2] + CR03 * knot[3];
        c2 = CR10 * knot[0] + CR11 * knot[1] + CR12 * knot[2] + CR13 * knot[3];
        c1 = CR20 * knot[0] + CR21 * knot[1] + CR22 * knot[2] + CR23 * knot[3];
        cO = CR30 * knot[0] + CR31 * knot[1] + CR32 * knot[2] + CR33 * knot[3];


        return ((c3 * x + c2) * x + c1)*x + cO;
    }

    float4 spline(float x, int nknots, const float4 *knot) const {
        float x_values[nknots];
        float y_values[nknots];
        float z_values[nknots];

        for (int i = 0; i < nknots; i++) {
            x_values[i] = knot[i].x;
            y_values[i] = knot[i].y;
            z_values[i] = knot[i].z;
        }

        float4 ret = float4(spline(x, nknots, x_values), spline(x, nknots, y_values), spline(x, nknots, z_values), 0);
        return ret;
    }

    float mod(float a, float b) const {
        int n = (int) (a / b);
        a -= n*b;
        if (a < 0)
            a += b;
        return a;
    }

    float smoothstep(float a, float b, float x) const {
        if (x < a)
            return 0;
        if (x >= b)
            return 1;
        x = (x - a) / (b - a);
        return (x * x * (3 - 2 * x));
    }

    float4 mix(float4 C0, float4 Cl, float f) const {
        return float4::rep((1 - f)) * C0 + float4::rep(f) * Cl;
    }

    float mix(float C0, float Cl, float f) const {
        return (1 - f) * C0 + f * Cl;
    }

    /*
     * Procedural fBm evaluated at “point”.
     *
     * Parameters:
     * “H” is the fractal increment parameter (between 0 and 1. 1 = smooth; 0 = nearly white noise)
     * “lacunarity” is the gap between successive frequencies
     * “octaves” is the number of frequencies in the fBm
     */
    double fBm(Vector point, float H, float lacunarity, float octaves) const {
        float value, remainder;
        int i;
        value = 0.0;
        /* inner loop of fractal construction */
        for (i = 0; i < octaves; i++) {
            value += noise(point) * pow(lacunarity, -H * i);
            point *= lacunarity;
        }
        remainder = octaves - (int) octaves;
        if (remainder) /* add in “octaves” remainder */
            /* ‘i’ and spatial freq. are preset in loop above */
            //value += remainder * Noise3(point) * pow(lacunarity, -H * i);
            value += remainder * noise(point) * pow(lacunarity, -H * i);
        return value;
    }

};

/**
 * a base class for a shader using the RenderManFunctions
 *
 */
class RenderManLikeShader : public RenderManFunctions, public DefaultPhongShader {
public:
    Point position;

    virtual void setPosition(const Point& point) {
        position = point;
    }

    virtual float4 getDiffuseCoefficient() const = 0;

    virtual void getCoeff(float4 &_diffuseCoef, float4 &_specularCoef, float &_specularExponent) const {
        _diffuseCoef = getDiffuseCoefficient();
        _specularCoef = specularCoef;
        _specularExponent = specularExponent;
    }

    virtual float4 getAmbientCoefficient() const {
        return getDiffuseCoefficient();
    }
};

/**
 * a marble like shader.
 * This shader was found in the book "Texturing and Modeling A Procedural Approach" by David S. Ebert et al. page 87, 88
 */
class MarbleLikeShader : public RenderManLikeShader {
#define PALE_BLUE        float4 (0.25, 0.25, 0.35, 0)
#define MEDIUM_BLUE      float4 (0.10, 0.10, 0.30, 0)
#define DARK_BLUE        float4 (0.05, 0.05, 0.26, 0)
#define DARKER_BLUE      float4 (0.03, 0.03, 0.20, 0)
#define NNOISE           4
#define snoise(x) (2 * noise(x) - 1)

private:
    float texturescale;
    float roughness;

    static float4 colors[13];
public:

    MarbleLikeShader(float texturescale = 2.5, float roughness = 0.1) {
        this->texturescale = texturescale;
        this->roughness = roughness;
    }

    float4 marble_color(float m) const {
        //float clampRet = clamp(2 * -m + .75, 0, -1);
        float clampRet = clamp(-m - 1.5, 0, 1);
        return spline(clampRet, 13, colors);
    }

    virtual float4 getDiffuseCoefficient() const{
        float4 Ct;
        Vector NN;
        Vector PP;
        float i, f, marble;

        NN = getNormal(); //normalize(faceforward(N, I));
        PP = Vector(position.x, position.y, position.z) * texturescale;

        marble = 0;
        f = 1;
        for (i = 0; i < NNOISE; i += 1) {
            marble += snoise(PP * f) / f;
            f *= 2.17;
        }
        Ct = marble_color(marble);
        return Ct;
    }


    _IMPLEMENT_CLONE(MarbleLikeShader);
};



#endif  /* PROCEDURALSHADERS_H */

