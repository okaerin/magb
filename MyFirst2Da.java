//  ------  JOGL 2D Beispiel-Programm (Rotierendes Dreieck) ------
import java.awt.*;
import java.awt.event.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.*;
import com.jogamp.opengl.util.FPSAnimator;
import ch.fhnw.util.opengl.MyShaders;
import ch.fhnw.util.opengl.VertexArray;
import ch.fhnw.util.opengl.Transform;
import ch.fhnw.util.math.Vec3;
import ch.fhnw.util.math.Mat4;

public class MyFirst2Da
       implements WindowListener, GLEventListener, KeyListener
{

    //  ---------  globale Daten  ---------------------------

    String windowTitle = "JOGL-Application";
    int windowWidth = 600;                      // Window-Groesse
    int windowHeight = 600;
    String vShader = MyShaders.vShader1;        // Vertex-Shader
    String fShader = MyShaders.fShader1;        // Fragment-Shader
    Frame frame;                                // Java-Frame
    GLCanvas canvas;                            // OpenGL Window
    int programId;                              // OpenGL Program-Ident.
    VertexArray vArray;
    Transform transform;
    int maxVerts = 2048;                        // max. Anzahl Vertices im Vertex-Array
    float s = 1.2f;                             // Dreiecksseite
    float h = 0.5f*s*(float)Math.sqrt(3);       // Hoehe
    float phi = 0;                              // Drehwinkel
    float dphi = 1.0f;                          // Zunahme Drehwinkel

    //  ---------  Methoden  -------------------

    public MyFirst2Da()                                   // Konstruktor
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



    public void zeichneDreieck(GL3 gl, VertexArray vArray,
                               float x1, float y1,
                               float x2, float y2,
                               float x3, float y3)
    {  vArray.rewindBuffer(gl);
       vArray.setColor(1,0,0);     // Rot
       vArray.putVertex(x1,y1,0);
       vArray.setColor(0,1,0);     // Gruen
       vArray.putVertex(x2,y2,0);
       vArray.setColor(0,0,1);     // Blau
       vArray.putVertex(x3,y3,0);
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
       gl.glClearColor(0,0,0,1); // Hintergrundfarbe
       // Compile and Link Shaders
       programId = MyShaders.initShaders(gl,vShader,fShader);
       vArray = new VertexArray(gl, programId, maxVerts);
       transform = new Transform(gl, programId);
       FPSAnimator anim = new FPSAnimator(canvas, 60, true);     // Animations-Thread, 60  Frames/sek
       anim.start();
   }


    @Override
    public void display(GLAutoDrawable drawable)
    {  GL3 gl = drawable.getGL().getGL3();
       gl.glClear(GL3.GL_COLOR_BUFFER_BIT); // Frame-Buffer loeschen
       vArray.setColor(0.7f,0.7f,0.7f);
       transform.resetM(gl);                // Objekt-System zuruecksetzen
       vArray.drawAxis(gl,5,5,5);           // Koordinatenachsen zeichnen
       float s2 = 0.5f*s;
       transform.rotateM(gl,phi,0,0,1);     // Objekt-System drehen
       zeichneDreieck(gl,vArray,-s2,-h/3,s2,-h/3,0,2*h/3);
       phi += dphi;
    }


    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y,
                        int width, int height)
    {  GL3 gl = drawable.getGL().getGL3();
       // ------  Set the viewport to the entire window
       gl.glViewport(0, 0, width, height);
    }


    @Override
    public void dispose(GLAutoDrawable drawable)  { }  // not needed


    //  -----------  main-Methode  ---------------------------

    public static void main(String[] args)
    { new MyFirst2Da();
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
       {
         case KeyEvent.VK_UP:    dphi += 0.05f;  // Rotationsgeschwindigkeit
                                 break;
         case KeyEvent.VK_DOWN:  dphi -= 0.05f;
                                 break;
       }
       canvas.repaint();
    }
    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) { }



}