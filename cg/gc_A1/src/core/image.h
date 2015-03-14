#ifndef __IMAGE_H_INCLUDED_8D3A98E5_95EC_400B_8807_28F30EAA674D
#define __IMAGE_H_INCLUDED_8D3A98E5_95EC_400B_8807_28F30EAA674D
#ifdef _MSC_VER
	#pragma once
#endif

#include "defs.h"
#include "algebra.h"

//An image class that can also be saved to a PNG
//Colors are represented as float4, with red mapped to .x, blue <-> .y, green <-> .z
//	.w is not used.
//Color components are in the range 0..1
class Image
{
	std::vector<float4> m_bits;
	uint m_width, m_height;
public:
	Image(uint _width, uint _height)
		: m_width(_width), m_height(_height)
	{
		m_bits.resize(_width * _height);
	}

	float4 *getBits() { return &m_bits[0]; }
	const float4 * getBits() const { return &m_bits[0]; }

	uint width() const {return m_width;}
	uint height() const {return m_height;}

	//An operator for accessing the image in the form img(x, y)
	//Example:
	//	Image img(800, 600);
	//	img(1, 2) = float4(1, 0, 0, 0); //Set pixel x = 1, y = 2 to red (RGB: 1, 0, 0)
	//	float4 col = img(1, 2); //Get the color of pixel x = 1, y = 2
	float4& operator() (uint _x, uint _y)
	{
		_ASSERT(_x < m_width && _y < m_height);
		return m_bits[_y * m_width + _x];
	}

	//The same () operator as above, defined for const this objects, to be able to retrieve colors from them.
	const float4& operator() (uint _x, uint _y) const
	{
		_ASSERT(_x < m_width && _y < m_height);
		return m_bits[_y * m_width + _x];
	}

	//Clears the image to a specified color
	void clear(float4 _color)
	{
		for(uint i = 0; i < m_width * m_height; i++)
			m_bits[i] = _color;
	}

	//Writes the image to a file name
	void writePNG(std::string _fileName);
};

#endif //__IMAGE_H_INCLUDED_8D3A98E5_95EC_400B_8807_28F30EAA674D
