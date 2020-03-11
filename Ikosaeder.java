//  -------------   Minimales JOGL Programm (Leeres Bild) -------------------

import ch.fhnw.util.math.Vec3;
import ch.fhnw.util.opengl.MyShaders;
import ch.fhnw.util.opengl.Transform;
import ch.fhnw.util.opengl.VertexArray;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class Ikosaeder
        implements WindowListener, GLEventListener, KeyListener {
    //  ---------  globale Daten  ---------------------------
    String windowTitle = "JOGL-Cube";
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

    final float X = 0.525731112119133606f;
    final float Z = 0.850650808352039932f;
    final float[][] vdata =
            {
                    {-X, 0.f, Z},
                    {X, 0.f, Z},
                    {-X, 0.f, -Z},
                    {X, 0.f, -Z},
                    {0.f, Z, X},
                    {0.f, Z, -X},
                    {0.f, -Z, X},
                    {0.f, -Z, -X},
                    {Z, X, 0.f},
                    {-Z, X, 0.f},
                    {Z, -X, 0.f},
                    {-Z, -X, 0.f}
            };

    final int[][] tindices =
            {
                    {0, 1, 4},
                    {0, 4, 9},
                    {9, 4, 5},
                    {4, 8, 5},
                    {4, 1, 8},
                    {8, 1, 10},
                    {8, 10, 3},
                    {5, 8, 3},
                    {5, 3, 2},
                    {2, 3, 7},
                    {7, 3, 10},
                    {7, 10, 6},
                    {7, 6, 11},
                    {11, 6, 0},
                    {0, 6, 1},
                    {6, 10, 1},
                    {9, 11, 0},
                    {9, 2, 11},
                    {9, 5, 2},
                    {7, 11, 2}
            };


//  ---------  Methoden  --------------------------------

    public Ikosaeder()   // Konstruktor
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
        transform.setShadingLevel(gl, 0);
        // -----  Kamera-System und Lichtquelle festlegen
        transform.lookAt2(gl, dist, azimut, elevation);
        transform.setLightPosition(gl, -1.f, 2.7f, 10.f);

        // -----  Koordinatenachsen zeichnen
        drawAxisColored(gl, 5, 5, 5);
        //draw ikosaeder
//        vArray.setColor(224 / 255.f, 155 / 255.f, 142 / 255.f);
        draw(gl);
    }

    public void drawAxisColored(GL3 gl, float a, float b, float c)                   // Koordinatenachsen zeichnen
    {
        vArray.rewindBuffer(gl);
        vArray.setColor(1, 0, 0);
        vArray.putVertex(0, 0, 0);           // Eckpunkte in VertexArray speichern
        vArray.putVertex(a, 0, 0);

        vArray.setColor(0, 1, 0);
        vArray.putVertex(0, 0, 0);
        vArray.putVertex(0, b, 0);

        vArray.setColor(0, 0, 1);
        vArray.putVertex(0, 0, 0);
        vArray.putVertex(0, 0, c);
        vArray.copyBuffer(gl);
        vArray.drawArrays(gl, GL3.GL_LINES);
    }

    void draw(GL3 gl) {
        vArray.rewindBuffer(gl);
        transform.setShadingLevel(gl, 1);
        vArray.setColor(224 / 255.f, 155 / 255.f, 142 / 255.f);
        for (int i = 0; i < tindices.length; ++i) {
            float[] vf0 = vdata[tindices[i][0]];
            float[] vf1 = vdata[tindices[i][1]];
            float[] vf2 = vdata[tindices[i][2]];

//            putTriangle(gl, new Vec3(vf0), new Vec3(vf1), new Vec3(vf2));
            //singular subdivide
            subdivide(gl,new Vec3(vf0), new Vec3(vf1), new Vec3(vf2));
        }
        vArray.copyBuffer(gl);
        vArray.drawArrays(gl, gl.GL_TRIANGLES);
    }

    void subdivide(GL3 gl, Vec3 A, Vec3 B, Vec3 C) {
        Vec3 v12 = new Vec3(0, 0, 0), v23 = new Vec3(0, 0, 0), v31 = new Vec3(0, 0, 0);
        v12 = A.add(B).normalize();
        v23 = B.add(C).normalize();
        v31 = C.add(A).normalize();

        putTriangle(gl, A, v12, v31);
        putTriangle(gl, B, v23, v12);
        putTriangle(gl, C, v31, v23);
        putTriangle(gl, v12, v23, v31);
    }

    void putTriangle(GL3 gl, Vec3 A, Vec3 B, Vec3 C) {
        Vec3 BA = A.subtract(B), BC = C.subtract(B);
        Vec3 normal = BC.cross(BA);
        vArray.setNormal(normal.x, normal.y, normal.z);

        vArray.putVertex(A.x, A.y, A.z);
        vArray.putVertex(B.x, B.y, B.z);
        vArray.putVertex(C.x, C.y, C.z);
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
        new Ikosaeder();
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