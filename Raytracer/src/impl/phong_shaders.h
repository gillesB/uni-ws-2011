#ifndef __INCLUDE_GUARD_810F2AF5_7E81_4F1E_AA05_992B6D2C0016
#define __INCLUDE_GUARD_810F2AF5_7E81_4F1E_AA05_992B6D2C0016
#ifdef _MSC_VER
#pragma once
#endif

#include "../rt/shading_basics.h"

struct DefaultAmbientShader : public PluggableShader {
    float4 ambientCoefficient;

    virtual float4 getAmbientCoefficient() const {
        return ambientCoefficient;
    }

    _IMPLEMENT_CLONE(DefaultAmbientShader);

    virtual ~DefaultAmbientShader() {
    }
};

//A base class for a phong shader.

class PhongShaderBase : public PluggableShader {
public:

    virtual void getCoeff(float4 &_diffuseCoef, float4 &_specularCoef, float &_specularExponent) const = 0;
    virtual Vector getNormal() const = 0;

    virtual float4 getReflectance(const Vector &_outDir, const Vector &_inDir) const {
        float4 Cs, Cd;
        float Ce;

        getCoeff(Cd, Cs, Ce);

        Vector normal = getNormal();
        Vector halfVect = ~(~_inDir + ~_outDir);
        //float specCoeff = std::max(halfVect * normal, 0.f);
        float specCoeff = fabs(halfVect * normal);
        specCoeff = exp(log(specCoeff) * Ce);
        //float diffCoeff = std::max(normal * ~_inDir, 0.f);
        float diffCoeff = fabs(normal * ~_inDir);

        float4 ret = float4::rep(diffCoeff) * Cd + float4::rep(specCoeff) * Cs;
        return ret;
    }

    virtual ~PhongShaderBase() {
    }
};


//The default phong shader

class DefaultPhongShader : public PhongShaderBase {
protected:
    Vector m_normal; //Stored normalized

public:
    float4 diffuseCoef;
    float4 specularCoef;
    float4 ambientCoef;
    float specularExponent;

    //Not yet used, will be used in shaders, which inherit from this one
    SmartPtr<Texture> diffuseTexture;
    SmartPtr<Texture> specularTexture;
    SmartPtr<Texture> ambientTexture;
    float bumpIntensity;
    SmartPtr<Texture> bumpTexture;


    //Get the ambient coefficient for the material

    virtual float4 getAmbientCoefficient() const {
        return ambientCoef;
    }

    virtual void getCoeff(float4 &_diffuseCoef, float4 &_specularCoef, float &_specularExponent) const {
        _diffuseCoef = diffuseCoef;
        _specularCoef = specularCoef;
        _specularExponent = specularExponent;
    }

    virtual Vector getNormal() const {
        return m_normal;
    }

    virtual void setNormal(const Vector& _normal) {
        m_normal = ~_normal;
    }

    _IMPLEMENT_CLONE(DefaultPhongShader);

};

//A phong shader that supports texturing

class TexturedPhongShader : public DefaultPhongShader {
protected:
    float2 m_texCoord;
public:

    virtual void setTextureCoord(const float2& texCoord) {
        m_texCoord = texCoord;
    }

    virtual float4 getAmbientCoefficient() const {
        float4 ret = DefaultPhongShader::getAmbientCoefficient();
        if (ambientTexture.data() != NULL)
            ret = ambientTexture->sample(m_texCoord);

        return ret;
    }

    virtual void getCoeff(float4& diffuseCoef, float4& specularCoef, float& specularExponent) const {
        DefaultPhongShader::getCoeff(diffuseCoef, specularCoef, specularExponent);

        if (diffuseTexture.data() != NULL)
            diffuseCoef = diffuseTexture->sample(m_texCoord);

        if (specularTexture.data() != NULL)
            specularCoef = specularTexture->sample(m_texCoord);
    }


    _IMPLEMENT_CLONE(TexturedPhongShader);
};

class BumpMirrorPhongShader : public DefaultPhongShader {
protected:
    float2 m_texCoord;
    Point m_position;
    Vector m_tang, m_biNorm;
public:
    float reflCoef;

    virtual void setPosition(const Point& point) {
        m_position = point;
    }

    //Set the tangent to (0, 1, 0)

    virtual void setNormal(const Vector& normal) {
        DefaultPhongShader::setNormal(normal);
        m_tang = Vector(0, -1, 0);
        m_biNorm = ~normal % m_tang;
    }

    virtual Vector getNormal() const {
        Vector ret = m_normal;

        float d;
        float2 t1 = float2(modf(m_texCoord.x, &d), modf(m_texCoord.y, &d)) - float2(0.5f, 0.5f);

        float dist = t1.x * t1.x + t1.y * t1.y;
        const float R = 0.25;
        const float DIV = 10.f;
        if (dist < R * R) {
            Vector newNorm(t1.x / DIV, t1.y / DIV, sqrtf(R * R - dist / (DIV * DIV)));
            ret = newNorm.x * m_tang + newNorm.y * m_biNorm + newNorm.z * m_normal;
            ret = ~ret;
        }

        return ret;
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

    virtual void setTextureCoord(const float2& _texCoord) {
        m_texCoord = _texCoord;
    }

    _IMPLEMENT_CLONE(BumpMirrorPhongShader);

};



#endif //__INCLUDE_GUARD_810F2AF5_7E81_4F1E_AA05_992B6D2C0016
