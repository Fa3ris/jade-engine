package org.jade;


import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
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
import org.jade.render.Cube;
import org.jade.render.GradientTriangle;
import org.jade.render.LayingTile;
import org.jade.render.SingleTriangle;
import org.jade.render.TexturedQuad;
import org.jade.render.Triangles;
import org.jade.render.UpdatingTriangles;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
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
  private SingleTriangle singleTriangle;

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
    singleTriangle = new SingleTriangle();

    float[] vertices = {
        -.5f, -.5f, 0f, // left
        0f, -.5f, 0f, // middle
        -.25f, .5f, 0f, // top-left
        .5f, -.5f, 0f, // right
        .25f, .5f, 0f, // top-right
    };

    int[] indices = {
        0, 1, 2,
        1, 3, 4
    };

    twoTriangles = new Triangles(vertices, indices);

    int[] tri1Ind  = {
        0, 1, 2,
    };

    triangle1 = new Triangles(vertices, tri1Ind);

    int[] tri2Ind  = {
        1, 3, 4
    };

    triangle2 = new Triangles(vertices, tri2Ind);

    yellowTriangle = new Triangles(vertices, tri2Ind,
        "shaders/triangle/vertexShader.glsl",
        "shaders/triangle/fragmentShader-yellow.glsl");

    linkedTriangle = new Triangles(vertices, tri2Ind,
        "shaders/link/vertexShader.glsl",
        "shaders/link/fragmentShader.glsl");

    updatingTriangles = new UpdatingTriangles(vertices, tri1Ind,
        "shaders/uniform/vertexShader.glsl",
        "shaders/uniform/fragmentShader.glsl");

    float coloredVertices[] = {
        // positions         // colors
        0.5f, -0.5f, 0.0f,  1.0f, 0.0f, 0.0f,   // bottom right
        -0.5f, -0.5f, 0.0f,  0.0f, 1.0f, 0.0f,   // bottom left
        0.0f,  0.5f, 0.0f,  0.0f, 0.0f, 1.0f    // top
    };

    coloredTriangle = new Triangles(coloredVertices, tri1Ind,
        "shaders/color/vertexShader.glsl",
        "shaders/color/fragmentShader.glsl");

    coloredTriangle.configVertexAttribute(0, 3, 6*Float.BYTES, 0);
    coloredTriangle.configVertexAttribute(1, 3, 6*Float.BYTES, 3*Float.BYTES);

    texturedQuad = new TexturedQuad();

    Vector4f vector4f = new Vector4f(1f, 0f, 0f, 1f);
    // w is the homogeneous coordinate
    logger.info("vector before transform {}", vector4f);

    Matrix4f matrix4f = new Matrix4f().identity().translate(new Vector3f(1f, 1f, 0f));

    vector4f = matrix4f.transform(vector4f);

    logger.info("vector after transform {}", vector4f);

    translateThenRotate = new Matrix4f();

    scaleThenRotate = new Matrix4f().identity() // transformations are applied in reverse order
        // the matrix operation on the vector is v' = M*v
        .rotate((float) Math.toRadians(90.0f), new Vector3f(0f, 0f, 1f)) // rotate around z-axis
        .scale(new Vector3f(.5f, .5f, .5f));

    circularRotation = new Matrix4f();

    layingTile = new LayingTile();

    cube = new Cube();

    cameraPosition = new Vector3f(0f, 0f, 3f);

    worldOrigin = new Vector3f(0f, 0f, 0f);

    // The name direction vector is not the best chosen name,
    // since it is actually pointing in the reverse direction of what it is targeting.
    cameraDirection = new Vector3f(cameraPosition).sub(worldOrigin).normalize();

    yaw = -90d;

    cameraDirection.x = (float) (Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
    cameraDirection.y = (float) (Math.sin(Math.toRadians(pitch)));
    cameraDirection.z = (float) (Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
    cameraDirection.normalize();

    logger.info("camera pointing at {}", cameraDirection);
    worldUp = new Vector3f(0f, 1f, 0f); // up in the world space
    cameraRight = new Vector3f(worldUp).cross(cameraDirection).normalize(); // n.b.: direction points from origin to the camera

    cameraUp = new Vector3f(cameraDirection).cross(cameraRight).normalize();

    lookAt = new Matrix4f().setLookAt(cameraPosition, worldOrigin, worldUp);

    cube.setView(lookAt);
  }

  private Vector3f worldOrigin;

  private Vector3f worldUp;

  private Triangles twoTriangles;
  private Triangles triangle1;
  private Triangles triangle2;
  private Triangles yellowTriangle;

  private Triangles linkedTriangle;
  private UpdatingTriangles updatingTriangles;
  private Triangles coloredTriangle;

  private TexturedQuad texturedQuad;

  private Matrix4f translateThenRotate;
  private Matrix4f circularRotation;
  private Matrix4f scaleThenRotate;

  private double yaw, pitch;

  private final Vector3f cameraFront = new Vector3f(0f, 0f, -1f); // fixed direction - look forward


  private LayingTile layingTile;

  private Cube cube;
  private Vector3f[] cubePositions = {
      new Vector3f( 0.0f,  0.0f,  0.0f),
  new Vector3f( 2.0f,  5.0f, -15.0f),
  new Vector3f(-1.5f, -2.2f, -2.5f),
  new Vector3f(-3.8f, -2.0f, -12.3f),
  new Vector3f( 2.4f, -0.4f, -3.5f),
  new Vector3f(-1.7f,  3.0f, -7.5f),
  new Vector3f( 1.3f, -2.0f, -2.5f),
  new Vector3f( 1.5f,  2.0f, -2.5f),
  new Vector3f( 1.5f,  0.2f, -1.5f),
  new Vector3f(-1.3f,  1.0f, -1.5f)
};

  private Vector3f cameraPosition;
  private Vector3f cameraDirection;
  private Vector3f cameraRight;
  private Vector3f cameraUp;
  private Matrix4f lookAt;

  private final Vector3f cameraTarget = new Vector3f();

  float fov = 45f;

  private final Vector3f tempVec3 = new Vector3f();

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
        updatingTriangles.update(step);

        translateThenRotate.identity()
            .rotate((float) glfwGetTime(), new Vector3f(0f, 0f, 1f))
            .translate(new Vector3f(.5f, -.5f, 0f));

        circularRotation.identity()
            .translate(new Vector3f(
                .5f * (float) Math.cos(glfwGetTime()),
                .5f * (float) Math.sin(glfwGetTime()),
                0f));

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
        accumulator -= step;
      }

      prevTime = newTime;

      glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the frame buffer and depth buffer

      render();

      glfwSwapBuffers(windowHandle); // swap the color buffers

      if (KeyListener.isKeyPressed(GLFW_KEY_SPACE)) {
        logger.info("space is pressed x: {}, y: {}", MouseListener.instance.x, MouseListener.instance.y);
      }


       float cameraSpeed = 2.5f * (float) step; // or can use elapsed = delta time

      if (KeyListener.isKeyPressed(GLFW_KEY_W)) { // camera forward
        logger.info("w pressed at {}", cameraSpeed);
        cameraPosition.add(tempVec3.set(cameraDirection).mul(cameraSpeed));
      }

      if (KeyListener.isKeyPressed(GLFW_KEY_S)) { // camera backward
        logger.info("s pressed at {}", cameraSpeed);
        cameraPosition.sub(tempVec3.set(cameraDirection).mul(cameraSpeed));

      }

      if (KeyListener.isKeyPressed(GLFW_KEY_A)) { // camera strafe left
        logger.info("a pressed at {}", cameraSpeed);
        cameraPosition.sub(tempVec3.set(cameraDirection).cross(cameraUp).mul(cameraSpeed));
      }

      if (KeyListener.isKeyPressed(GLFW_KEY_D)) { // camera strafe right
        logger.info("d pressed at {}", cameraSpeed);
        cameraPosition.add(tempVec3.set(cameraDirection).cross(cameraUp).mul(cameraSpeed));

      }
      if (MouseListener.isButtonPressed(GLFW_MOUSE_BUTTON_1)) {
        logger.info("mouse button {} is pressed at x: {}, y: {}, dragging: {}",
            GLFW_MOUSE_BUTTON_1,
            MouseListener.instance.x, MouseListener.instance.y,
            MouseListener.instance.dragging);
      }

      boolean updateCamera = false;
      double sensitivity = .1d;
      if (MouseListener.instance.x != MouseListener.instance.prevX && MouseListener.instance.dragging) {
        double xOffset = MouseListener.instance.x - MouseListener.instance.prevX;
        xOffset *= sensitivity;

        yaw += xOffset;
        updateCamera = true;
      }

      if (MouseListener.instance.y != MouseListener.instance.prevY && MouseListener.instance.dragging) {
        double yOffset = MouseListener.instance.y - MouseListener.instance.prevY;
        yOffset *= sensitivity;
        pitch -= yOffset;

        if (pitch > 89.0f)
          pitch =  89.0f;
        if (pitch < -89.0f)
          pitch = -89.0f;

        updateCamera = true;
      }


      if (updateCamera) {
        cameraDirection.x = (float) (Math.cos(Math.toRadians(yaw)) * Math
            .cos(Math.toRadians(pitch)));
        cameraDirection.y = (float) (Math.sin(Math.toRadians(pitch)));
        cameraDirection.z = (float) (Math.sin(Math.toRadians(yaw)) * Math
            .cos(Math.toRadians(pitch)));
        cameraDirection.normalize();
      }

      if (MouseListener.instance.scrollY != 0) {
          fov -= MouseListener.instance.scrollY;

        if (fov < 1.0f)
          fov = 1.0f;
        if (fov > 45.0f)
          fov = 45.0f;

        cube.setFOV(fov);
      }
      endFrame();
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

    if (false) {
      gradientTriangle.render();
      singleTriangle.render();
    }
    if (false) {
      twoTriangles.render();
    } else if (false) {
      linkedTriangle.render();
    } else if (false) {
      triangle1.render();
      if (true) {
        yellowTriangle.render();
      } else {
        triangle2.render();
      }
    } else if (false){
      updatingTriangles.render();
    } else if (false) {
      coloredTriangle.render();
    } else if (false) {

      texturedQuad.applyTransform(translateThenRotate);
      texturedQuad.render();

      texturedQuad.applyTransform(circularRotation);
      texturedQuad.render();

      texturedQuad.applyTransform(scaleThenRotate);
      texturedQuad.render();
    } else if (false) {
      layingTile.render();
      updatingTriangles.render();
    } else {
      if (false) {
        // circle around Y-axis
        float radius = 20f;
        cameraPosition.x = (float) Math.sin(glfwGetTime()) * radius - 10;
        cameraPosition.z = (float) Math.cos(glfwGetTime()) * radius;
      }
      lookAt.setLookAt(
          cameraPosition,
              cameraTarget.set(cameraPosition).add(cameraDirection),
              cameraUp);
      cube.setView(lookAt);
      for (int i = 0; i < cubePositions.length; i++) {
        cube.setTranslation(cubePositions[i]);
        cube.setRotationOffset(20 * i);
        cube.render();
      }
    }
  }

  private void endFrame() {
    MouseListener.endFrame();
  }

  private void cleanup() {
    logger.debug("clean window");

    gradientTriangle.clean();
    singleTriangle.clean();
    twoTriangles.clean();

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
