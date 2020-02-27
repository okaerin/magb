//  -------------   Minimales JOGL Programm (Leeres Bild) -------------------

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;

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
    private int azimut = 45;
    private int elevation = 30;
    float xleft = -3f, xright = 3f;//viewing volume
    private float ybottom, ytop;
    private float znear = -.1f, zfar = 100;
    private float dist = 8f;

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
        //not potato
        GL3 gl = drawable.getGL().getGL3();

        // -----  Sichtbarkeitstest
        gl.glEnable(GL3.GL_DEPTH_TEST);

        // -----  Color- und Depth-Buffer loeschen
        gl.glClear(gl.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);

        // -----  Kamera-System und Lichtquelle festlegen
        transform.lookAt2(gl, dist, azimut, elevation);
        transform.setLightPosition(gl, -1.0f, 2.7f, -1.0f);

        // -----  Koordinatenachsen zeichnen
        vArray.setColor(.8f, .8f, .8f);
        vArray.drawAxis(gl, 5, 5, 5);
        //draw helix
        float t = .0f;
        float dt = .1f;
        ArrayList<Vec3> verts = new ArrayList<>();
        for (int i = 0; i < 35 * 5 * 2; i++, t += dt) {
            //r(t) = 1− 0.02 · t
            float rt = 1 - .02f * t;
            //x(t) = r(t) · sin(t)
            float xt = (float) (rt * Math.sin(t));
            //y(t) = 0.03 · t
            float yt = .03f * t;
            //z(t) = r(t) · cos(t)
            float zt = (float) (rt * Math.cos(t));

            verts.add(new Vec3(xt, yt, zt));
        }
        vArray.setColor(1, 0, 0);
        drawLine(gl, verts.toArray(Vec3[]::new));
    }
    public void drawLine(GL3 gl, Vec3[] arr){
        if(arr==null||arr.length<1)throw new IllegalArgumentException();
        vArray.rewindBuffer(gl);
        Arrays.stream(arr)
                .forEach(vert-> vArray.putVertex(vert.x, vert.y, vert.z));
        vArray.copyBuffer(gl);
        vArray.drawArrays(gl, GL3.GL_LINE_STRIP);
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y,
                        int width, int height) {
        GL3 gl = drawable.getGL().getGL3();
        // ------  Set the viewport to the entire window
        gl.glViewport(0, 0, width, height);
        float aspect = (float) height / width;
        ybottom = aspect * xleft;
        ytop = aspect * xright;
        // ------ Projektionsmatrix (Orthogonalprojektion)
        transform.ortho(gl, xleft, xright, ybottom, ytop, znear, zfar);
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
        System.out.println("elevation: " + elevation + " azimut: " + azimut);
        canvas.repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}