#version 330 core
layout (location = 0) in vec3 in_position;

out vec4 out_color;

void main()
{
    gl_Position = vec4(in_position, 1.0); // swizzling

    out_color = vec4(0.5, 0.0, 0.0, 1.0); // dark-red
}