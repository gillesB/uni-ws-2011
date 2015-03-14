#include "stdafx.h"

#include "core/image.h"
#include "rt/basic_definitions.h"
#include "rt/geometry_group.h"

#include "impl/lwobject.h"
#include "impl/phong_shaders.h"
#include "impl/basic_primitives.h"
#include "impl/perspective_camera.h"
#include "impl/integrator.h"
#include "rt/renderer.h"
#include "impl/samplers.h"

#include "impl/transparentShader.h"
#include "impl/proceduralShaders/allProceduralShaders.h"
#include "impl/bumpmapShader.h"



void liquid() {
    Image img(800, 600);
    img.addRef();

    //Set up the scene
    GeometryGroup scene;

    //set up the cow
    LWObject cow;
    cow.read("models/Glaeser_liquid3.obj", true);
    cow.addReferencesToScene(scene.primitives);
    scene.rebuildIndex();

    TransparentShader sektglas;
    sektglas.addRef();
    sektglas.ambientCoef = float4(0, 0, 0, 0);
    sektglas.diffuseCoef = float4(1, 1, 1, 0);
    sektglas.specularExponent = 1000.f;
    sektglas.specularCoef = float4::rep(.2f);
    sektglas.setTransparency(0.85, 0.9);
    sektglas.refractionIndex = 1.45f;
    cow.materials[cow.materialMap["Glas"]].shader = &sektglas;

    ParquetShader parquet;
    parquet.addRef();
    cow.materials[cow.materialMap["Floor"]].shader = &parquet;

    SmartPtr<TransparentShader> liquid = new TransparentShader;
    cow.materials[cow.materialMap["Water"]].setShaderAndKeepValues(liquid);
    liquid->ambientCoef = liquid->diffuseCoef;
    liquid->setTransparency(0.80, 0.85);
    liquid->refractionIndex=1.333f;

    SmartPtr<BumpmapShader> bumpMapShader = new BumpmapShader;
    cow.materials[cow.materialMap["Stones"]].setShaderAndKeepValues(bumpMapShader);
    bumpMapShader->diffuseTexture->filterMode = Texture::TFM_Bilinear;
    bumpMapShader->ambientTexture->filterMode = Texture::TFM_Bilinear;
    bumpMapShader->bumpTexture->filterMode = Texture::TFM_Bilinear;

    PerspectiveCamera cam1(Point(-3.0f, 2.5f, 2.0f), Point(3.6545f, 8.0570f, 0.7497f), Vector(0, 0, 1), 30,
            std::make_pair(img.width(), img.height()));

    cam1.addRef();

    //Set up the integrator
    IntegratorImpl integrator;
    integrator.addRef();
    integrator.scene = &scene;
    PointLightSource pls;

    pls.falloff = float4(0, 0, 1, 0);

    pls.intensity = float4::rep(1.2f);
    pls.position = Point(-3.3, 2.874f, 1.7f);
    integrator.lightSources.push_back(pls);
    //    pls.position = Point(-0.935f, 6.992f, 2.895f);
    //integrator.lightSources.push_back(pls);

    integrator.ambientLight = float4::rep(0.2f);

    DefaultSampler samp;
    samp.addRef();

    //Render
    Renderer r;
    r.integrator = &integrator;
    r.target = &img;
    r.sampler = &samp;

    r.camera = &cam1;
    r.render();
    img.writePNG("pictures/result_cam_liquid.png");
}

