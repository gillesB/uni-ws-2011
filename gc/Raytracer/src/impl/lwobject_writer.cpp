/*
 * This File was written by Gilles Baatz, please do NOT consider this part for the grading.
 */

#include "stdafx.h"
#include "lwobject.h"
#include "phong_shaders.h"

#ifdef __unix
#include <libgen.h>
#define _strnicmp strncasecmp
#endif

#ifdef _WIN32
#define _PATH_SEPARATOR '\\'
#endif
#ifdef __unix
#define _PATH_SEPARATOR '/'
#endif

void LWObject::write(const std::string &_fileName) {

    std::ofstream outfile;
    outfile.open(_fileName.c_str());
    for (int i = 0; i < vertices.size(); i++) {
        Point v = vertices[i];
        outfile << "v " << v.x << " " << v.y << " " << v.z << "\n";
    }

    for (int i = 0; i < normals.size(); i++) {
        Vector vn = normals[i];
        outfile << "vn " << vn.x << " " << vn.y << " " << vn.z << "\n";
    }

    for (int i = 0; i < faces.size(); i++) {
        Face f = faces[i];
        outfile << "f " << f.vert1 + 1 << "//" << f.norm1 + 1 << " " << f.vert2 + 1 << "//" << f.norm2 + 1 << " " << f.vert3 + 1 << "//" << f.norm3 + 1 << "\n";
    }


    outfile.close();

}
