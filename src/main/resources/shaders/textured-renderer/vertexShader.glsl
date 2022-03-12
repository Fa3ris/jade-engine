#version 330 core
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec2 aTexCoord;
layout (location = 2) in float aTexId;

out vec2 TexCoord;
out float TexId;

// uniform mat4 transform;

void main()
{
    gl_Position = vec4(aPos, 1.0);
    TexCoord = aTexCoord;
    TexId = aTexId;

}