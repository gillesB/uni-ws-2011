#ifndef __PRIMITIVES_H_INCLUDED_29F93A38_6FEC_4D65_82B0_DC9B7CB5848A
#define __PRIMITIVES_H_INCLUDED_29F93A38_6FEC_4D65_82B0_DC9B7CB5848A
#ifdef _MSC_VER
	#pragma once
#endif

#include "../core/algebra.h"
#include "basic_definitions.h"

//TODO: implement an infinite plane
struct InfinitePlane : public Primitive
{
	float4 color;

	InfinitePlane() {}
	InfinitePlane(Point _origin, Vector _normal, float4 _color) 
		: color(_color)
	{}

	virtual void intersect(const Ray& _ray, HitPoint *_result, float _maxDistance) const
	{}

	virtual float4 getColor() const {return color;}
};

//TODO: implement a sphere
struct Sphere : public Primitive
{
	float4 color;

	Sphere() {}
	Sphere(Point _center, float _radius, float4 _color) 
		: color(_color) {}

	virtual void intersect(const Ray& _ray, HitPoint *_result, float _maxDistance) const
	{}

	virtual float4 getColor() const {return color;}
};

//TODO: implement a triangle
struct Triangle : public Primitive
{
	float4 color;

	Triangle(){}
	Triangle(Point _p1, Point _p2, Point _p3, float4 _color)
		: color(_color) {}


	virtual void intersect(const Ray& _ray, HitPoint *_result, float _maxDistance) const
	{}

	virtual float4 getColor() const {return color;}
};

#endif //__PRIMITIVES_H_INCLUDED_29F93A38_6FEC_4D65_82B0_DC9B7CB5848A
