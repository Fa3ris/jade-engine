package org.jade;


import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_G;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
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
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.IntBuffer;
import java.util.Objects;
import org.jade.render.ColoredQuadRenderer.ColoredVertex;
import org.jade.render.ColoredQuadRenderer;
import org.jade.render.ColoredQuadRenderer.ColoredQuad;
import org.jade.render.camera.Camera;
import org.jade.render.shader.Shader;
import org.jade.scenes.SceneManager;
import org.jade.scenes.SceneManagerFactory;
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

  private SceneManager sceneManager;

  private long windowHandle;
  private int w, h;
  private final String title;

  /**
   * in seconds
   */
  private double prevTime = glfwGetTime();
  private int currentFrame = 0;

  private final double step = 1 / 60d;
  private double accumulator = 0;
  private final double maxAccumulator = 10 * step;

  private final Camera camera = new Camera();

  private float fovInDegrees = 45f;
  private final double mouseSensitivity = .1d;

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
    afterInit();
    loop();

    cleanup();
  }

  private ColoredQuadRenderer renderer;

  private void afterInit() {
    ColoredVertex[] vertices = new ColoredVertex[4];

    vertices[0] = new ColoredVertex(new float[] {0.5f,  0.5f, 0.0f}, new float[] {1.0f, 0.0f, 0.0f, 1});
    vertices[1] = new ColoredVertex(new float[] {0.5f, -0.5f, 0.0f}, new float[] {0.0f, 1.0f, 0.0f, 1f});
    vertices[2] = new ColoredVertex(new float[] {-0.5f, -0.5f, 0.0f}, new float[] {0.0f, 0.0f, 1.0f, 1f});
    vertices[3] = new ColoredVertex(new float[] {-0.5f,  0.5f, 0.0f}, new float[] {1.0f, 1.0f, 0.0f, 1f});

    ColoredQuad quad = new ColoredQuad(vertices);

    renderer = new ColoredQuadRenderer();

    renderer.addQuad(quad);

    // translate right
    vertices[0].getPosition()[0] += .5f;
    vertices[1].getPosition()[0] += .5f;
    vertices[2].getPosition()[0] += .5f;
    vertices[3].getPosition()[0] += .5f;

    quad = new ColoredQuad(vertices);
    renderer.addQuad(quad);

    // translate up
    vertices[0].getPosition()[1] += .5f;
    vertices[1].getPosition()[1] += .5f;
    vertices[2].getPosition()[1] += .5f;
    vertices[3].getPosition()[1] += .5f;

    quad = new ColoredQuad(vertices);
    renderer.addQuad(quad);

    for (int i = 0; i < 9_999; i++) {
      renderer.addQuad(quad);
    }

    Shader shader = new Shader("shaders/colored-renderer/vertexShader.glsl",
        "shaders/colored-renderer/fragmentShader.glsl");

    renderer.setShader(shader);

    sceneManager = SceneManagerFactory.createInstance();

  }

  private void init() {

    // Setup an error callback.
    glfwSetErrorCallback(new ErrorCallback());

    // Initialize GLFW. Most GLFW functions will not work before doing this.
    if ( !glfwInit() ) {
      throw new IllegalStateException("Unable to initialize GLFW");
    }

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
      render();
    });
  }

  private void loop() {
    // Set the clear color to blue
    glClearColor(0f, 0f, 1f, .5f);

    while (!glfwWindowShouldClose(windowHandle)) {
      ++currentFrame;
      glfwPollEvents();

      final double newTime = glfwGetTime();
      final double elapsed = newTime - prevTime; // in seconds

      if (elapsed > 0) {
        double fps = 1 / elapsed;
        logger.info("fps: {}", fps);
      }
      accumulator += elapsed;
      accumulator = Math.min(accumulator, maxAccumulator);

      while (accumulator > step) {
        accumulator -= step;
        logger.debug("update frame {} with step {} s", currentFrame, step);

        sceneManager.update(step);

          /*
          * model matrix : local space -> world space
          * view matrix : world space -> view/camera space
          * projection matrix : view space -> clip space
          *
          * project into the range of -1;1 expected by GL for each axis
          * e.g. for an axis map range -1000;1000 to -1;1
          *
          * frustrum = viewing box = space containing what will be visible
          * near plane / far plane
          *
          * perspective division: divide by the homogeneous coordinate w; has an effect only if w != 1
          * goes from a 4D vector to 3D vector
          * => performed automatically at end of vertex shader execution
          *
          * 2 types of projection
          * ortho: w = 1
          * perspective: w != 1
             FOV: field of view = angle defining width of near and far planes ~ 45°
             * aspect ratio = width / height
          *
          * viewport transform: after mapping to -1;1 normalized device coordinate
          * use viewPort dimension (glViewport) to map to physical screen
          *
          *  */


        /*
        * define camera/view space
        *
        * set camera as the origin of the scene
        *
        * view matrix: transforms world space to coordinates relative to camera position and direction
        *
        * define the basis of the camera = {position, looking direction, right direction, up direction}
        *
        * relative to the world space
        *
        *
        * lookAt matrix: 4x4 matrix using camera basis
        *
        * view coord = lookAt * world coord
        *
        * */


        /*
        * Euler angle
        * yaw : precession = psi = rotation autour de l'axe vertical
        * pitch: nutation = theta = l'inclinaison par rapport à l'axe vertical
        * roll: rotation/giration = phi = rotation de l'objet sur lui-même
        *
        *
        * for the camera
        * given a pitch and yaw, find a corresponding direction to affect to cameraFront
        * */
      }

        /*
        ECS
        Définition générale :

        entité = un id uniquement
        composant = data
        système = algos
       */

      render();

      if (KeyListener.isKeyPressed(GLFW_KEY_SPACE)) {
        logger.info("space is pressed x: {}, y: {}", MouseListener.instance.x, MouseListener.instance.y);
      }

      boolean updateCamera = false;
      float cameraSpeed = (float) step; // or can use elapsed = delta time

      if (KeyListener.isKeyPressed(GLFW_KEY_W)) { // camera forward
        logger.info("w pressed at {}", cameraSpeed);
        camera.forward();
        updateCamera = true;
      }

      if (KeyListener.isKeyPressed(GLFW_KEY_S)) { // camera backward
        logger.info("s pressed at {}", cameraSpeed);
        camera.backward();
        updateCamera = true;
      }

      if (KeyListener.isKeyPressed(GLFW_KEY_A)) { // camera strafe left
        logger.info("a pressed at {}", cameraSpeed);
        camera.strafeLeft();
        updateCamera = true;
      }

      if (KeyListener.isKeyPressed(GLFW_KEY_D)) { // camera strafe right
        logger.info("d pressed at {}", cameraSpeed);
        camera.strafeRight();
        updateCamera = true;
      }

      if (KeyListener.isKeyPressed(GLFW_KEY_F)) { // roll left
        camera.roll(-1);
        updateCamera = true;
      }

      if (KeyListener.isKeyPressed(GLFW_KEY_G)) { // roll right
        camera.roll(1);
        updateCamera = true;
      }

      if (MouseListener.isButtonPressed(GLFW_MOUSE_BUTTON_1)) {
        logger.info("mouse button {} is pressed at x: {}, y: {}, dragging: {}",
            GLFW_MOUSE_BUTTON_1,
            MouseListener.instance.x, MouseListener.instance.y,
            MouseListener.instance.dragging);
        updateCamera = true;
      }

      if (MouseListener.instance.x != MouseListener.instance.prevX && MouseListener.instance.dragging) {
        double xOffset = MouseListener.instance.x - MouseListener.instance.prevX;
        xOffset *= mouseSensitivity;

        updateCamera = true;
        camera.yaw(xOffset);
      }

      if (MouseListener.instance.y != MouseListener.instance.prevY && MouseListener.instance.dragging) {
        double yOffset = MouseListener.instance.y - MouseListener.instance.prevY;
        yOffset *= mouseSensitivity;

        camera.pitch(yOffset);
        updateCamera = true;
      }

      if (updateCamera) {
        logger.info("camera look at {}", camera.getLookAt());
        sceneManager.updateCamera(camera);
      }

      if (MouseListener.instance.scrollY != 0) {
        fovInDegrees -= MouseListener.instance.scrollY;
        if (fovInDegrees < 1.0f)
          fovInDegrees = 1.0f;
        if (fovInDegrees > 45.0f)
          fovInDegrees = 45.0f;
        sceneManager.setFOV(fovInDegrees);
      }

      endFrame(newTime);
    }

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
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the frame buffer and depth buffer

    sceneManager.render();

    renderer.render();
    glfwSwapBuffers(windowHandle); // swap the color buffers

  }

  private void endFrame(double newPrevTime) {
    MouseListener.endFrame();
    prevTime = newPrevTime;
  }

  private void cleanup() {
    logger.debug("clean window");

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
