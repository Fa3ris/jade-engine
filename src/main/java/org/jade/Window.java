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
import org.jade.render.GradientTriangle;
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

  private GradientTriangle gradientTriangle;

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

      // re-render during resizing
      glClear(GL_COLOR_BUFFER_BIT); // clear the frame buffer
      render();
      glfwSwapBuffers(windowHandle); // swap the color buffers
    });

    gradientTriangle = new GradientTriangle();

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

    logger.info("clean window");
    gradientTriangle.clean();

  }

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

    gradientTriangle.render();
    
// ##############################################

    final String triVertSrc = readFile("shaders/triangle/vertexShader.glsl");
    final int triVertId = GL30.glCreateShader(GL30.GL_VERTEX_SHADER);

    GL30.glShaderSource(triVertId, triVertSrc);
    GL30.glCompileShader(triVertId);

    if (GL30.glGetShaderi(triVertId, GL30.GL_COMPILE_STATUS) == GL30.GL_FALSE) {
      logger.error("cannot compile triangle vertex shader {}", GL30.glGetShaderInfoLog(triVertId));
      throw new RuntimeException("vertex shader compilation error");
    }

    final String triFragSrc = readFile("shaders/triangle/fragmentShader.glsl");
    final int triFragId = GL30.glCreateShader(GL30.GL_FRAGMENT_SHADER);

    GL30.glShaderSource(triFragId, triFragSrc);
    GL30.glCompileShader(triFragId);

    if (GL30.glGetShaderi(triFragId, GL30.GL_COMPILE_STATUS) == GL30.GL_FALSE) {
      logger.error("cannot compile triangle fragment shader {}", GL30.glGetShaderInfoLog(triFragId));
      throw new RuntimeException("fragment shader compilation error");
    }

    final int triProgId = GL30.glCreateProgram();

    GL30.glAttachShader(triProgId, triVertId);
    GL30.glAttachShader(triProgId, triFragId);
    GL30.glLinkProgram(triProgId);
    GL30.glValidateProgram(triProgId);

    if (GL30.glGetProgrami(triProgId, GL30.GL_LINK_STATUS) == GL30.GL_FALSE) {
      logger.error("cannot link shader program {}", GL30.glGetProgramInfoLog(triProgId));
      throw new RuntimeException("shader program linking error");
    }

    // can delete shaders once they have been linked
    GL30.glDeleteShader(triVertId);
    GL30.glDeleteShader(triFragId);

    // each vertex position uses 3 floats
    // tightly packed there is not space between consecutive vertex data
    final float[] vertices = {
        -0.5f, -0.5f, 0.0f,
        0.5f, -0.5f, 0.0f,
        0.0f,  0.5f, 0.0f
    };

    final int triangleVAOID = GL30.glGenVertexArrays();

    GL30.glBindVertexArray(triangleVAOID);

    final int triangleVBOID = GL30.glGenBuffers();

    GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, triangleVBOID);
    GL30.glBufferData(GL30.GL_ARRAY_BUFFER, vertices, GL30.GL_STATIC_DRAW);

    GL30.glVertexAttribPointer(0,
        3,
        GL30.GL_FLOAT,
        false,
        3 * Float.BYTES, // could use 0 if array is tightly packed, GL will compute automatically
        0);
    GL30.glEnableVertexAttribArray(0);

    GL30.glUseProgram(triProgId);

    // draw commands
    GL30.glDrawArrays(GL30.GL_TRIANGLES, 0, 3);

    // need to wait to attach to VAO before unbinding
    GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, 0);

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
