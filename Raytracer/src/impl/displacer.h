/*
 * This Header was written by Paul Hirtz, please DO consider this part for the grading.
 */


#ifndef DISPLACER_H
#define	DISPLACER_H

class Displacer {
private:
    const static float epsilon = 0.00000001;

    //Create new face with the given indices  for the vertices, normals and texture coordinates
    //and push it to the given LWObject

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


    //displace a vertex based on the height information out of the displacementmap

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

    //recalculate the normals of a given face

    void recalculateNormals(LWObject& object, LWObject::Face& face) {
        Point vertex1 = object.vertices[face.vert1];
        Point vertex2 = object.vertices[face.vert2];
        Point vertex3 = object.vertices[face.vert3];

        object.normals[face.norm1] = ~(~(vertex2 - vertex1) % ~(vertex3 - vertex1));
        object.normals[face.norm2] = ~(~(vertex3 - vertex2) % ~(vertex1 - vertex2));
        object.normals[face.norm3] = ~(~(vertex1 - vertex3) % ~(vertex2 - vertex3));
    }

public:

    // divide a face into 4 new faces recursivly

    void divideFace(LWObject& displacedObject, LWObject::Face& face, int recursionDepth) {

        if (recursionDepth != 0) {

            //Vertex v1 of Face and Index of it in the Vertices-index-array
            Point v1 = displacedObject.vertices[face.vert1];
            int vIndex1 = face.vert1;

            //Vertex v2 of Face and Index of it in the Vertices-index-array
            Point v2 = displacedObject.vertices[face.vert2];
            int vIndex2 = face.vert2;

            //Vertex v3 of Face and Index of it in the Vertices-index-array
            Point v3 = displacedObject.vertices[face.vert3];
            int vIndex3 = face.vert3;

            //Devide the 3 edges in the middle
            //Divide the edge of the face in the middle and create new Vertex, index of
            // vertex in the Vertices-index-array; p1 in the middle between v1 and v2
            Point p1 = v1 + (v2 - v1) / 2;
            displacedObject.vertices.push_back(p1);
            int pIndex1 = displacedObject.vertices.size() - 1;

            //Divide the edge of the face in the middle and create new Vertex, index of
            // vertex in the Vertices-index-array; p2 in the middle between v2 and v3
            Point p2 = v2 + (v3 - v2) / 2;
            displacedObject.vertices.push_back(p2);
            int pIndex2 = displacedObject.vertices.size() - 1;

            //Divide the edge of the face in the middle and create new Vertex, index of
            // vertex in the Vertices-index-array; p3 in the middle between v1 and v3
            Point p3 = v3 + (v1 - v3) / 2;
            displacedObject.vertices.push_back(p3);
            int pIndex3 = displacedObject.vertices.size() - 1;


            //Normal of Vertex v1 and Index of it in the normals-index-array
            Vector n1 = displacedObject.normals[face.norm1];
            int nIndexV1 = face.norm1;

            //Normal of Vertex v2 and Index of it in the normals-index-array
            Vector n2 = displacedObject.normals[face.norm2];
            int nIndexV2 = face.norm2;

            //Normal of Vertex v3 and Index of it in the normals-index-array
            Vector n3 = displacedObject.normals[face.norm3];
            int nIndexV3 = face.norm3;


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
            float2 t1 = displacedObject.texCoords[face.tex1];
            int tIndex1 = face.tex1;

            //texture coordinate of v2
            float2 t2 = displacedObject.texCoords[face.tex2];
            int tIndex2 = face.tex2;

            //texture coordinate of v3
            float2 t3 = displacedObject.texCoords[face.tex3];
            int tIndex3 = face.tex3;

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

            divideFace(displacedObject, f1, recursionDepth);
            divideFace(displacedObject, f2, recursionDepth);
            divideFace(displacedObject, f3, recursionDepth);
            divideFace(displacedObject, f4, recursionDepth);

        } else {
            return;
        }
    }

    //displace the vertices of a face if not yet displaced

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

};

#endif	/* DISPLACER_H */