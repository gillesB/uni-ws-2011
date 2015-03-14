// Defines the entry Point for the console application.

/* IMPORTANT!
 * Initially this project was a groupwork between Gilles Baatz and Paul Hirtz.
 * But then Gilles Baatz decided to drop this course.
 * The scene contains lot of materials, which make use of shaders written by
 * Gilles Baatz. Therefore I kept his code but clearly marked the files by
 * a sentence at the beginning of each file.
 *
 * The raytracer is based upon the Tasksheet04_Solution and has been extended by
 * the following files:
 * Surface Shading:
 *      -Procedural Shaders: allProceduralShaders.h, mountainShader.h, parquetShader.h,
 *          proceduralShaders.cpp, proceduralShaders.h, woodShader.h
 *      -Reflective and Refractive Transparency: transparentShader.h
 *      (mirrirShader.h)
 * Modeling:
 *      -Fractal Geometry: fractalGeometryTest.cpp
 *      -Displacement Mapping: displacer.h
 * Texturing:
 *      -Bump Mapping: bumpmapShader.h
 */

#include "stdafx.h"

//void fractalGeometryTest();
void mountainAndTable(int resolutionX, int resolutionY);

int main(int argc, char* argv[]) {
    try {
        int resolutionX = 1280;
        int resolutionY = 960;

        if (argc == 3) {
            resolutionX = atoi(argv[1]);
            resolutionY = atoi(argv[2]);
        }

        mountainAndTable(resolutionX, resolutionY);

    }    catch (const std::exception &_ex) {
        std::cerr << "Error: " << _ex.what() << std::endl << "Terminating...";
    }    catch (...) {
        std::cerr << "Unhandled exception. \n Terminating...";
    }
}


