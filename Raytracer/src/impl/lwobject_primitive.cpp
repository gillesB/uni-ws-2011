#include <stdlib.h>

#include "stdafx.h"
#include "lwobject.h"
#include "../core/util.h"

SmartPtr<Shader> LWObject::Face::getShader(IntRet _intData) const
{
	SmartPtr<ExtHitPoint> hit = _intData.hitInfo;

	SmartPtr<PluggableShader> shader = m_lwObject->materials[material].shader->clone();

	shader->setPosition(Point::lerp(m_lwObject->vertices[vert1], m_lwObject->vertices[vert2], 
		m_lwObject->vertices[vert3], hit->intResult.x, hit->intResult.y));


	Vector norm = 
		m_lwObject->normals[norm1] * hit->intResult.x + 
		m_lwObject->normals[norm2] * hit->intResult.y + 
		m_lwObject->normals[norm3] * hit->intResult.z;
	
	shader->setNormal(norm);

	if(tex1 != -1 && tex2 != -1 && tex3 != -1)
	{
		float2 texPos = 
			m_lwObject->texCoords[tex1] * hit->intResult.x +  
			m_lwObject->texCoords[tex2] * hit->intResult.y +  
			m_lwObject->texCoords[tex3] * hit->intResult.z;

//                if(((float2)m_lwObject->texCoords[tex1]).x>1.0 || ((float2)m_lwObject->texCoords[tex1]).y>1.0 || ((float2)m_lwObject->texCoords[tex2]).x>1.0 || ((float2)m_lwObject->texCoords[tex2]).y>1.0 || ((float2)m_lwObject->texCoords[tex3]).x>1.0 || ((float2)m_lwObject->texCoords[tex3]).y>1.0){
//                    std::cout<<"Used UV out of [0,1] " <<" tex1: " << ((float2)m_lwObject->texCoords[tex1]).x<<" "<<((float2)m_lwObject->texCoords[tex1]).y<<" tex2: "<<((float2)m_lwObject->texCoords[tex2]).x<<" "<< ((float2)m_lwObject->texCoords[tex2]).y<<" tex3: "<<((float2)m_lwObject->texCoords[tex3]).x<<" " << ((float2)m_lwObject->texCoords[tex3]).y << std::endl;
//                }

		shader->setTextureCoord(texPos);
	}

	return shader;
}


Primitive::IntRet LWObject::Face::intersect(const Ray& _ray, float _previousBestDistance) const
{
	IntRet ret;

	float4 inter = 
		intersectTriangle(
			m_lwObject->vertices[vert1], m_lwObject->vertices[vert2], m_lwObject->vertices[vert3], _ray
		);

	ret.distance = inter.w;

	if(inter.w < _previousBestDistance)
	{
		SmartPtr<ExtHitPoint> hit = new ExtHitPoint;
		ret.hitInfo = hit;
		hit->intResult = inter;
	}

	return ret;
}


BBox LWObject::Face::getBBox() const
{

	BBox ret = BBox::empty();
	ret.extend(m_lwObject->vertices[vert1]);
	ret.extend(m_lwObject->vertices[vert2]);
	ret.extend(m_lwObject->vertices[vert3]);

	return ret;
}

void LWObject::addReferencesToScene(std::vector<Primitive*> &_scene) const
{
	for(std::vector<Face>::const_iterator it = faces.begin(); it != faces.end(); it++)
		_scene.push_back((Primitive*)&*it);
}
