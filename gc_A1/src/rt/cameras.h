#ifndef __CAMERAS_H_INCLUDED_5EF03557_9D56_459F_A0D7_F3933088981E
#define __CAMERAS_H_INCLUDED_5EF03557_9D56_459F_A0D7_F3933088981E
#ifdef _MSC_VER
#pragma once
#endif

#include "../core/algebra.h"
#include "basic_definitions.h"
#include <math.h>

//A perspective camera implementation
//TODO: Implement the necessary methods here for a perspective camera

class PerspectiveCamera : public Camera {
private:
    Point pos, pos00;
    Vector direction, up, spanX;
    float angle, distance;
    float resX, resY, imageWidth, imageHeight;

public:

    PerspectiveCamera(const Point &_center, const Vector &_forward, const Vector &_up,
            float _horizOpeningAngInGrad, std::pair<uint, uint> _resolution) {
        pos = _center;
        direction = ~_forward;
        up = _up;
        angle = _horizOpeningAngInGrad;
        resX = _resolution.first;
        resY = _resolution.second;
        distance = _forward.len();
        spanX = up % direction;
        imageWidth = 2 * distance * tan(angle/2.);
        imageHeight = imageWidth * ( resY / resX);
        pos00 = pos + distance * direction - imageWidth / 2. * spanX + imageHeight / 2. * up;
    }

    virtual Ray getPrimaryRay(float _x, float _y) {
        Ray ret;
        ret.d = (pos00 + imageWidth * (_x / (resX - 1)) * spanX - imageHeight * (_y / (resY - 1)) * up) - pos;
        ret.d = ~ret.d;
        ret.o = pos;
        return ret;
    }
};

#endif //__CAMERAS_H_INCLUDED_5EF03557_9D56_459F_A0D7_F3933088981E
