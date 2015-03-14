/*
 * This File was written by Gilles Baatz, please do NOT consider this part for the grading.
 */
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

/*
 * Heterogeneous procedural terrain function: stats by altitude method.
 * Evaluated at “point”; returns value stored in “value”.
 *
 * Parameters:
 *
“H” determines the fractal dimension of the roughest areas
 *
“lacunarity” is the gap between successive frequencies
 *
“octaves” is the number of frequencies in the fBm
 *
“offset” raises the terrain from “sea level”
 */
double
Hetero_Terrain(Point point,
        double H, double lacunarity, double octaves, double offset) {
    double value, increment, frequency, remainder;
    int i;
    RenderManFunctions renderMan;
    static int first = 1;
    static double *exponent_array;
    /* precompute and store spectral weights, for efficiency */
    if (first) {
        /* seize required memory for exponent_array */
        exponent_array =
                (double *) malloc((octaves + 1) * sizeof (double));
        frequency = 1.0;
        for (i = 0; i <= octaves; i++) {
            /* compute weight for each frequency */
            exponent_array[i] = pow(frequency, -H);
            frequency *= lacunarity;
        }
        first = 0;
    }
    /* first unscaled octave of function; later octaves are scaled */
    value = offset + renderMan.noise(point);
    point.x *= lacunarity;
    point.y *= lacunarity;
    point.z *= lacunarity;
    
    /* spectral construction inner loop, where the fractal is built */
    for (i = 1; i < octaves; i++) {
        /* obtain displaced noise value */
        increment = renderMan.noise(point) + offset;
        /* scale amplitude appropriately for this frequency */
        increment *= exponent_array[i];
        /* scale increment by current “altitude” of function */
        increment *= value;

        /* add increment to “value” */
        value += increment;       
            //value /= (renderMan.smoothstep(0.08,0.12,value)+1);      
        
        /* raise spatial frequency */
        point.x *= lacunarity;
        point.y *= lacunarity;
        point.z *= lacunarity;
    } /* for */
    /* take care of remainder in “octaves” */
    remainder = octaves - (int) octaves;
    if (remainder) {
        /* “i” and spatial freq. are preset in loop above */
        /* note that the main loop code is made shorter here */
        /* you may want to make that loop more like this */
        increment = (renderMan.noise(point) + offset) * exponent_array[i];
        value += remainder * increment * value;
    }

//    if (foothill) {
//        float minValue = 0;
//        float maxValue = 0.3;
//
//        if (value < minValue) {
//            return (minValue + value * 0.2);
//        } else if (value > maxValue) {
//            return (maxValue);
//        }
//    }

    return ( value);
} /* Hetero_Terrain() */

void fractalGeometryTest() {

    Image img(800, 600);
    img.addRef();

    //Set up the scene
    GeometryGroup scene;

    //set up a face in a lwObject
    LWObject mountain;

    float mountainLength = 2.5;
    float mountainWidth = 4;
    //int foothillWidth = 3;

    int pointAmountLength = 300;
    int pointAmountWidth = 300;

    float startX = 1.7;
    float startY = 0.2;

    float widthRatio = ((float) mountainWidth / pointAmountWidth);
    float lengthRatio = ((float) mountainLength / pointAmountLength);

    float offset = 0.1;
    for (int i = 0; i < pointAmountWidth; i++) {
        
//        if(i*widthRatio > foothillWidth){
//            offset -= 0.01;
//        }
        for (int j = 0; j < pointAmountLength; j++) {
            Point p = Point(j*lengthRatio+startX, i*widthRatio+startY, 0);
            //IMHO good result
            //double zValue = Hetero_Terrain(p, 1, 2.1, 3.2, offset);
            double zValue = Hetero_Terrain(p, 1, 2.1 , 5.2, offset);
            p.z = zValue;
            mountain.vertices.push_back(p);
        }
    }

    Vector n1 = Vector(0, 0, 1);
    mountain.normals.push_back(n1);

    //add material
    MountainShader sh4;
    sh4.ambientCoef = float4::rep(0.5);
    sh4.diffuseCoef = float4::rep(0.8);
    sh4.specularCoef = float4::rep(0.2);
    sh4.addRef();

    LWObject::Material m1;
    m1.shader = &sh4;
    mountain.materials.push_back(m1);

    for (int i = 0; i < pointAmountWidth - 1; i++) {
        for (int j = 0; j < pointAmountLength - 1; j++) {
            LWObject::Face f1(&mountain);
            int index = i * pointAmountLength + j;
            f1.vert1 = index;
            f1.vert2 = index + 1;
            f1.vert3 = index + pointAmountLength;
            //getNormal
            Point p1 = mountain.vertices.at(index);
            Point p2 = mountain.vertices.at(index + 1);
            Point p3 = mountain.vertices.at(index + pointAmountLength);
            Vector e1 = ~(p2 - p1);
            Vector e2 = ~(p3 - p1);
            Vector norm = ~(e1 % e2);
            mountain.normals.push_back(norm);

            f1.norm1 = mountain.normals.size() - 1;
            f1.norm2 = mountain.normals.size() - 1;
            f1.norm3 = mountain.normals.size() - 1;
            f1.tex1 = -1;
            f1.material = 0;
            mountain.faces.push_back(f1);

            LWObject::Face f2(&mountain);
            f2.vert1 = index + 1;
            f2.vert2 = index + pointAmountLength + 1;
            f2.vert3 = index + pointAmountLength;
            //getNormal
            p1 = mountain.vertices.at(index + 1);
            p2 = mountain.vertices.at(index + pointAmountLength + 1);
            p3 = mountain.vertices.at(index + pointAmountLength);
            e1 = ~(p2 - p1);
            e2 = ~(p3 - p1);
            norm = ~(e1 % e2);
            mountain.normals.push_back(norm);

            f2.norm1 = mountain.normals.size() - 1;
            f2.norm2 = mountain.normals.size() - 1;
            f2.norm3 = mountain.normals.size() - 1;
            f2.tex1 = -1;
            f2.material = 0;
            mountain.faces.push_back(f2);
        }
    }

    Sphere sphere1(Point(0, 3, 2), 0.5, &sh4);
    //scene.primitives.push_back(&sphere1);
    mountain.addReferencesToScene(scene.primitives);
    scene.rebuildIndex();

    PerspectiveCamera cam1(Point(10, 10, 10), Point(0, 0, 0), Vector(0, 0, 1), 30,
            std::make_pair(img.width(), img.height()));

    cam1.addRef();

    IntegratorImpl integrator;
    integrator.addRef();
    integrator.scene = &scene;
    PointLightSource pls;

    pls.falloff = float4(0, 0, 1, 0);

    pls.intensity = float4::rep(0.8f);
    pls.position = Point(10, 10, 10);
    integrator.lightSources.push_back(pls);

    integrator.ambientLight = float4::rep(.2f);
    DefaultSampler samp;
    samp.addRef();

    //Render
    Renderer r;
    r.integrator = &integrator;
    r.target = &img;
    r.sampler = &samp;

    r.camera = &cam1;
    r.render();
    img.writePNG("pictures/result_cam1_mountain.png");
    mountain.write("models/mountains.obj");


}