/*
 * This Header was written by Gilles Baatz, please do NOT consider this part for the grading.
 */

#ifndef WOODSHADER_H
#define WOODSHADER_H

#include "proceduralShaders.h"

/**
 * a shader which can be used to shade wooden objects
 * the original file was found on the website http://www.yaldex.com/open-gl/ch15lev1sec7.html
 * @param _outDir
 * @param _inDir
 * @return
 */
class WoodShader : public RenderManLikeShader {
private:

    float fract(float f) const {
        return f - (int) f;
    }

public:
    float4 lightwood;
    float4 darkwood;

    WoodShader() {
        lightwood = float4(0.345098039, 0.215686275, 0.188235294, 0);
        darkwood = float4(0.05, 0.01, 0.005, 0);
    }

    virtual float4 getDiffuseCoefficient() const {

        float Noisiness = 0.1;
        float RingFreq = 100;
        float GrainScale = 100;
        float GrainThreshold = 0.5;
        float LightGrains = 1;
        float DarkGrains = 0.1;


        Point usePosition = Point(position.x, position.y, position.z);

        float Noise = noise(usePosition) * Noisiness;
        float4 location = float4(usePosition.x + Noise, usePosition.y + Noise, usePosition.z + Noise, 0);
        //float4 location = float4(Noise, Noise, Noise, 0);

        float dist = sqrt(location.x * location.x + location.z * location.z);
        dist *= RingFreq;

        //float r = fract(dist + location.x + location.y + location.z) * 2.0;
        float r = fract(dist + Noise + Noise + Noise) * 2.0;

        if (r > 1.0) {
            //r = 2.0 - r;
            r = fract(r);
        }
        float4 color = mix(lightwood, darkwood, r);

        r = fract((usePosition.x + usePosition.z) * GrainScale + 0.5);
        //location[2] *= r;
        Noise *= r;
        if (r < GrainThreshold)
            color += lightwood * float4::rep(LightGrains) * float4::rep(Noise);
        else
            color -= lightwood * float4::rep(DarkGrains) * float4::rep(Noise);
        //color *= LightIntensity;
        //gl_FragColor = vec4(color, 1.0);

        return color;




    }

    _IMPLEMENT_CLONE(WoodShader);
};



#endif  /* WOODSHADER_H */
