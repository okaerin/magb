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

public class Cube
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
    private float a;

    //  ---------  Methoden  --------------------------------

    public Cube(float a)   // Konstruktor
    {
        this.vArray = vArray;
        this.a = a;
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
        drawAxisColored(gl, 5, 5, 5);
        //draw cube
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
        //top verts
        Vec3 A, B, C, D;
        float a = .5f;
        A = new Vec3(-a, a, -a);
        B = new Vec3(-a, a, a);
        C = new Vec3(a, a, a);
        D = new Vec3(a, a, -a);

        //bottom verts
        Vec3 A_prime, B_prime, C_prime, D_prime;
        A_prime = new Vec3(-a, -a, -a);
        B_prime = new Vec3(-a, -a, a);
        C_prime = new Vec3(a, -a, a);
        D_prime = new Vec3(a, -a, -a);


        //top
        vArray.setColor(0, 1, 0);
        putQuad(gl, vArray, A, B, C, D);
        //left
        vArray.setColor(1, 0, 0);
        putQuad(gl, vArray, A, A_prime, B_prime, B);
        //right
        vArray.setColor(1, 0, 0);
        putQuad(gl, vArray, C, C_prime, D_prime, D);
        //bottom
        vArray.setColor(0, 1, 0);
        putQuad(gl, vArray, A_prime, B_prime, C_prime, D_prime);
        //back
        vArray.setColor(0, 0, 1);
        putQuad(gl, vArray, D, D_prime, A_prime, A);
        //front
        vArray.setColor(0, 0, 1);
        putQuad(gl, vArray, B, B_prime, C_prime, C);

        vArray.copyBuffer(gl);
        vArray.drawArrays(gl, gl.GL_TRIANGLES);
    }

    void putQuad(GL3 gl, VertexArray vArray,
                 Vec3 A, Vec3 B, Vec3 C, Vec3 D) {
        Vec3 BA = A.subtract(B), BC = C.subtract(B);
        Vec3 normal = BC.cross(BA);//BC x BA

        vArray.setNormal(normal.x, normal.y, normal.z);
        vArray.putVertex(A.x, A.y, A.z);
        vArray.putVertex(B.x, B.y, B.z);
        vArray.putVertex(C.x, C.y, C.z);

        vArray.putVertex(C.x, C.y, C.z);
        vArray.putVertex(D.x, D.y, D.z);
        vArray.putVertex(A.x, A.y, A.z);
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
        new Cube(2);
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