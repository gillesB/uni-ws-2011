#include "stdafx.h"

#include "core/image.h"
#include "rt/basic_definitions.h"
#include "impl/basic_primitives.h"
#include "impl/perspective_camera.h"
#include "impl/samplers.h"
#include <time.h>

struct EyeLightIntegrator : public Integrator {
public:
    Primitive *scene;

    virtual float4 getRadiance(const Ray & _ray) {
        float4 col = float4::rep(0);

        Primitive::IntRet ret = scene->intersect(_ray, FLT_MAX);
        if (ret.distance < FLT_MAX && ret.distance >= Primitive::INTEPS()) {
            SmartPtr<Shader> shader = scene->getShader(ret);
            if (shader.data() != NULL)
                col += shader->getReflectance(-_ray.d, -_ray.d);
        }

        return col;
    }
};

//A check board shader in 3D, dependent only of the position in space

class CheckBoard3DShader : public PluggableShader {
    Point m_position;
public:
    //Only the x, y, and z are taken into account
    float4 scale;

    virtual void setPosition(const Point& _point) {
        m_position = _point;
    }

    virtual float4 getReflectance(const Vector &_outDir, const Vector &_inDir) const {
        //TODO: Implement a check board shader, depending only on the position.
        //Return
        //	white for all points in [0 .. 1 / scale.x] x [0 .. 1 / scale.y] x [0 .. 1 / scale.z]
        //	black for all points in [1 / scale.x .. 2 / scale.x] x [0 .. 1 / scale.y] x [0 .. 1 / scale.z]
        //	white for all points in [2 / scale.x .. 3 / scale.x] x [0 .. 1 / scale.y] x [0 .. 1 / scale.z]
        //	black for all points in [0 / scale.x .. 1 / scale.x] x [1 .. 2 / scale.y] x [0 .. 1 / scale.z]
        //	and so on ...

        //take the fractional of the coordinates, to get a value between [0..1[
        float4 ret = float4::rep(0.8f);
        double diffX = fabs(m_position.x - (int) m_position.x);
        double diffZ = fabs(m_position.z - (int) m_position.z);

        if (diffX >= 0 && diffX <= 1 / scale.x || diffX >= 2 / scale.x && diffX <= 3 / scale.x) {
            if (diffZ >= 0 && diffZ <= 1 / scale.z || diffZ >= 2 / scale.z && diffZ <= 3 / scale.z) {
                ret = float4::rep(1);
            } else {
                ret = float4::rep(0);
            }
        } else {
            if (diffZ >= 0 && diffZ <= 1 / scale.z || diffZ >= 2 / scale.z && diffZ <= 3 / scale.z) {
                ret = float4::rep(0);
            } else {
                ret = float4::rep(1);
            }
        }

        //take inverse of color if x or z, but not both, are negative
        if ((m_position.x < 0) ^ (m_position.z < 0)) {
            if (ret.x == 0) {
                ret = float4::rep(1);
            } else {
                ret = float4::rep(0);
            }
        }

        return ret;

    }

    _IMPLEMENT_CLONE(CheckBoard3DShader);
};

void assigment4_ex3() {
    Image img(800, 600);
    img.addRef();

    CheckBoard3DShader sh;
    sh.scale = float4::rep(4);
    sh.addRef();

    InfinitePlane p1(Point(0, 0.1f, 0), Vector(0, 1, 0), &sh);

    PerspectiveCamera cam1(Point(0, 2, 10), Vector(0, 0, -1), Vector(0, 1, 0), 60,
            std::make_pair(img.width(), img.height()));
    cam1.addRef();

    EyeLightIntegrator integrator;
    integrator.addRef();
    integrator.scene = &p1;

    DefaultSampler sampDefault;
    sampDefault.addRef();

    RegularSampler sampRegular;
    sampRegular.addRef();

    RandomSampler sampRandom;
    sampRandom.addRef();

    StratifiedSampler sampStratified;
    sampStratified.addRef();

    //TODO: Create instances of the samplers and put them in th samplers[] array
    SmartPtr<Sampler> samplers[] = {&sampDefault, &sampRegular, &sampRandom, &sampStratified};

    Renderer r;
    r.camera = &cam1;
    r.integrator = &integrator;
    r.target = &img;

     srand ( time(NULL) );

    for (int s = 0; s < 4; s++) {
        r.sampler = samplers[s];
        r.render();
        std::stringstream ssm;
        ssm << "result_ex3_" << s + 1 << ".png" << std::flush;
        img.writePNG(ssm.str());

    }

}
