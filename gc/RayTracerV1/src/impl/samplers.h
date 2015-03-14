#ifndef __INCLUDE_GUARD_EA5235C2_ADC9_40B5_9859_473C44497D3A
#define __INCLUDE_GUARD_EA5235C2_ADC9_40B5_9859_473C44497D3A
#ifdef _MSC_VER
#pragma once
#endif


#include "../rt/renderer.h"

//The default sampler which samples a pixel with a ray through it's center

struct DefaultSampler : public Sampler {

    virtual void getSamples(uint _x, uint _y, std::vector<Sample> &_result) {
        Sample s;
        s.position = float2(0.5f, 0.5f);
        s.weight = 1.f;
        _result.push_back(s);
    }

};

//TODO: Implement Regular, Random, and Stratified samplers.

struct RegularSampler : public Sampler {

    virtual void getSamples(uint _x, uint _y, std::vector<Sample> &_result) {
        for (int i = 0; i < 4; i++) {
            double x = (i + 0.5) / 4;
            for (int j = 0; j < 4; j++) {
                Sample s;
                double y = (j + 0.5) / 4;
                s.position = float2(x, y);
                s.weight = 1 / 16.;
                _result.push_back(s);
            }
        }
    }
};

struct RandomSampler : public Sampler {

    virtual void getSamples(uint _x, uint _y, std::vector<Sample> &_result) {
        for (int i = 0; i < 16; i++) {
            Sample s;
            double x = (float) rand() / ((long) RAND_MAX + 1);
            double y = (float) rand() / ((long) RAND_MAX + 1);
            s.position = float2(x, y);
            s.weight = 1 / 16.;
            _result.push_back(s);
        }
    }
};

struct StratifiedSampler : public Sampler {

    virtual void getSamples(uint _x, uint _y, std::vector<Sample> &_result) {
        for (int i = 0; i < 4; i++) {
            double xiX = (float) rand() / ((long) RAND_MAX + 1);
            double x = (i + xiX) / 4;
            for (int j = 0; j < 4; j++) {
                double xiY = (float) rand() / ((long) RAND_MAX + 1);
                double y = (j + xiY) / 4;
                Sample s;
                s.position = float2(x, y);
                s.weight = 1 / 16.;
                _result.push_back(s);
            }
        }
    }
};

#endif //__INCLUDE_GUARD_EA5235C2_ADC9_40B5_9859_473C44497D3A
