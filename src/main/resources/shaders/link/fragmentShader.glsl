#version 330 core

in vec4 out_color; // color sent by vertex shader

out vec4 FragColor;

void main()
{
    FragColor = out_color;
}