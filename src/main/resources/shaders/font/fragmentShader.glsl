#version 330 core
in vec2 TexCoord;

out vec4 FragColor;

uniform sampler2D texture1;

void main() {
    vec4 charTex = texture(texture1, TexCoord);
    FragColor = vec4(1.0f, 0.5f, 0.0f, 1.0f) * charTex.w;  // get alpha from texture
}