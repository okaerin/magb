//  -------------   JOGL 3D Beispiel-Programm (Lichtstrahl durch Dreieck) -------------------
import java.awt.*;
import java.awt.event.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.*;
import com.jogamp.opengl.util.*;

import ch.fhnw.util.opengl.MyShaders;
import ch.fhnw.util.opengl.VertexArray;
import ch.fhnw.util.opengl.Transform;
import ch.fhnw.util.math.Vec3;
import ch.fhnw.util.math.Mat4;

public class MyFirst3D
       implements WindowListener, GLEventListener, KeyListener
{

    //  ---------  globale Daten  ---------------------------

    String windowTitle = "JOGL-Application";
    int windowWidth = 800;
    int windowHeight = 600;
    String vShader = MyShaders.vShader1;        // Vertex-Shader
    String fShader = MyShaders.fShader1;        // Fragment-Shader
    Frame frame;
    GLCanvas canvas;                            // OpenGL Window
    int programId;                              // OpenGL-Id
    VertexArray vArray;
    Transform transform;
    int maxVerts = 2048;                        // max. Anzahl Vertices im Vertex-Array
    float elevation = 10;  // Betrachtungswinkel (Kamera)
    float azimut = 30;
    float dist = 8;        // Abstand Kamera vom Ursprung

    float xleft=-3.0f, xright=3.0f;  // Viewing-Volume
    float ybottom, ytop;
    float znear=-10, zfar=100;

    //  ---------  Methoden  --------------------------------

    public MyFirst3D()                                   // Konstruktor
    { createFrame();
    }


    void createFrame()                                    // Fenster erzeugen
    {  Frame f = new Frame(windowTitle);
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


    public void zeichneStrecke(GL3 gl, VertexArray va,
                               Vec3 A, Vec3 B, Vec3 color)
    {  vArray.rewindBuffer(gl);
       vArray.setColor(color.x,color.y,color.z); // Farbe setzen
       vArray.putVertex(A.x,A.y,A.z);
       vArray.putVertex(B.x,B.y,B.z);
       vArray.copyBuffer(gl);
       vArray.drawArrays(gl, GL3.GL_LINES);
    }

    public void zeichneDreieck(GL3 gl, VertexArray va,
                               Vec3 A, Vec3 B, Vec3 C, Vec3 color)
    {  vArray.rewindBuffer(gl);         // Vertex-Array leeren
       vArray.setColor(color.x,color.y,color.z); // Farbe setzen
       Vec3 u = B.subtract(A);
       Vec3 v = C.subtract(A);
       Vec3 n = u.cross(v);           // Dreiecks-Normale
       vArray.setNormal(n.x,n.y,n.z);
       vArray.putVertex(A.x,A.y,A.z);
       vArray.putVertex(B.x,B.y,B.z);
       vArray.putVertex(C.x,C.y,C.z);
       vArray.copyBuffer(gl);
       vArray.drawArrays(gl, gl.GL_TRIANGLES);
    }


    //  ----------  OpenGL-Events   ---------------------------

    @Override
    public void init(GLAutoDrawable drawable)             //  Initialisierung
    {  GL3 gl = drawable.getGL().getGL3();
       System.out.println("OpenGl Version: " + gl.glGetString(gl.GL_VERSION));
       System.out.println("Shading Language: " + gl.glGetString(gl.GL_SHADING_LANGUAGE_VERSION));
       System.out.println();
       gl.glClearColor(0,0,1,1); // Hintergrundfarbe
       // Compile/Link Shaders
       programId = MyShaders.initShaders(gl,vShader,fShader);
       vArray = new VertexArray(gl, programId, maxVerts);
       transform = new Transform(gl, programId);
    }


    @Override
    public void display(GLAutoDrawable drawable)
    {  GL3 gl = drawable.getGL().getGL3();

       // -----  Sichtbarkeitstest
       gl.glEnable(GL3.GL_DEPTH_TEST);

       // -----  Color- und Depth-Buffer loeschen
       gl.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);

       // -----  Kamera-System und Lichtquelle festlegen
       transform.lookAt2(gl,dist,azimut, elevation);
       transform.setLightPosition(gl,-1.0f,2.7f,-1.0f);

       // -----  Koordinatenachsen zeichnen
       vArray.setColor(0.7f,0.7f,0.7f);
       vArray.drawAxis(gl,5,5,5);

       // -----  Figuren zeichnen
       Vec3 A = new Vec3(0,0.3f,0.3f);  // Eckpunkte Dreieck
       Vec3 B = new Vec3(2.5f,0.8f,1);
       Vec3 C = new Vec3(0.5f,1.5f,-1);
       transform.setShadingLevel(gl,1);     // Beleuchtung aktivieren
       Vec3 color = new Vec3(0.8f,0.8f,0.8f);
       zeichneDreieck(gl,vArray,A,B,C,color);
       transform.setShadingLevel(gl,0);     // ohne Beleuchtung
       color = new Vec3(1,0,0);
       zeichneStrecke(gl,vArray,new Vec3(-1,3,5),
                      new Vec3(2,-0.5f,-4),color);
    }


    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y,
                        int width, int height)
    {  GL3 gl = drawable.getGL().getGL3();
       // ------  Set the viewport to the entire window
       gl.glViewport(0, 0, width, height);
       float aspect = (float)height/width;
       ybottom=aspect*xleft;
       ytop=aspect*xright;
       // ------ Projektionsmatrix (Orthogonalprojektion)
       transform.ortho(gl,xleft,xright,ybottom,ytop,znear,zfar);
    }


    @Override
    public void dispose(GLAutoDrawable drawable)  { }  // not needed


    //  -----------  main-Methode  ---------------------------

    public static void main(String[] args)
    { new MyFirst3D();
    }

    //  ---------  Window-Events  --------------------

    public void windowClosing(WindowEvent e)
    {   System.out.println("closing window");
        System.exit(0);
    }
    public void windowActivated(WindowEvent e) {  }
    public void windowClosed(WindowEvent e) {  }
    public void windowDeactivated(WindowEvent e) {  }
    public void windowDeiconified(WindowEvent e) {  }
    public void windowIconified(WindowEvent e) {  }
    public void windowOpened(WindowEvent e) {  }


    //  ---------  Keyboard-Events  --------------------

    public void keyPressed(KeyEvent e)
    {  int code = e.getKeyCode();
       switch (code)
       { case KeyEvent.VK_LEFT:  azimut--;
                                 break;
         case KeyEvent.VK_RIGHT: azimut++;
                                 break;
         case KeyEvent.VK_UP:    elevation++;
                                 break;
         case KeyEvent.VK_DOWN:  elevation--;
                                 break;
       }
       canvas.repaint();
    }
    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) { }

}