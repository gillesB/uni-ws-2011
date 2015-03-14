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
#include "impl/proceduralShaders/allProceduralShaders.h"
#include "impl/bumpmapShader.h"
#include "impl/displacer.h"

void displacementTest2() {
    Displacer displacer;

    Image img(800, 600);
    img.addRef();

    //Set up the scene
    GeometryGroup scene;

    ParquetShader parquet;
    parquet.addRef();

    LWObject cow;
    cow.read("models/Displacement_test_textured.obj", true);

    //The part of the scene which has to be displaced, as a new LWObject.
    LWObject displacedObject;
    displacedObject.materialMap = cow.materialMap;
    displacedObject.materials = cow.materials;

    for (vector<LWObject::Face>::iterator it = cow.faces.begin(); it != cow.faces.end(); it++) {
        if (it->material == cow.materialMap["Cube_stones_diffuse.png"]) {
            //subdivide the faces recursivly into a finer mesh and store them in the displaced LWObject
            displacer.divideFace(displacedObject, *it, 5);
            //delete faces out of the primary Object
            cow.faces.erase(it);
            //erase makes the iterator pointing to the next element, and the iterator is increased
            // by the for loop, so we will always jump over one element.
            //To avoid this, decrease the iterator.
            it--;
        }
    }
    cow.addReferencesToScene(scene.primitives);
    scene.rebuildIndex();

    /* isDisplaced is a boolean array of the size of the vertices of the object which
     * has to be displaced. The value of an element tells if the vertex has already been displaced.
     */
    bool isDisplaced[displacedObject.vertices.size()];
    int materialIndex = cow.materialMap["Cube_stones_diffuse.png"];
    for (int i = 0; i < displacedObject.faces.size(); i++) {
        displacer.displaceFace(displacedObject, displacedObject.faces[i], materialIndex, isDisplaced);
    }
    displacedObject.addReferencesToScene(scene.primitives);
    scene.rebuildIndex();

    cow.materials[cow.materialMap["Floor"]].shader = &parquet;

    SmartPtr<BumpmapShader> bumpMapShader = new BumpmapShader;
    cow.materials[cow.materialMap["Stones_stones_diffuse.pn.001"]].setShaderAndKeepValues(bumpMapShader);
    bumpMapShader->diffuseTexture->filterMode = Texture::TFM_Bilinear;
    bumpMapShader->ambientTexture->filterMode = Texture::TFM_Bilinear;
    bumpMapShader->bumpTexture->filterMode = Texture::TFM_Bilinear;

    SmartPtr<TexturedPhongShader> texShader = new TexturedPhongShader;
    displacedObject.materials[cow.materialMap["Cube_stones_diffuse.png"]].setShaderAndKeepValues(texShader);
    texShader->ambientTexture = texShader->diffuseTexture;
    texShader->diffuseTexture->filterMode = Texture::TFM_Bilinear;
    texShader->ambientTexture->filterMode = Texture::TFM_Bilinear;

    //Set up the cameras
    PerspectiveCamera cam1(Point(-5.5f, 1.6f, 9.6f), Point(2.2f, 6.7f, 3.2f), Vector(0, 0, 1), 30,
            std::make_pair(img.width(), img.height()));

    cam1.addRef();

    //Set up the integrator
    IntegratorImpl integrator;
    integrator.addRef();
    integrator.scene = &scene;
    PointLightSource pls;

    pls.falloff = float4(0, 0, 1, 0);

    pls.intensity = float4::rep(1.2f);
    pls.position = Point(-3.3, 0.0f, 8.5f);
    integrator.lightSources.push_back(pls);

    integrator.ambientLight = float4::rep(0.2f);

    //    DefaultSampler samp;
    StratifiedSampler samp;
    samp.samplesX = 4;
    samp.samplesY = 4;
    samp.addRef();

    //Render
    Renderer r;
    r.integrator = &integrator;
    r.target = &img;
    r.sampler = &samp;

    r.camera = &cam1;
    r.render();
    img.writePNG("pictures/result_displacementTest2.png");

}