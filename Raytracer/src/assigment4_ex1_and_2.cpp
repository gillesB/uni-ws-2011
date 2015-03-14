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

void assigment4_1_and_2() {
    Image img(800, 600);
    img.addRef();

    //Set up the scene
    GeometryGroup scene;
    //set up an sphere
    TransparentShader sektglas;

    sektglas.addRef();
    sektglas.ambientCoef = float4(0, 0, 0, 0);
    sektglas.diffuseCoef = float4(1, 1, 1, 0);
    sektglas.specularExponent = 1000.f;
    sektglas.specularCoef = float4::rep(.2f);
    sektglas.setTransparency(0.85, 0.9);

//    MarbleLikeShader marble;
//    marble.ambientCoef = float4::rep(0.5);
//    marble.diffuseCoef = float4::rep(0.8);
//    marble.specularCoef = float4::rep(0.2);
//    marble.addRef();
//
//    Sphere sphere1(Point(0.82, 7.36, 1.7), 0.5, &marble);
//    scene.primitives.push_back(&sphere1);
//
//    WoodShader wood;
//    wood.addRef();
//
//    StrataShader strata;
//    strata.addRef();

//    Sphere sphere1(Point(0.82, 7.36, 1.7), 0.5, &marble);
//    scene.primitives.push_back(&sphere1);

    ParquetShader parquet;
    parquet.addRef();

    WoodShader wood;
    wood.diffuseCoef = float4(0.2f, 0.2f, 0, 0);
    wood.ambientCoef = wood.diffuseCoef;
    wood.specularCoef = float4::rep(0.8f);
    wood.specularExponent = 10000.f;
    wood.addRef();

    //set up the cow
    LWObject cow;
    cow.read("models/Sektglas_test.obj", true);
    cow.addReferencesToScene(scene.primitives);
    scene.rebuildIndex();
    //    cow.materials[cow.materialMap["bottle"]].shader = &sh1;
    cow.materials[cow.materialMap["Sektglas"]].shader = &sektglas;


    BumpMirrorPhongShader sh4;
    sh4.diffuseCoef = float4(0.2f, 0.2f, 0, 0);
    sh4.ambientCoef = sh4.diffuseCoef;
    sh4.specularCoef = float4::rep(0.8f);
    sh4.specularExponent = 10000.f;
    sh4.reflCoef = 0.4f;
    sh4.addRef();

    cow.materials[cow.materialMap["Floor"]].shader = &parquet;

    //Enable bi-linear filtering on the walls
    //cow.materials[cow.materialMap["Stones"]].shader = &marble;
//    ((TexturedPhongShader*) cow.materials[cow.materialMap["Stones"]].shader.data())->diffTexture->filterMode = Texture::TFM_Bilinear;
//    ((TexturedPhongShader*) cow.materials[cow.materialMap["Stones"]].shader.data())->amibientTexture->filterMode = Texture::TFM_Bilinear;

    SmartPtr<BumpmapShader> bumpMapShader = new BumpmapShader;
    cow.materials[cow.materialMap["Stones"]].setShaderAndKeepValues(bumpMapShader);
    ((BumpmapShader*) cow.materials[cow.materialMap["Stones"]].shader.data())->diffuseTexture->filterMode = Texture::TFM_Bilinear;
    ((BumpmapShader*) cow.materials[cow.materialMap["Stones"]].shader.data())->ambientTexture->filterMode = Texture::TFM_Bilinear;
    ((BumpmapShader*) cow.materials[cow.materialMap["Stones"]].shader.data())->bumpTexture->filterMode = Texture::TFM_Bilinear;

    //Set up the cameras
    //    PerspectiveCamera cam1(Point(-9.398149f, -6.266083f, 5.348377f), Point(-6.324413f, -2.961229f, 4.203216f), Vector(0, 0, 1), 30,
    //            std::make_pair(img.width(), img.height()));
    //
    //    PerspectiveCamera cam2(Point(2.699700f, 6.437226f, 0.878297f), Point(4.337114f, 8.457443f, -0.019007f), Vector(0, 0, 1), 30,
    //            std::make_pair(img.width(), img.height()));

//    PerspectiveCamera cam1(Point(-3.45f, 3.06f, 2.96f), Point(3.6545f, 8.0570f, 0.7497f), Vector(0, 0, 1), 30,
//            std::make_pair(img.width(), img.height()));
    PerspectiveCamera cam1(Point(-3.0f, 2.5f, 2.0f), Point(3.6545f, 8.0570f, 0.7497f), Vector(0, 0, 1), 30,
            std::make_pair(img.width(), img.height()));

    cam1.addRef();
    //    cam2.addRef();

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
    img.writePNG("pictures/result_cam1.png");

    //For seeing the difference in texture filtering
    //    r.camera = &cam2;
    //    r.render();
    //    img.writePNG("result_cam2.png");
}

