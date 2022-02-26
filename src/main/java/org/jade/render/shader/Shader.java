package org.jade.render.shader;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Objects;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Shader {

  private final Logger logger = LoggerFactory.getLogger(Shader.class);

  final int programID;

  /**
   *
   * @param vertexPath absolute path starting from classpath root WITHOUT leading slash '/'
   * @param fragmentPath absolute path starting from classpath root WITHOUT leading slash '/'
   */
  public Shader(String vertexPath, String fragmentPath) {

    final int vertexID = GL30.glCreateShader(GL30.GL_VERTEX_SHADER);

    GL30.glShaderSource(vertexID, readFile(vertexPath));
    GL30.glCompileShader(vertexID);

    if (GL30.glGetShaderi(vertexID, GL30.GL_COMPILE_STATUS) == GL30.GL_FALSE) {
      logger.error("cannot compile vertex shader {}", GL30.glGetShaderInfoLog(vertexID));
      throw new RuntimeException("vertex shader compilation error");
    }

    final int triFragId = GL30.glCreateShader(GL30.GL_FRAGMENT_SHADER);
    GL30.glShaderSource(triFragId, readFile(fragmentPath));
    GL30.glCompileShader(triFragId);

    if (GL30.glGetShaderi(triFragId, GL30.GL_COMPILE_STATUS) == GL30.GL_FALSE) {
      logger.error("cannot compile fragment shader {}", GL30.glGetShaderInfoLog(triFragId));
      throw new RuntimeException("fragment shader compilation error");
    }

    programID = GL30.glCreateProgram();

    GL30.glAttachShader(programID, vertexID);
    GL30.glAttachShader(programID, triFragId);
    GL30.glLinkProgram(programID);
    GL30.glValidateProgram(programID);

    if (GL30.glGetProgrami(programID, GL30.GL_LINK_STATUS) == GL30.GL_FALSE) {
      logger.error("cannot link shader program {}", GL30.glGetProgramInfoLog(programID));
      throw new RuntimeException("shader program linking error");
    }

    // can delete shaders once they have been linked
    GL30.glDeleteShader(vertexID);
    GL30.glDeleteShader(triFragId);
  }

  public void bindAttribute(int index, String name) {
    GL30.glBindAttribLocation(
        programID,
        index,
        name // which variable in vertex shader to bind
    );
  }

  public void use() {
    GL30.glUseProgram(programID);
  }

  public void delete() {
    GL30.glDeleteProgram(programID);
  }

  /**
   * need to pass off-heap memory buffer
   */
  @Deprecated
  public void setUniform4fv(String name, FloatBuffer buffer) {
    use();
    int uniformLocation = GL30.glGetUniformLocation(programID, name);
    GL30.glUniform4fv(uniformLocation, buffer);
  }

  public void setUniform4fv(String name, float[] vec) {
    use();
    try (MemoryStack ignored = MemoryStack.stackPush()) {
      FloatBuffer buffer = MemoryStack.stackMallocFloat(4);
      buffer.put(vec);
      buffer.flip();
      int uniformLocation = GL30.glGetUniformLocation(programID, name);
      GL30.glUniform4fv(uniformLocation, buffer);
    }
  }

  @Deprecated
  public void setUniformMatrix4fv(String name, FloatBuffer mat) {
    use();
    int uniformLocation = GL30.glGetUniformLocation(programID, name);
    GL30.glUniformMatrix4fv(uniformLocation, false, mat);
  }

  public void setUniformMatrix4fv(String name, Matrix4f mat) {
    use();
    try (MemoryStack stack = MemoryStack.stackPush()) {
      FloatBuffer buffer = mat.get(stack.mallocFloat(16));
      int uniformLocation = GL30.glGetUniformLocation(programID, name);
      GL30.glUniformMatrix4fv(uniformLocation, false, buffer);
    }
  }

  @Deprecated
  public void setUniform1iv(String name, IntBuffer buffer) {
    use();
    int uniformLocation = GL30.glGetUniformLocation(programID, name);
    GL30.glUniform1iv(uniformLocation, buffer);
  }

  public void setUniform1iv(String name, int value) {
    use();
    try (MemoryStack ignored = MemoryStack.stackPush()) {
      IntBuffer buffer = MemoryStack.stackMallocInt(1);
      buffer.put(value);
      buffer.flip();
      int uniformLocation = GL30.glGetUniformLocation(programID, name);
      GL30.glUniform1iv(uniformLocation, buffer);
    }
  }

  private String readFile(String path) {
    try {
      return new String(Objects.requireNonNull(
          getClass().getClassLoader().getResourceAsStream(path)).readAllBytes());
    } catch (Exception e) {
      logger.error("cannot load file at path {}", path, e);
      throw new RuntimeException();
    }
  }

}
