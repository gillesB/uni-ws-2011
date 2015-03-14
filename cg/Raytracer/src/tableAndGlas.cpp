/*
 * This File was written by Paul Hirtz, please DO consider this part for the grading.
 */

#include <cstdlib>

using namespace std;

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
#include "impl/bumpmapShader.h"
#include "impl/proceduralShaders/allProceduralShaders.h"

void tableAndGlas() {
    Image img(800, 600);
    img.addRef();

    //Set up the scene
    GeometryGroup scene;
    //set up an sphere
    SmartPtr<TransparentShader> sektglas = new TransparentShader;
    sektglas->refractionIndex = 1.33;
    sektglas->setTransparency(0.85, 0.9);


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
    cow.read("models/TischGlas_test.obj", true);
    cow.addReferencesToScene(scene.primitives);
    scene.rebuildIndex();

    cow.materials[cow.materialMap["Sektglas"]].setShaderAndKeepValues(sektglas);
    cow.materials[cow.materialMap["Floor"]].shader = &parquet;    
    cow.materials[cow.materialMap["Table"]].shader = &wood;

    //Enable bi-linear filtering on the walls
    //cow.materials[cow.materialMap["Stones"]].shader = &marble;

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
     PerspectiveCamera cam1(Point(-5.5f, 1.6f, 9.6f), Point(2.2f, 6.7f, 3.2f), Vector(0, 0, 1), 30,
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
    pls.position = Point(3.3, 2.874f, 3);
    integrator.lightSources.push_back(pls);
    //    pls.position = Point(-0.935f, 6.992f, 2.895f);
    //integrator.lightSources.push_back(pls);

    integrator.ambientLight = float4::rep(0.4f);

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

}



