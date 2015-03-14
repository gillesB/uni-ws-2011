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
#include "impl/mirrorShader.h"
#include "impl/proceduralShaders/allProceduralShaders.h"
#include "impl/displacer.h"

void mountainAndTable(int resolutionX, int resolutionY) {
    Displacer displacer;
    Image img(resolutionX, resolutionY);
    img.addRef();

    // toggle between high-resolution settings and low-resolution settings
    bool highRes = true;
    if (resolutionX <= 640) {
        highRes = false;
    } else {
        highRes = true;
    }

    //Set up the scene
    GeometryGroup scene;

    //read the .obj file
    LWObject model;
    model.read("models/Final scene 1.0.obj", true);

    //The part of the scene which has to be displaced, as a new LWObject.
    LWObject displacedObject;
    displacedObject.materialMap = model.materialMap;
    displacedObject.materials = model.materials;
    displacedObject.normals = model.normals;
    displacedObject.vertices = model.vertices;
    displacedObject.texCoords = model.texCoords;

    int recDepth = 1;   //recursion depth of 0 is not allowed
            if (highRes){
                recDepth = 6;
            } else {
                recDepth = 2;
            }
    std::cout<<"Starting with recursive subdividing..."<<std::endl;
    for (vector<LWObject::Face>::iterator it = model.faces.begin(); it != model.faces.end(); it++) {
        if (it->material == model.materialMap["Clock_Face_ClockFace_tex.png"]) {
            //subdivide the faces recursivly into a finer mesh and store them in the displaced LWObject
            displacer.divideFace(displacedObject, *it, recDepth);
            //delete faces out of the primary Object
            model.faces.erase(it);
            //erase makes the iterator pointing to the next element, and the iterator is increased
            // by the for loop, so we will always jump over one element.
            //To avoid this, decrease the iterator.
            it--;
        }
    }
    std::cout<<"Finished with recursive subdividing..."<<std::endl;
    model.addReferencesToScene(scene.primitives);
    scene.rebuildIndex();

    /* isDisplaced is a boolean array of the size of the vertices of the object which
     * has to be displaced. The value of an element tells if the vertex has already been displaced.
     */
    std::cout<<"Starting with displacing vertices..."<<std::endl;
    bool isDisplaced[displacedObject.vertices.size()];
    int materialIndex = model.materialMap["Clock_Face_ClockFace_tex.png"];
    for (int i = 0; i < displacedObject.faces.size(); i++) {
        displacer.displaceFace(displacedObject, displacedObject.faces[i], materialIndex, isDisplaced);
    }
    displacedObject.addReferencesToScene(scene.primitives);
    scene.rebuildIndex();
    std::cout<<"Finished with displacing vertices..."<<std::endl;

    // Initialize all needed shaders

    //Clock_Face_ClockFace_tex.png
    SmartPtr<TexturedPhongShader> clockFace = new TexturedPhongShader;
    displacedObject.materials[displacedObject.materialMap["Clock_Face_ClockFace_tex.png"]].setShaderAndKeepValues(clockFace);
    clockFace->ambientTexture = clockFace->diffuseTexture;
    clockFace->diffuseTexture->filterMode = Texture::TFM_Bilinear;
    clockFace->ambientTexture->filterMode = Texture::TFM_Bilinear;

    //Walls_wallpaper_UV.png
    SmartPtr<BumpmapShader> wallpaper = new BumpmapShader;
    model.materials[model.materialMap["Walls_william_morris_wallpa"]].setShaderAndKeepValues(wallpaper);
    wallpaper->ambientTexture = wallpaper->diffuseTexture;
    wallpaper->diffuseTexture->filterMode = Texture::TFM_Bilinear;
    wallpaper->ambientTexture->filterMode = Texture::TFM_Bilinear;
    wallpaper->bumpTexture->filterMode = Texture::TFM_Bilinear;

    //Ceiling
    ParquetShader ceiling;
    ceiling.addRef();
    ceiling.plankspertile = 10;
    ceiling.plankvary = 0;
    ceiling.turnPlanks = false;
    ceiling.groovewidth = 0.1;
    model.materials[model.materialMap["Ceiling"]].shader = &ceiling;

    //Floor
    ParquetShader parquet;
    parquet.addRef();
    model.materials[model.materialMap["Floor"]].shader = &parquet;

    //Window Frame
    SmartPtr<WoodShader> windowShader = new WoodShader;
    windowShader->lightwood = float4(0.662745098039, 0.321568627451, 0.113725490196, 0);
    windowShader->darkwood = float4(0.407843137255, 0.152941176471, 0.0274509803922, 0);
    model.materials[model.materialMap["Window_Wood"]].shader = windowShader;

    //Special Glas
    SpecialTransparentShader special_glas;
    special_glas.addRef();
    special_glas.ambientCoef = float4(0, 0, 0, 0);
    special_glas.diffuseCoef = float4(1, 1, 1, 0);
    special_glas.specularExponent = 1000.f;
    special_glas.specularCoef = float4::rep(.2f);
    special_glas.setTransparency(0.95, 0.99);
    model.materials[model.materialMap["Special_Glas"]].shader = &special_glas;

    //Clock Wood
    SmartPtr<WoodShader> clockWoodShader = new WoodShader;
    clockWoodShader->lightwood = float4(0.30, 0.17, 0.14, 0);
    clockWoodShader->darkwood = float4(0.1, 0.06, 0.055, 0);
    model.materials[model.materialMap["Clock_Wood"]].shader = clockWoodShader;

    //Glas shader
    TransparentShader glas;
    glas.addRef();
    glas.ambientCoef = float4(0, 0, 0, 0);
    glas.diffuseCoef = float4(1, 1, 1, 0);
    glas.specularExponent = 1000.f;
    glas.specularCoef = float4::rep(.2f);
    glas.setTransparency(0.85, 0.9);
    glas.refractionIndex = 1.62f;

    //Clock glas
    model.materials[model.materialMap["Clock_Glas"]].shader = &glas;

    //Clock Metal       Pendular, Indicator
    MirrorPhongShader metal = MirrorPhongShader();
    metal.ambientCoef = float4(0.486f, 0.29f, 0.03529, 0);
    metal.diffuseCoef = metal.ambientCoef;
    metal.specularCoef = float4::rep(0.8f);
    metal.specularExponent = 10000.f;
    metal.reflCoef = 0.4f;
    metal.addRef();
    model.materials[model.materialMap["Indicator"]].shader = &metal;
    model.materials[model.materialMap["Pendular"]].shader = &metal;

    //Mountain
    MountainShader mountain;
    mountain.addRef();
    model.materials[model.materialMap["Mountain"]].shader = &mountain;

    //Table
    SmartPtr<WoodShader> tableShader = new WoodShader;
    tableShader->lightwood = float4(0.4, 0.254901960784, 0.149019607843, 0);
    tableShader->darkwood = float4(0.286274509804, 0.125490196078, 0.0470588235294, 0);
    model.materials[model.materialMap["Table"]].shader = tableShader;

    //Absinth Fountain Glas
    model.materials[model.materialMap["Absinth_Fountain"]].shader = &glas;

    //Fontaine Gold
    SmartPtr<MirrorPhongShader> gold = new MirrorPhongShader();
    model.materials[model.materialMap["Gold"]].setShaderAndKeepValues(gold);
    gold->ambientCoef = gold->diffuseCoef;
    gold->reflCoef = 0.2f;

    //Water
    SmartPtr<TransparentShader> liquid = new TransparentShader;
    model.materials[model.materialMap["Water"]].setShaderAndKeepValues(liquid);
    liquid->setTransparency(1.0f, 1.0f);
    liquid->refractionIndex = 1.333f;

    //Absinth Glas
    model.materials[model.materialMap["Absinth_Glas"]].shader = &glas;

    //Absinth
    SmartPtr<TransparentShader> absinth = new TransparentShader;
    model.materials[model.materialMap["Absinth"]].setShaderAndKeepValues(absinth);
    absinth->ambientCoef = absinth->diffuseCoef;
    absinth->setTransparency(0.85, 0.90);
    absinth->refractionIndex = 1.3638f;

    //Absinth Bottle
    model.materials[model.materialMap["Bottle_Glas"]].shader = &glas;

    //Bottle Buckler
    SmartPtr<MirrorPhongShader> buckler = new MirrorPhongShader();
    model.materials[model.materialMap["Bottle_Buckler"]].setShaderAndKeepValues(buckler);
    buckler->ambientCoef = buckler->diffuseCoef;
    buckler->reflCoef = 0.05f;

    //Spoon Silver
    SmartPtr<MirrorPhongShader> silver = new MirrorPhongShader();
    model.materials[model.materialMap["Silver"]].setShaderAndKeepValues(silver);
    silver->ambientCoef = silver->diffuseCoef;
    silver->reflCoef = 0.2f;

    //Sugar
    SmartPtr<TexturedPhongShader> sugar = new TexturedPhongShader;
    model.materials[model.materialMap["Sugar_Sugar.png"]].setShaderAndKeepValues(sugar);
    sugar->ambientTexture = sugar->diffuseTexture;
    sugar->diffuseTexture->filterMode = Texture::TFM_Bilinear;
    sugar->ambientTexture->filterMode = Texture::TFM_Bilinear;

    //Candleholder Brass
    SmartPtr<MirrorPhongShader> brass = new MirrorPhongShader();
    model.materials[model.materialMap["Brass"]].setShaderAndKeepValues(brass);
    brass->ambientCoef = brass->diffuseCoef;
    brass->specularExponent = 10000.f;
    brass->reflCoef = 0.08f;

    //Candleholder Candle       DefaultPhongShader
    //Candleholder Flame        DefaultPhongShader

    //Chair_Leather     DefaultPhongShader
    model.materials[model.materialMap["Chair_Leather"]].ambientCoeff
            = model.materials[model.materialMap["Chair_Leather"]].diffuseCoeff;

    //Chair_Stand       DefaultPhongShader

    //Ceramic Bowl
    SmartPtr<MirrorPhongShader> ceramic = new MirrorPhongShader();
    model.materials[model.materialMap["Ceramic"]].setShaderAndKeepValues(ceramic);
    ceramic->ambientCoef = ceramic->diffuseCoef;
    ceramic->specularExponent = 10000.f;
    ceramic->reflCoef = 0.5f;

    //Skydome
    SmartPtr<TexturedPhongShader> sky = new TexturedPhongShader;
    model.materials[model.materialMap["Skydome_Skydome_clouds.png"]].setShaderAndKeepValues(sky);
    sky->ambientTexture = sky->diffuseTexture;
    sky->diffuseTexture->filterMode = Texture::TFM_Bilinear;
    sky->ambientTexture->filterMode = Texture::TFM_Bilinear;


    //set up camera and lights etc...
    PerspectiveCamera cam1(Point(-3.724, -6.922, 3.6), Point(5.4561, 25.4713, 1.0), Vector(0, 0, 1), 30,
            std::make_pair(img.width(), img.height()));
    cam1.addRef();

    //Set up the integrator
    IntegratorImpl integrator;
    integrator.addRef();
    integrator.scene = &scene;

    PointLightSource interior;
    interior.falloff = float4(0, 0, 1, 0);
    interior.intensity = float4(1, 0.745098039216, 0.521568627451, 0);
    interior.intensity /= float4::rep(2); //good result

    if (!highRes) {
        interior.position = Point(-4, -10, 1.75);
        integrator.lightSources.push_back(interior);
    } else {//For hight resolution image; these lightsources shall simulate an ingle
        interior.intensity /= float4::rep(25.0);
        BBox bbLight;
//        bbLight.min = Point(-3, -0.75, 0.5);
//        bbLight.max = Point(-3.5, -0.25, 1);
        bbLight.min = Point(-4, -10, 1.75);
        bbLight.max = Point(-3.5, -9.5, 1.75);

        for (float y = 0; y < 5; y += 1) {
            for (float x = 0; x < 5; x += 1) {
                float x1 = (x + (rand() % 1000) / 1000.f) / 10;
                float y1 = (y + (rand() % 1000) / 1000.f) / 10;
                Point pt = Point(
                        bbLight.min.x * x1 + bbLight.max.x * (1 - x1),
                        bbLight.min.y * y1 + bbLight.max.y * (1 - y1),
                        bbLight.min.z);
                interior.position = pt;
                integrator.lightSources.push_back(interior);
            }
        }
    }

    PointLightSource exterior;
    exterior.falloff = float4(0, 0, 1, 0);
    exterior.intensity = float4(0.996078431373, 0.760784313725, 0.447058823529, 0);
    exterior.position = Point(45, 15.55f, 14.117f);
    integrator.lightSources.push_back(exterior);

    integrator.ambientLight = float4::rep(0.2f);

    //Renderer
    Renderer r;
    r.integrator = &integrator;
    r.target = &img;
    r.camera = &cam1;

    //Sampler
    if (highRes) {
        //For hight resolution image; Stratified Sampler
        StratifiedSampler samp;
        samp.samplesX = 4;
        samp.samplesY = 4;
        samp.addRef();
        r.sampler = &samp;
        std::cout<<"Starting with rendering of image with size="<<resolutionX<<"x"<<resolutionY<<std::endl;
        r.render();
    }else{
        DefaultSampler samp;
        samp.addRef();
        r.sampler = &samp;
        r.render();
    }

    img.writePNG("result.png");
    
}