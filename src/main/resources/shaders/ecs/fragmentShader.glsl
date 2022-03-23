#version 330 core
out vec4 FragColor;

in vec2 TexCoord;
in float TexId;

// more like samplers ?
uniform sampler2D textures[8];

void main()
{
//    FragColor = mix(texture(texture1, TexCoord), texture(texture2, TexCoord), 0.2);
//    FragColor = vec4(1.0, 0.0, 0.0, 0.5) * texture(textures[int(TexId)], TexCoord);
    FragColor = texture(textures[int(TexId)], TexCoord);
}