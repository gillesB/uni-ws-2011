#ifndef __INCLUDE_GUARD_6A0F8987_914D_41B8_8E51_53B29CF1A045
#define __INCLUDE_GUARD_6A0F8987_914D_41B8_8E51_53B29CF1A045
#ifdef _MSC_VER
#pragma once
#endif


#include "../core/image.h"


//Specifies where the center of the texel is.
//Currently the value 0.5 means that (0.5, 0.5)
//	in normalized texture coordinates will correspond
//	to the center of the the pixel (0,0) in the image
//	This is quite visible with bilinear filtering, since
//	texel (1, 1) will actually be 
//	1/4 (pixel(0, 0) + pixel(0, 1) + pixel(1, 0) + pixel(1, 1)),
//	whereas texel(0.5, 0.5) will be = pixel(0, 0) of the imagge
#define _TEXEL_CENTER_OFFS 0.5f

//A texture class

class Texture : public RefCntBase {
public:
    //What to do if the texture coordinates are outside
    //	of the texture

    enum TextureAddressMode {
        TAM_Wrap, //Wrap around 0. Results in a repeated texture
        TAM_Border, //Clamp the coordinate to the border.
        //Results in the border pixels repeated
    };

    //The texture filtering mode. It affects magnifaction only

    enum TextureFilterMode {
        TFM_Point,
        TFM_Bilinear
    };

    SmartPtr<Image> image;

    TextureAddressMode addressModeX, addressModeY;
    TextureFilterMode filterMode;

    Texture() {
        addressModeX = TAM_Wrap;
        addressModeY = TAM_Wrap;
        filterMode = TFM_Point;
    }

    //Sample the texture. Coordinates are normalized:
    //	(0, 0) corresponds to pixel (0, 0) in the image and
    //	(1, 1) - to pixel (width - 1, height - 1) of the image,
    //	if doing point sampling

    float4 sample(const float2& _pos) const {
        //Denormalize the texture coordinates and offset the center
        //	of the texel
        float2 pos =
                _pos * float2((float) image->width(), (float) image->height())
                + float2(_TEXEL_CENTER_OFFS, _TEXEL_CENTER_OFFS);

        //wrap around
        while (pos.x >= image->width()) {
            pos.x -= image->width();
        }
        while (pos.y >= image->height()) {
            pos.y -= image->height();
        }

        //TODO: Implement point and bilinear texture sampling
        if (filterMode == TFM_Point) {
            uint x = pos.x;
            uint y = pos.y;
            return (*image)(x, y);
        } else if (filterMode == TFM_Bilinear) {
            return bilinearInterpolation(pos);
        } else {
            return float4::rep(0.f);
        }
    }

private:

    float4 bilinearInterpolation(const float2 &pos) const {
        int x = (uint) pos.x;
        int y = (uint) pos.y;
        float diffX = pos.x - x;
        float diffY = pos.y - y;

        //check if pos is center of texel
        float EPSILON = 0.000001;
        if (fabs(diffX - 0.5) < EPSILON && fabs(diffY - 0.5) < EPSILON) {
            return (*image)(x, y);
        }

        //fetch 4 colors to make the bilinear interpolation
        int nextTexelX, nextTexelY;
        nextTexelX = diffX < 0.5 ? -1 : 1;
        nextTexelY = diffY < 0.5 ? -1 : 1;

        //wrap the other coordinates
        if (x + nextTexelX < 0) {
            nextTexelX += image->width();
        } else if (x + nextTexelX >= image->width()) {
            nextTexelX -= image->width();
        }
        nextTexelX = x + nextTexelX;
        if (y + nextTexelY < 0) {
            nextTexelY += image->height();
        } else if (y + nextTexelY >= image->height()) {
            nextTexelY -= image->height();
        }
        nextTexelY = y + nextTexelY;

        //fetch the 4 colors
        float4 color1 = (*image)(x, y);
        float4 color2 = (*image)(nextTexelX, y);
        float4 color3 = (*image)(x, nextTexelY);
        float4 color4 = (*image)(nextTexelX, nextTexelY);

        //make the bilinear Interpolation
        float4 interpol1 = lerp(color1, color2, x, nextTexelX, pos.x);
        float4 interpol2 = lerp(color3, color4, x, nextTexelX, pos.x);

        return lerp(interpol1, interpol2, y, nextTexelY, pos.y);;
    }

    float4 lerp(float4 color_from, float4 color_to, float from, float to, float direction) const {
        return ( color_from + float4::rep((direction - from) / (to - from)) * (color_to - color_from));
    }
};


#endif //__INCLUDE_GUARD_6A0F8987_914D_41B8_8E51_53B29CF1A045
