/*
 * This Header was written by Paul Hirtz, please DO consider this part for the grading.
 */

#ifndef BUMPMAPSHADER_H
#define	BUMPMAPSHADER_H

#include "phong_shaders.h"


/**
 * A textured phong shader that supports bump mapping based on a height-map,
 * where the diffrent heights are stored as grey-values.
 */

class BumpmapShader : public TexturedPhongShader {
  
public:

    virtual float4 getAmbientCoefficient() const {
        float4 ret = TexturedPhongShader::getAmbientCoefficient();
        ret += bumpTexture->sample(m_texCoord);
        return ret;
    }
    

    virtual Vector getNormal() const {
        if (bumpTexture.data() == NULL){
            return m_normal;
        }

        // Coordinates Texels around the sampling point

        float xfactor = 1 / bumpTexture->getWidth();
        float yfactor = 1 / bumpTexture->getHeight();
        float x = m_texCoord.x;
        float y = m_texCoord.y;
        float xplus1 = x + xfactor;
        float yplus1 = y + yfactor;

        // look at the right and top of itself, to calculate the differences
        float4 top = bumpTexture->sample(float2(x, yplus1));
        float4 right = bumpTexture->sample(float2(xplus1, y));
        float4 self = bumpTexture->sample(float2(x, y));

        float height_top = top.x * bumpIntensity;
        float height_right = right.x * bumpIntensity;
        float height_self = self.x * bumpIntensity;

        Point p_top = Point(x, yplus1, height_top);
        Point p_right = Point(xplus1, y, height_right);
        Point p_self = Point(x,y,height_self);

        Vector diff1 = p_right - p_self; 
        Vector diff2 = p_top - p_self;

        return ~(m_normal + diff1 + diff2);
    }


    _IMPLEMENT_CLONE(BumpmapShader);
};

#endif	/* BUMPMAPSHADER_H */
