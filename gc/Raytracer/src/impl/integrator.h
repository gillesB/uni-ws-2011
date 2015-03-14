#ifndef __INCLUDE_GUARD_D2CAE848_1C1E_4362_8DFE_163B2EA2A97D
#define __INCLUDE_GUARD_D2CAE848_1C1E_4362_8DFE_163B2EA2A97D
#ifdef _MSC_VER
#pragma once
#endif

#include "../rt/basic_definitions.h"
#include "phong_shaders.h"

struct PointLightSource {
    Point position;
    float4 intensity, falloff;
    //falloff formula: (.x  / dist^2 + .y / dist + .z) * intensity;
};

struct DepthStateKey {
    typedef int t_data;
};

class IntegratorImpl : public Integrator {
public:

    enum {
        _MAX_BOUNCES = 10
    };

    GeometryGroup *scene;
    std::vector<PointLightSource> lightSources;
    float4 ambientLight;

    IntegratorImpl() {
        state.value<DepthStateKey > () = 0;
    }

    /**
     * getRadiance calculates the total radiance of the intersection-point,
     * by calling getReflectance for every light source and adding these values,
     * then the indirect radiance on this point is calculated and added to the total radiance
     * @param _ray
     * @return
     */
    virtual float4 getRadiance(const Ray &_ray) {
        state.value<DepthStateKey > ()++;

        float4 col = float4::rep(0);

        if (state.value<DepthStateKey > () < _MAX_BOUNCES) {
            Primitive::IntRet ret = scene->intersect(_ray, FLT_MAX);
            if (ret.distance < FLT_MAX && ret.distance >= Primitive::INTEPS()) {
                SmartPtr<Shader> shader = scene->getShader(ret);

                if (shader.data() != NULL) {
                    col += shader->getAmbientCoefficient() * ambientLight;

                    Point intPt = _ray.o + ret.distance * _ray.d;

                    for (std::vector<PointLightSource>::const_iterator lightSource = lightSources.begin(); lightSource != lightSources.end(); lightSource++) {
                        float transparencyFactor = visibleLS(intPt, lightSource->position);
                        if (transparencyFactor) {
                            Vector lightD = lightSource->position - intPt;
                            float4 refl = shader->getReflectance(-_ray.d, lightD);
                            float dist = lightD.len();
                            float fallOff = lightSource->falloff.x / (dist * dist) + lightSource->falloff.y / dist + lightSource->falloff.z;
                            col += refl * float4::rep(fallOff) * lightSource->intensity * float4::rep(transparencyFactor);
                        }
                    }
                    col += shader->getIndirectRadiance(-_ray.d, this);
                }
            }
        }

        state.value<DepthStateKey > ()--;

        return col;
    }
private:


    float visibleLS(const Point& _pt, const Point& _pls) {
        Ray r;
        r.d = _pls - _pt;
        r.o = _pt + Primitive::INTEPS() * r.d;

        Primitive::IntRet ret = scene->intersect(r, 1.1f);
        if (ret.distance < 1.1f && ret.distance >= Primitive::INTEPS()) {
            SmartPtr<Shader> shader = scene->getShader(ret);
            if (shader.data() != NULL) {
                Point intPt = r.o + ret.distance * r.d;
                float transparency = shader->getTransparency()/2;
                if(transparency == 0){
                    return 0;
                } else {
                    return transparency * visibleLS(intPt,_pls);
                }
            } else {
                return 1;
            }
        } else {
            return 1;
        }

    }

};


#endif //__INCLUDE_GUARD_D2CAE848_1C1E_4362_8DFE_163B2EA2A97D
