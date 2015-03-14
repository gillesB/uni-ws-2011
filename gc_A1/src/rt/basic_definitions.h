#ifndef __BASICS_H_INCLUDED_4584228C_AB0F_4667_BFC9_64C37E40BD19
#define __BASICS_H_INCLUDED_4584228C_AB0F_4667_BFC9_64C37E40BD19
#ifdef _MSC_VER
	#pragma once
#endif

#include "../core/defs.h"
#include "../core/algebra.h"

//The basic ray tracing structures

//A ray
struct Ray
{
	Point o; //origin
	Vector d; //direction
};

//The basic structure determining a hit point. 
//Currently, it's only member is the distance to the intersection
struct HitPoint
{
	//Set to FLT_MAX if no intersection found
	float distance;

	//We will need this one in a later exercise
	virtual ~HitPoint () {}
};

//A class for a primitive
class Primitive
{
public:
	//This routine retrieves the color of the primitive, which will later 
	//	be used for determining the color of the pixel.
	virtual float4 getColor() const = 0;

	//This routine intersects the ray with the primitive and fills 
	//	the _result. Currently, it should only set _result->distance.
	//	Set the distance to FLT_MAX if you do not find an intersection
	//	with the primitive in the interval (INTEPS(), _maxDistance)
	virtual void intersect(const Ray& _ray, HitPoint * _result, float _maxDistance) const = 0;

	static const float INTEPS() { return 0.0000001f;};
};

//This is the basic class for a camera, used to get a primary for a pixel
class Camera
{
public:
	//Returns the primary ray for pixel _x, _y. 
	virtual Ray getPrimaryRay(float _x, float _y) = 0;
};

#endif //__BASICS_H_INCLUDED_4584228C_AB0F_4667_BFC9_64C37E40BD19
