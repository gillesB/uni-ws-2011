/*
 * This Header was written by Paul Hirtz, please DO consider this part for the grading.
 */

#ifndef TRANSPARENTSHADER_H
#define	TRANSPARENTSHADER_H

/**
 A class for a simple transparent shader.
 implemented after TRANSPARENCY FOR COMPUTERSYNTHESIZED IMAGES by Douglas Scott Kay
 */
class TransparentShader : public DefaultPhongShader {
private:

    struct RefractedAndReflectedVector {
        Vector refractedVector;
        float transmittance; //how many "photons" are transmitted
        Vector reflectedVector;
        float reflectance; //how many "photons" are reflected
        //transmittance + reflectance = 1
    };

    float transparencyCurrentPixel() const {
        float Zn = fabs(getNormal().z);
        float power = (1 - Zn)*(1 - Zn);
        return ((transparency_max - transparency_min) * power + transparency_min);
    }

    float fresnel(float refractionIndex1, float refractionIndex2, float cosIn, float cosOut) const {

        float refOrthogonal = (refractionIndex1 * cosIn - refractionIndex2 * cosOut)
                / (refractionIndex1 * cosIn + refractionIndex2 * cosOut);
        refOrthogonal *= refOrthogonal; // power 2
        float refParallel = (refractionIndex2 * cosIn - refractionIndex1 * cosOut)
                / (refractionIndex2 * cosIn + refractionIndex1 * cosOut);
        refParallel *= refParallel; //power 2

        float average = (refOrthogonal + refParallel) / 2;

        return average;
    }

    RefractedAndReflectedVector getOutVectors(const Vector& in) const {

        Vector normalizedIn = ~in;

        RefractedAndReflectedVector vectors;
        Vector normal = getNormal();

        float cosIncome = normal * -normalizedIn; //cosine of the incoming vector
        float refractionIndex1 = 1;
        float refractionIndex2 = this->refractionIndex;
        if (cosIncome < 0) {
            normal = -normal;
            cosIncome = normal * -normalizedIn;
            refractionIndex1 = this->refractionIndex;
            refractionIndex2 = 1;
        }

        float sqr_cosIncome = cosIncome*cosIncome;

        float refractionRatio = refractionIndex1 / refractionIndex2;
        float sqr_refractionIndex = refractionRatio * refractionRatio;

        float radicidant = 1 - sqr_refractionIndex * (1 - sqr_cosIncome);

        //if the radicadant is negative then total internal reflection happens
        if (radicidant >= 0) {
            float cosRefraction = sqrtf(radicidant);
            //if (cosIncome >= 0) {
            Vector a = refractionRatio * normalizedIn;
            Vector b = (refractionRatio * cosIncome - cosRefraction) * normal;
            Vector c = a + b;

            vectors.refractedVector = c;
            //} else {
            //    vectors.refractedVector = refractionIndex * normalizedIn - (refractionIndex * cosIncome - cosRefraction) * normal;
            //}
            vectors.reflectance = fresnel(refractionIndex1, refractionIndex2, fabs(cosIncome), cosRefraction);

            vectors.transmittance = 1 - vectors.reflectance;
        } else { //total internal reflection
            vectors.transmittance = 0;
            vectors.reflectance = 1;
            vectors.refractedVector = Vector();
        }

        vectors.reflectedVector = ~(normalizedIn + (2 * cosIncome) * normal);

        return vectors;

    }

protected:
    Point m_position;
    float transparency_max, transparency_min;

public:

    float refractionIndex;

    TransparentShader() {
        refractionIndex = 1;
    }

    virtual void setPosition(const Point& point) {
        m_position = point;
    }

    virtual void setTransparency(float transparency) {
        this->transparency_min = transparency - 0.25;
        this->transparency_max = transparency + 0.25;
    }

    virtual void setTransparency(float transparency_min, float transparency_max) {
        this->transparency_min = transparency_min;
        this->transparency_max = transparency_max;
    }

    virtual float4 getReflectance(const Vector& outDir, const Vector& inDir) const {
        float4 usualReflectance = DefaultPhongShader::getReflectance(outDir, inDir);
        float transparency = transparencyCurrentPixel();
        return (float4::rep(1) - float4::rep(transparency))*usualReflectance;
    }

    virtual float4 getIndirectRadiance(const Vector& out, Integrator* integrator) const {

        RefractedAndReflectedVector outVectors = getOutVectors(-out);

        Ray r;
        r.o = m_position;

        r.d = outVectors.reflectedVector;
        float4 indirectRadiance = integrator->getRadiance(r) * float4::rep(outVectors.reflectance);

        if (outVectors.transmittance != 0) {
            r.d = outVectors.refractedVector;
            indirectRadiance += integrator->getRadiance(r) * float4::rep(outVectors.transmittance);
        }

        return indirectRadiance * float4::rep(transparencyCurrentPixel());

    }

    //the default shader is not tranparent
    virtual float getTransparency() const {
        return transparency_max;
    }

    _IMPLEMENT_CLONE(TransparentShader);

};

/**
 * a transparent shader, which is used for the window.
 * It is neccessary to avoid that shadows are thrown from the interior to the mountains
 * @return
 */
class SpecialTransparentShader : public TransparentShader {
public:

    virtual float getTransparency() const {
        return 0;
    }

    _IMPLEMENT_CLONE(SpecialTransparentShader);
};



#endif	/* TRANSPARENTSHADER_H */

