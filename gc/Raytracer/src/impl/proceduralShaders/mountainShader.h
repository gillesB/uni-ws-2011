/*
 * This Header was written by Gilles Baatz, please do NOT consider this part for the grading.
 */
#ifndef MOUNTAINSHADER_H
#define MOUNTAINSHADER_H
#include "proceduralShaders.h"

/*
 *  This procedural shader is used for coloring the mountain depending on the slope.
 */
class MountainShader : public RenderManLikeShader {
public:

    virtual float4 getDiffuseCoefficient() const {

        float pseudoRandom = fabs(noise(position)-(int) noise(position))*2;
        Vector normal = getNormal();
        float slope = fabs(Vector(0, 1, 0) * normal);

        float4 brownGray = float4(0.239215686, 0.22745098, 0.090196078, 0);
        float4 greenBrown = float4(0.352941176, 0.317647059, 0.282352941, 0);

        float4 hill = mix(brownGray, greenBrown, smoothstep(0.4, 0.55, slope));

        float4 snowWhite = float4(0.819607843, 0.819607843, 0.819607843, 0);
        float4 gray = float4(0.529411765, 0.529411765, 0.549019608, 0);
        float4 snow = mix(snowWhite, gray, smoothstep(0.55, 0.90, slope));

        float4 mountainColorSlope = mix(hill, snow, smoothstep(0.6, 0.95, slope));
        mountainColorSlope = mix(mountainColorSlope, snow, smoothstep(0.4, 0.8, pseudoRandom));

        float4 mountainColor = mix(mountainColorSlope, snow, smoothstep(1.8, 2.5, position.z));

        float4 Cd = mountainColor;
        return Cd;
    }


    _IMPLEMENT_CLONE(MountainShader);
};

#endif  /* MOUNTAINSHADER_H */

