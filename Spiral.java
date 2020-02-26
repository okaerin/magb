//  -------------   Minimales JOGL Programm (Leeres Bild) -------------------

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import ch.fhnw.util.math.Vec3;
import ch.fhnw.util.opengl.MyShaders;
import ch.fhnw.util.opengl.Transform;
import ch.fhnw.util.opengl.VertexArray;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.*;

public class Spiral
        implements WindowListener, GLEventListener, KeyListener {
    //  ---------  globale Daten  ---------------------------
    String windowTitle = "JOGL-Spiral";
    int windowWidth = 800;
    int windowHeight = 600;
    Frame frame;
    GLCanvas canvas;      // OpenGL Window
    VertexArray vArray;
    int maxVerts = 2048;                        // max. Anzahl Vertices im Vertex-Array
    private int programId;
    private Transform transform;
    private int azimut = 0;
    private int elevation = 0;

    //  ---------  Methoden  --------------------------------

    public Spiral()    // Konstruktor
    {
        createFrame();
    }


    void createFrame()     // Fenster erzeugen
    {
        Frame f = new Frame(windowTitle);
        f.setSize(windowWidth, windowHeight);
        f.addWindowListener(this);
        GLProfile glp = GLProfile.get(GLProfile.GL3);
        GLCapabilities glCaps = new GLCapabilities(glp);
        canvas = new GLCanvas(glCaps);
        canvas.addGLEventListener(this);
        f.add(canvas);
        f.setVisible(true);
        f.addKeyListener(this);
        canvas.addKeyListener(this);
    }


    //  ----------  OpenGL-Events   ---------------------------
    @Override
    public void init(GLAutoDrawable drawable)      //  Initialisierung
    {
        GL3 gl = drawable.getGL().getGL3();
        System.out.println("OpenGl Version: " + gl.glGetString(gl.GL_VERSION));
        System.out.println("Shading Language: " + gl.glGetString(gl.GL_SHADING_LANGUAGE_VERSION));
        System.out.println();
        gl.glClearColor(.5f, .5f, .5f, 1);                  // Hintergrundfarbe (RGBA)
        //Compile and link shaders
        programId = MyShaders.initShaders(gl, MyShaders.vShader1, MyShaders.fShader1);
        vArray = new VertexArray(gl, programId, maxVerts);
        transform = new Transform(gl, programId);
    }


    @Override
    public void display(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();

        // -----  Sichtbarkeitstest
        gl.glEnable(GL3.GL_DEPTH_TEST);
        // -----  Color-Buffer loeschen
        gl.glClear(gl.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_CLEAR_VALUE);

        //kamera system
        transform.lookAt2(gl, 8, azimut, elevation);
//        transform.setLightPosition(gl, -1f, 3f, -1f);

        //coordinates
//        transform.resetM(gl);
//        transform.perspective(gl,0f, windowWidth, 0, windowHeight, .01f,100f);
//        transform.translateM(gl, 0, 0, -5);
        vArray.setColor(1, 1, 1);
        vArray.drawAxis(gl, 5, 5, 5);

        //draw helix
//        float t = .0f;
//        float dt = .1f;
//        ArrayList<Vec3> verts = new ArrayList<>();
//        for (int i = 0; i < 35 * 5 * 2; i++, t += dt) {
//            //r(t) = 1− 0.02 · t
//            float rt = 1 - .02f * t;
//            //x(t) = r(t) · sin(t)
//            float xt = (float) (rt * Math.sin(t));
//            //y(t) = 0.03 · t
//            float yt = .03f * t;
//            //z(t) = r(t) · cos(t)
//            float zt = (float) (rt * Math.cos(t));
//
//            verts.add(new Vec3(xt, yt, zt));
//        }
//        vArray.setColor(1, 0, 0);
//        vArray.drawLine(gl, verts.toArray(Vec3[]::new));
    }


    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y,
                        int width, int height) {
        GL3 gl = drawable.getGL().getGL3();
        // Set the viewport to be the entire window
        gl.glViewport(0, 0, width, height);
    }


    @Override
    public void dispose(GLAutoDrawable drawable)   // not needed
    {
    }

    //  -----------  main-Methode  ---------------------------

    public static void main(String[] args) {
        new Spiral();
    }

    //  ---------  Window-Events  --------------------

    public void windowClosing(WindowEvent e) {
        System.out.println("closing window");
        System.exit(0);
    }

    public void windowActivated(WindowEvent e) {
    }

    public void windowClosed(WindowEvent e) {
    }

    public void windowDeactivated(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        switch (code) {
            case KeyEvent.VK_LEFT:
                azimut--;
                break;
            case KeyEvent.VK_RIGHT:
                azimut++;
                break;
            case KeyEvent.VK_UP:
                elevation++;
                break;
            case KeyEvent.VK_DOWN:
                elevation--;
                break;
        }
        System.out.println("elevation: "+elevation+" azimut: "+azimut);
        canvas.repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}