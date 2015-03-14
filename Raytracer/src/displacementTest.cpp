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

float epsilon = 0.00000001;

LWObject::Face createFace(LWObject::Face& actualFace, LWObject& displacedObject,
        int vertIndex1, int vertIndex2, int vertIndex3,
        int normIndex1, int normIndex2, int normIndex3,
        int texIndex1, int texIndex2, int texIndex3) {
    LWObject::Face newFace(&displacedObject);

    newFace.material = actualFace.material;
    newFace.norm1 = normIndex1;
    newFace.norm2 = normIndex2;
    newFace.norm3 = normIndex3;
    newFace.vert1 = vertIndex1;
    newFace.vert2 = vertIndex2;
    newFace.vert3 = vertIndex3;
    newFace.tex1 = texIndex1;
    newFace.tex2 = texIndex2;
    newFace.tex3 = texIndex3;
 
    displacedObject.faces.push_back(newFace);
    return newFace;
}

int addVertexAndReturnIndex(LWObject& displacedObject, Point vertex) {
    for (int i = 0; i < displacedObject.vertices.size(); i++) {
        if ((fabs(displacedObject.vertices[i].x - vertex.x) < epsilon)
                && (fabs(displacedObject.vertices[i].y - vertex.y) < epsilon)
                && (fabs(displacedObject.vertices[i].z - vertex.z) < epsilon)) {
            return i;
        }
    }
    displacedObject.vertices.push_back(vertex);
    return displacedObject.vertices.size() - 1;
}

void divideFace(LWObject& object, LWObject& displacedObject, LWObject::Face& face, int recursionDepth) {

    if (recursionDepth != 0) {

        //Vertex v1 of Face and Index of it in the Vertices-index-array
        Point v1 = object.vertices[face.vert1];
        int vIndex1 = addVertexAndReturnIndex(displacedObject, v1);

        //Vertex v2 of Face and Index of it in the Vertices-index-array
        Point v2 = object.vertices[face.vert2];
        int vIndex2 = addVertexAndReturnIndex(displacedObject, v2);

        //Vertex v3 of Face and Index of it in the Vertices-index-array
        Point v3 = object.vertices[face.vert3];
        int vIndex3 = addVertexAndReturnIndex(displacedObject, v3);

        //Devide the 3 edges in the middle
        //Divide the edge of the face in the middle and create new Vertex, index of
        // vertex in the Vertices-index-array; p1 in the middle between v1 and v2
        Point p1 = v1 + (v2 - v1) / 2;
        int pIndex1 = addVertexAndReturnIndex(displacedObject, p1);

        //Divide the edge of the face in the middle and create new Vertex, index of
        // vertex in the Vertices-index-array; p2 in the middle between v2 and v3
        Point p2 = v2 + (v3 - v2) / 2;
        int pIndex2 = addVertexAndReturnIndex(displacedObject, p2);

        //Divide the edge of the face in the middle and create new Vertex, index of
        // vertex in the Vertices-index-array; p3 in the middle between v1 and v3
        Point p3 = v3 + (v1 - v3) / 2;
        int pIndex3 = addVertexAndReturnIndex(displacedObject, p3);



        //store normals individually, so they can also be edited individually during displacement
        //Normal of Vertex v1 and Index of it in the normals-index-array
        Vector n1 = object.normals[face.norm1];
        displacedObject.normals.push_back(n1);
        int nIndexV1 = displacedObject.normals.size() - 1;

        //Normal of Vertex v2 and Index of it in the normals-index-array
        Vector n2 = object.normals[face.norm2];
        displacedObject.normals.push_back(n2);
        int nIndexV2 = displacedObject.normals.size() - 1;

        //Normal of Vertex v3 and Index of it in the normals-index-array
        Vector n3 = object.normals[face.norm3];
        displacedObject.normals.push_back(n3);
        int nIndexV3 = displacedObject.normals.size() - 1;


        //Normal of Point p1 and Index of it in the normals-index-array
        Vector n4 = ~(~(v2 - p1) % ~(p2 - p1)); //~(~(n2 - n1) % ~(n3 - n1));
        displacedObject.normals.push_back(n4);
        int nIndexP1 = displacedObject.normals.size() - 1;

        //Normal of Point p2 and Index of it in the normals-index-array
        Vector n5 = ~(~(v3 - p2) % ~(p3 - p2));
        displacedObject.normals.push_back(n5);
        int nIndexP2 = displacedObject.normals.size() - 1;

        //Normal of Point p3 and Index of it in the normals-index-array
        Vector n6 = ~(~(p2 - p3) % ~(v3 - p3));
        displacedObject.normals.push_back(n6);
        int nIndexP3 = displacedObject.normals.size() - 1;



        //Calculate the texture coordinates
        //texture coordinate of v1
        float2 t1 = object.texCoords[face.tex1];
        displacedObject.texCoords.push_back(t1);
        int tIndex1 = displacedObject.texCoords.size() - 1;

        //texture coordinate of v2
        float2 t2 = object.texCoords[face.tex2];
        displacedObject.texCoords.push_back(t2);
        int tIndex2 = displacedObject.texCoords.size() - 1;

        //texture coordinate of v3
        float2 t3 = object.texCoords[face.tex3];
        displacedObject.texCoords.push_back(t3);
        int tIndex3 = displacedObject.texCoords.size() - 1;

        // texture coordinates of p1 between t1 and t2
        float2 t4 = t1 + (t2 - t1) / 2;
        displacedObject.texCoords.push_back(t4);
        int tIndex4 = displacedObject.texCoords.size() - 1;

        // texture coordinates of p2 between t2 and t3
        float2 t5 = t2 + (t3 - t2) / 2;
        displacedObject.texCoords.push_back(t5);
        int tIndex5 = displacedObject.texCoords.size() - 1;

        // texture coordinates of p3 between t1 and t3
        float2 t6 = t3 + (t1 - t3) / 2;
        displacedObject.texCoords.push_back(t6);
        int tIndex6 = displacedObject.texCoords.size() - 1;


        // Face of Points v1-p1-p3
        LWObject::Face f1 = createFace(face, displacedObject,
                vIndex1, pIndex1, pIndex3,
                nIndexV1, nIndexP1, nIndexP3,
                tIndex1, tIndex4, tIndex6);
        // Face of Points p1-v1-p2
        LWObject::Face f2 = createFace(face, displacedObject,
                pIndex1, vIndex2, pIndex2,
                nIndexP1, nIndexV2, nIndexP2,
                tIndex4, tIndex1, tIndex5);
        // Face of Points p1-p2-p3
        LWObject::Face f3 = createFace(face, displacedObject,
                pIndex1, pIndex2, pIndex3,
                nIndexP1, nIndexP2, nIndexP3,
                tIndex4, tIndex5, tIndex6);
        // Face of Points p2-v3-p3
        LWObject::Face f4 = createFace(face, displacedObject,
                pIndex2, vIndex3, pIndex3,
                nIndexP2, nIndexV3, nIndexP3,
                tIndex5, tIndex3, tIndex6);

        recursionDepth--;

        divideFace(displacedObject, displacedObject, f1, recursionDepth);
        divideFace(displacedObject, displacedObject, f2, recursionDepth);
        divideFace(displacedObject, displacedObject, f3, recursionDepth);
        divideFace(displacedObject, displacedObject, f4, recursionDepth);

    } else {
        return;
    }
}



void displaceVertex(LWObject& object, int materialIndex, int vertexIndex, int normalIndex, int texIndex) {
    Point vertex = object.vertices[vertexIndex];
    Vector normal = object.normals[normalIndex];
    BumpmapShader* materialShader = (BumpmapShader*) object.materials[materialIndex].shader.data();
    float4 codedHeight = materialShader->bumpTexture->sample(object.texCoords[texIndex]);
    float bumpIntensity = materialShader->bumpIntensity;
    float height = codedHeight.x * bumpIntensity;
    vertex = vertex + ((bumpIntensity * height) * normal);
    object.vertices[vertexIndex] = vertex;
}

void recalculateNormals(LWObject& object, LWObject::Face& face) {
    Point vertex1 = object.vertices[face.vert1];
    Point vertex2 = object.vertices[face.vert2];
    Point vertex3 = object.vertices[face.vert3];

    object.normals[face.norm1] = ~(~(vertex2 - vertex1) % ~(vertex3 - vertex1));
    object.normals[face.norm2] = ~(~(vertex3 - vertex2) % ~(vertex1 - vertex2));
    object.normals[face.norm3] = ~(~(vertex1 - vertex3) % ~(vertex2 - vertex3));
}

void displaceFace(LWObject& object, LWObject::Face& face, int materialIndex, bool* isDisplaced) {
    if (!isDisplaced[face.vert1]) {
        displaceVertex(object, materialIndex, face.vert1, face.norm1, face.tex1);
        isDisplaced[face.vert1] = true;
    }
    if (!isDisplaced[face.vert2]) {
        displaceVertex(object, materialIndex, face.vert2, face.norm2, face.tex2);
        isDisplaced[face.vert2] = true;
    }
    if (!isDisplaced[face.vert3]) {
        displaceVertex(object, materialIndex, face.vert3, face.norm3, face.tex3);
        isDisplaced[face.vert3] = true;
    }
    recalculateNormals(object, face);
}

void displacementTest() {

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
            divideFace(cow, displacedObject, *it, 2);
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
        displaceFace(displacedObject, displacedObject.faces[i], materialIndex, isDisplaced);
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
    //    cam2.addRef();

    //Set up the integrator
    IntegratorImpl integrator;
    integrator.addRef();
    integrator.scene = &scene;
    PointLightSource pls;

    pls.falloff = float4(0, 0, 1, 0);

    pls.intensity = float4::rep(1.2f);
    pls.position = Point(-3.3, 0.0f, 8.5f);
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
    img.writePNG("pictures/result_displacementTest.png");

}