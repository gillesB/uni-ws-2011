#include "stdafx.h"

#include "core/image.h"
#include "rt/basic_definitions.h"

#include "rt/primitives.h"
#include "rt/cameras.h"


typedef std::vector<Primitive*> t_primitiveVector;

void renderImage(Image &_img, Camera &_camera, const t_primitiveVector &_primitives) {
    t_primitiveVector primitives;

    //TODO: Currently this routine clears the frame buffer with the color red. Implement the basic
    //  ray tracer from the assignment sheet
    //NOTE: Colors are stored and represented as float4. The x, y, and z components map to red, green and blue. The
    //	.w component is currently ignored when saving the image. All values in the red, green, and blue channels
    //  are floating point numbers \in [0..1]

    //For each pixel
    for (uint y = 0; y < _img.height(); y++)
        for (uint x = 0; x < _img.width(); x++) {
            HitPoint hitPoint1, hitPoint2;
            HitPoint *curHitPoint = &hitPoint1, *bestHitPoint = &hitPoint2;
            bestHitPoint->distance = FLT_MAX;

            //TODO: Generate a primary ray using the supplied camera
            //	Than, walk all primitives to find the closest to the origin
            //  Finally put the color of the closest primitive in the frame buffer (_img)
            //
            //	When you invoke the intersect method of a primitive, pass the curHitPoint
            //	as parameter. Then, if curHitPoint->distance < bestHitPoint->distance,
            //	simply swap curHitPoint and bestHitPoint. The closest intersection will be
            //	in bestHitPoint. Also, you need to track the object of the closest intersection.

            //TODO: Set the color of the pixel to the color of the primitive, or to black if no intersection found.
            _img(x, y) = float4(1, 0, 0, 0);
        }
}

void assigment1() {
    Image img(800, 600);

    t_primitiveVector scene;

    //Set up the scene
    Sphere s1(Point(-2.f, 1.7f, 0), 2, float4(1, 0, 0, 0));
    Sphere s2(Point(1, -1, 1), 2.2f, float4(0, 1, 0, 0));
    Sphere s3(Point(3.f, 0.8f, -2), 2, float4(0, 0, 1, 0));
    scene.push_back(&s1);
    scene.push_back(&s2);
    scene.push_back(&s3);

    InfinitePlane p1(Point(0, -1, 0), Vector(0, 1, 0), float4(1, 1, 0, 0));
    scene.push_back(&p1);

    Triangle t1(Point(-2, 3.7f, 0), Point(1, 2, 1), Point(3, 2.8f, -2), float4(0, 1, 1, 0));
    Triangle t2(Point(3, 2, 3), Point(3, 2, -3), Point(-3, 2, -3), float4(1, 1, 1, 0));
    scene.push_back(&t1);
    scene.push_back(&t2);


    //Set up the cameras
    PerspectiveCamera cam1(Point(0, 0, 10), Vector(0, 0, -1), Vector(0, 1, 0), 60,
            std::make_pair(img.width(), img.height()));
    PerspectiveCamera cam2(Point(-8, 3, 8), Vector(1, -0.1f, -1), Vector(0, 1, 0), 45,
            std::make_pair(img.width(), img.height()));
    PerspectiveCamera cam3(Point(-8, 3, 8), Vector(1, -0.1f, -1), Vector(1, 1, 0), 45,
            std::make_pair(img.width(), img.height()));

    Ray r = cam1.getPrimaryRay(0, 0);
    r.d.print();
    r = cam1.getPrimaryRay(0, 600);
    r = cam1.getPrimaryRay(800, 0);
    r = cam1.getPrimaryRay(800, 600);

    //Render
    /*renderImage(img, cam1, scene);
    img.writePNG("result_cam1.png");

    renderImage(img, cam2, scene);
    img.writePNG("result_cam2.png");

    renderImage(img, cam3, scene);
    img.writePNG("result_cam3.png");*/
}
