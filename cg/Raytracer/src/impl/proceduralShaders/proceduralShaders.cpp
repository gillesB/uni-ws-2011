/*
 * This Header was written by Gilles Baatz, please do NOT consider this part for the grading.
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

#include "proceduralShaders.h"

float4 MarbleLikeShader::colors[13] = {PALE_BLUE, PALE_BLUE,
    MEDIUM_BLUE, MEDIUM_BLUE, MEDIUM_BLUE,
    PALE_BLUE, PALE_BLUE,
    DARK_BLUE, DARK_BLUE,
    DARKER_BLUE, DARKER_BLUE,
    PALE_BLUE, DARKER_BLUE};

unsigned char RenderManFunctions::perm[TABSIZE] = {225, 155, 210, 108, 175, 199, 221, 144, 203, 116, 5, 82, 173, 133, 222, 139, 174, 169, 138, 248,
    36, 2, 151, 194, 235, 65, 224, 217, 27, 81, 7, 22, 121, 229, 63, 8, 165, 110, 237, 117, 231, 51, 172, 157, 47, 162, 115, 44, 43, 124, 94, 150,
    53, 131, 84, 57, 220, 197, 58, 24, 15, 179, 18, 215, 153, 26, 200, 226, 119, 12, 76, 34, 187, 140, 164, 236, 232, 120,
    70, 213, 69, 158, 33, 252, 90, 246, 75, 130, 91, 191, 25, 113, 228, 159, 205, 253, 134, 142, 89, 103, 96, 104, 156,
    80, 212, 176, 250, 16, 141, 247, 50, 208, 39, 49, 32, 10, 198, 223, 255, 11, 241, 46, 31, 123, 168, 125, 249, 17, 201, 129,
    20, 181, 111, 239, 218, 87, 55, 73, 112, 182, 244, 195, 227, 1, 243, 148, 102, 166, 28, 99, 242, 136, 189, 72, 3, 192, 62, 202, 6, 128, 167, 23, 188,
    13, 35, 77, 196, 185, 68, 183, 230, 177, 135, 160, 180, 38, 238, 251, 37, 240, 126, 64, 74, 161, 40, 66, 29, 59, 146, 61, 254, 107, 42, 86, 154, 4,
    21, 233, 209, 45, 98, 193, 114, 184, 149, 171, 178, 101, 48, 41, 71, 56, 132, 211, 152, 170, 163, 106, 9, 79, 147, 85, 30, 207, 219, 137, 214, 145, 93, 92, 100, 245,
    54, 78, 19, 206, 88, 234, 190, 122, 0, 216, 186, 60, 95, 83, 105, 14, 118, 127, 67, 143, 109, 97, 204, 52};
