//  -------------   Minimales JOGL Programm (Leeres Bild) -------------------
import java.awt.*;
import java.awt.event.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.*;

public class MyFirstGL
       implements WindowListener, GLEventListener
{
    //  ---------  globale Daten  ---------------------------
    String windowTitle = "JOGL-Application";
    int windowWidth = 800;
    int windowHeight = 600;
    Frame frame;
    GLCanvas canvas;      // OpenGL Window

 //  ---------  Methoden  --------------------------------

    public MyFirstGL()    // Konstruktor
    { createFrame();
    }


    void createFrame()     // Fenster erzeugen
    {  Frame f = new Frame(windowTitle);
       f.setSize(windowWidth, windowHeight);
       f.addWindowListener(this);
       GLProfile glp = GLProfile.get(GLProfile.GL3);
       GLCapabilities glCaps = new GLCapabilities(glp);
       canvas = new GLCanvas(glCaps);
       canvas.addGLEventListener(this);
       f.add(canvas);
       f.setVisible(true);
    }


    //  ----------  OpenGL-Events   ---------------------------
    @Override
    public void init(GLAutoDrawable drawable)      //  Initialisierung
    {  GL3 gl = drawable.getGL().getGL3();
       System.out.println("OpenGl Version: " + gl.glGetString(gl.GL_VERSION));
       System.out.println("Shading Language: " + gl.glGetString(gl.GL_SHADING_LANGUAGE_VERSION));
       System.out.println();
       gl.glClearColor(0,0,1,1);                  // Hintergrundfarbe (RGBA)
    }


    @Override
    public void display(GLAutoDrawable drawable)
    {  GL3 gl = drawable.getGL().getGL3();
       // -----  Color-Buffer loeschen
       gl.glClear(gl.GL_COLOR_BUFFER_BIT);
    }


    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y,
                        int width, int height)
    {  GL3 gl = drawable.getGL().getGL3();
       // Set the viewport to be the entire window
       gl.glViewport(0, 0, width, height);
    }


    @Override
    public void dispose(GLAutoDrawable drawable)   // not needed
    { }

    //  -----------  main-Methode  ---------------------------

    public static void main(String[] args)
    { new MyFirstGL();
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

}