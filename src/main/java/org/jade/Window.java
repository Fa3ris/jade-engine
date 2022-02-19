package org.jade;


import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_MAXIMIZED;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Objects;
import org.lwjgl.BufferUtils;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Window {

  private static final Window instance = new Window();

  private static final Logger logger = LoggerFactory.getLogger(Window.class);

  public static boolean faulty = false;

  private long windowHandle;
  private int w, h;
  private String title;

  private double prevTime = glfwGetTime();
  private double accumulator = 0;

  private int currentFrame = 0;


  private Window() {
    w = 1920;
    h = 1080;
    title = "Mario";
  }

  public static Window getInstance() {
    return instance;
  }

  public void run() {

    logger.info("Hello LWJGL {} !", Version.getVersion());
    logger.debug("start time {} s", prevTime);

    init();
    loop();

    cleanup();

  }


  private void init() {

    // Setup an error callback.
    glfwSetErrorCallback(new ErrorCallback());

    // Initialize GLFW. Most GLFW functions will not work before doing this.
    if ( !glfwInit() )
      throw new IllegalStateException("Unable to initialize GLFW");


    // Configure GLFW
    glfwDefaultWindowHints(); // optional, the current window hints are already the default
    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
    glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable
    glfwWindowHint(GLFW_MAXIMIZED, GLFW_FALSE); // full window

    if (faulty) {
      // request an impossible version of openGL
      glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 99);
      glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
    }

    // Get the resolution of the primary monitor
    GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

    if (vidmode == null) {
      throw new IllegalStateException("Unable to get vidmode");
    }

    w = (int) (vidmode.width() * .5);
    h = (int) (vidmode.height() * .5);
    // create window
    windowHandle = glfwCreateWindow(w, h, title, NULL, NULL);


    if ( windowHandle == NULL )
      throw new RuntimeException("Failed to create the GLFW window");

    // mouse callbacks
    glfwSetCursorPosCallback(windowHandle, MouseListener::mousePosCallback);
    glfwSetMouseButtonCallback(windowHandle, MouseListener::mouseButtonCallback);
    glfwSetScrollCallback(windowHandle, MouseListener::mouseScrollCallback);
    // key callback
    glfwSetKeyCallback(windowHandle, KeyListener::keyCallback);

    // Get the thread stack and push a new frame
    try ( MemoryStack stack = stackPush() ) {
      IntBuffer pWidth = stack.mallocInt(1); // int*
      IntBuffer pHeight = stack.mallocInt(1); // int*

      // Get the window size passed to glfwCreateWindow
      glfwGetWindowSize(windowHandle, pWidth, pHeight);

      // Center the window
      glfwSetWindowPos(
          windowHandle,
          (int) ((vidmode.width() - pWidth.get(0)) * .5),
          (int) ((vidmode.height() - pHeight.get(0)) * .5)
      );
    } // the stack frame is popped automatically


    // Make the OpenGL the current context for the window
    glfwMakeContextCurrent(windowHandle);
    // Enable v-sync

    /*
    * The swap interval indicates how many frames to wait until swapping the buffers,
    * commonly known as vsync.
    * By default, the swap interval is zero, meaning buffer swapping will occur immediately.
    * On fast machines, many of those frames will never be seen, as the screen is still
    * only updated typically 60-75 times per second, so this wastes a lot of CPU and GPU cycles.
    */
    glfwSwapInterval(1);

    // Make the window visible
    glfwShowWindow(windowHandle);

    // This line is critical for LWJGL's interoperation with GLFW's
    // OpenGL context, or any context that is managed externally.
    // LWJGL detects the context that is current in the current thread,
    // creates the GLCapabilities instance and makes the OpenGL
    // bindings available for use.
    GL.createCapabilities();

    // control how to map normalized device coordinates to window coordinates
    // when window gets resized, can change the viewport according to the new dimensions
    GL30.glViewport(0, 0, w, h);

    glfwSetFramebufferSizeCallback(windowHandle, (window, width, height) -> {
      logger.info("window resized to w: {}, h: {}", width, height);
      w = width;
      h = height;
      GL30.glViewport(0, 0, w, h);
    });

  }

  private void loop() {
    // Set the clear color
    glClearColor(0f, 0f, 1f, .5f);

    while (!glfwWindowShouldClose(windowHandle)) {
      ++currentFrame;
      glfwPollEvents();

      double newTime = glfwGetTime();
      double elapsed = newTime - prevTime; // in seconds

      if (elapsed > 0) {
        double fps = 1 / elapsed;
        logger.debug("fps: {}", fps);
      }
      accumulator += elapsed;

      final double step = 1 / 60d;
      final double maxAccumulator = 10 * step;
      accumulator = Math.min(accumulator, maxAccumulator);

      while (accumulator > step) {
        logger.debug("update frame {} with step {} s", currentFrame, step);
        accumulator -= step;
      }

      prevTime = newTime;

      glClear(GL_COLOR_BUFFER_BIT); // clear the frame buffer

      render();

      glfwSwapBuffers(windowHandle); // swap the color buffers

      if (KeyListener.isKeyPressed(GLFW_KEY_SPACE)) {
        logger.info("space is pressed x: {}, y: {}", MouseListener.instance.x, MouseListener.instance.y);
      }

      if (MouseListener.isButtonPressed(GLFW_MOUSE_BUTTON_1)) {
        logger.info("mouse button {} is pressed at x: {}, y: {}, dragging: {}",
            GLFW_MOUSE_BUTTON_1,
            MouseListener.instance.x, MouseListener.instance.y,
            MouseListener.instance.dragging);
      }
      endFrame();
    }

  }



  private String vertexShaderSrc = "#version 330 core\n" + // version of opengl
      "layout (location=0) in vec3 aPos;\n" + // Vertex Array Object Attribute - position of a vertex
      "layout (location=1) in vec4 aColor;\n" + // Vertex Array Object Attribute - color of a vertex
      "out vec4 fColor;\n" + // define output
      "void main()\n" + // called by GPU
      "{\n" +
      "    fColor = aColor;\n" + // map same color
      "    gl_Position = vec4(aPos, 1.0);\n" + // ???
      "}";

  /*
   * VAO = Vertex Array Object
   * struct to store data about 3D model
   *  contain slots called attribute list
   *     each attribute list contain one type of data
   *         one attribute for vertex position
   *         one attribute for color of the vertex
   *         texture
   *         normal vector of the vertex
   *         ...
   *
   *
   * VBO = Vertex Buffer Object
   * struct that is stored in the attribute list = contain the actual data
   *
   *
   * EBO = Element Buffer Object
   * define what vertices are to be grouped to form a triangle,
   * allows to reuse same vertex for different triangle instead of duplicating
   *
   *
   * */
  private void render() {
    // use GL 3.0

    // need one VAO
    final int vaoID = GL30.glGenVertexArrays();

    GL30.glBindVertexArray(vaoID);

    // store vertex positions into buffer
    final float[] positions = {
        -0.5f, 0.5f, 0f,//v0
        -0.5f, -0.5f, 0f,//v1
        0.5f, -0.5f, 0f,//v2
        0.5f, 0.5f, 0f,//v3
    };
    final FloatBuffer floatBuffer =  BufferUtils.createFloatBuffer(positions.length);

    floatBuffer.put(positions);
    floatBuffer.flip(); // change from write-mode to read-mode

    // need one VBO to store in one of the attribute list of the VAO
    final int vboID = GL30.glGenBuffers();

    GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vboID);

    // put data into the VBO
    GL30.glBufferData(GL30.GL_ARRAY_BUFFER, floatBuffer, GL30.GL_STATIC_DRAW);

    // attribute list index to bind the VBO to
    // must be 0 because attribute 0 must be enabled
    // TODO see why
    final int attributeListIndex = 0;

    // define properties of one of the attribute list of the VAO
    // and bind the currently bound VBO
    /*
    * Each attribute which is stated in the Vertex Array Objects state vector
    * may refer to a different Vertex Buffer Object.
    * This reference is stored when glVertexAttribPointer is called.
    * Then the buffer which is currently bound to the target ARRAY_BUFFER
    * is associated to the attribute and the name (value) of the object is stored
    * in the state vector of the VAO.
    * The ARRAY_BUFFER binding is a global state.
    * */
    GL30.glVertexAttribPointer(
        attributeListIndex, // which attribute list
        3, // number of values per vertex
        GL30.GL_FLOAT, // type of value
        false,
        0, // offset between to vertices. > 0 if contain intermediary values, why do that ?
        0); // where to begin

    // unbind VBO
    GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, 0);

    int[] indices= { // sens trigonométrique
        0,1,3, //top left triangle (v0, v1, v3)
        3,1,2 //bottom right triangle (v3, v1, v2)
    };

    IntBuffer intBuffer = BufferUtils.createIntBuffer(indices.length);
    intBuffer.put(indices);
    intBuffer.flip();

    // index buffer object
    // warning DO NOT unbind IBO !!!
    int iboID = GL30.glGenBuffers();

    // bind index buffer to the currently bound VAO
    GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, iboID);

    GL30.glBufferData(GL30.GL_ELEMENT_ARRAY_BUFFER, intBuffer, GL30.GL_STATIC_DRAW);

    // unbind VAO
    GL30.glBindVertexArray(0);

    /* Shader loading */
    final String vertexSource = readFile("shaders/vertexShader.glsl");
    logger.debug("vertex source is {}", vertexSource);

    final int vertexID = GL30.glCreateShader(GL30.GL_VERTEX_SHADER);
    GL30.glShaderSource(vertexID, vertexSource);
    GL30.glCompileShader(vertexID);

    if (GL30.glGetShaderi(vertexID, GL30.GL_COMPILE_STATUS) == GL30.GL_FALSE) {
      logger.error("cannot compile vertex shader {}", GL30.glGetShaderInfoLog(vertexID));
      throw new RuntimeException("vertex shader compilation error");
    }

    final String fragmentSource = readFile("shaders/fragmentShader.glsl");
    logger.debug("fragment source is {}", fragmentSource);

    final int fragmentID = GL30.glCreateShader(GL30.GL_FRAGMENT_SHADER);
    GL30.glShaderSource(fragmentID, fragmentSource);
    GL30.glCompileShader(fragmentID);

    if (GL30.glGetShaderi(fragmentID, GL30.GL_COMPILE_STATUS) == GL30.GL_FALSE) {
      logger.error("cannot compile fragment shader {}", GL30.glGetShaderInfoLog(fragmentID));
      throw new RuntimeException("fragment shader compilation error");
    }

    final int programID = GL30.glCreateProgram();

    GL30.glAttachShader(programID, vertexID);
    GL30.glAttachShader(programID, fragmentID);

    GL30.glBindAttribLocation(
        programID,
        attributeListIndex,
        "position" // which variable in vertex shader to bind
    );

    GL30.glLinkProgram(programID);
    GL30.glValidateProgram(programID);

    GL30.glUseProgram(programID);

    /* Shader loading END */

    // actual draw
    GL30.glBindVertexArray(vaoID);
    GL30.glEnableVertexAttribArray(attributeListIndex);

    GL30.glDrawElements(
        GL30.GL_TRIANGLES,
        indices.length, // number of vertices
        GL30.GL_UNSIGNED_INT, // type of index values
        0); // where to start if index buffer object is bound
    GL30.glDisableVertexAttribArray(0);
    GL30.glBindVertexArray(0);

    /* Shader unloading */

    GL30.glUseProgram(0);

    GL30.glDetachShader(programID, vertexID);
    GL30.glDetachShader(programID, fragmentID);

    GL30.glDeleteShader(vertexID);
    GL30.glDeleteShader(fragmentID);
    GL30.glDeleteProgram(programID);

    /* Shader unloading END */

    // clean up
    GL30.glDeleteBuffers(vboID);
    GL30.glDeleteBuffers(iboID);
    GL30.glDeleteVertexArrays(vaoID);
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

  private void endFrame() {
    MouseListener.endFrame();
  }

  private void cleanup() {
    // Free the window and associated callbacks
    glfwFreeCallbacks(windowHandle);
    glfwDestroyWindow(windowHandle);

    // Terminate GLFW and free the separate error callback
    glfwTerminate();
    try {
      Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    } catch (Exception e) {
      logger.error("cannot free error callback");
    }
  }

}
