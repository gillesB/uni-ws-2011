/*
 * This Header was written by Paul Hirtz, please DO consider this part for the grading.
 */

#ifndef MIRRORSHADER_H
#define	MIRRORSHADER_H

#include "phong_shaders.h"

class MirrorPhongShader : public DefaultPhongShader {
protected:
    float2 m_texCoord;
    Point m_position;
    Vector m_tang, m_biNorm;
public:
    float reflCoef;

    virtual void setPosition(const Point& point) {
        m_position = point;
    }

    /**
     * Calculates the radiance that does not come from direct illumination (reflected rays)
     * this is done by forwarding the reflected ray and calculating the radiance of it
     * @param out
     * @param integrator
     * @return
     */
    virtual float4 getIndirectRadiance(const Vector& out, Integrator* integrator) const {
        Ray r;
        r.o = m_position;

        Vector n = getNormal();
        Vector v = n * fabs(n * out);
        r.d = out + 2 * (v - out);

        return integrator->getRadiance(r) * float4::rep(reflCoef);
    }

    _IMPLEMENT_CLONE(MirrorPhongShader);

};

#endif	/* MIRRORSHADER_H */

