//  -------------   Object-System  (Matrix M)  -------------------
//                                                              E.Gutknecht, Feb. 2020
package ch.fhnw.util.opengl;

import com.jogamp.opengl.*;
import java.util.*;
import ch.fhnw.util.math.*;                                   // Vektor- und Matrix-Algebra

public class ObjectSystem
{

    //  --------------  Globale Daten  -------------------------------------

    private Mat4 M = Mat4.ID;                                 // ModelMatrix

    private Stack<Mat4> MStack = new Stack<>();              // Matrix-Stack

    // ------ Identifiers fuer OpenGL-Objekte und Shader-Variablen  ------

    private int MId;                                //  Uniform Shader Variables

    //  ------------- Konstruktor  ---------------------------

    public ObjectSystem(GL3 gl, int programId)
    {  setupMatrices(gl, programId);                          // Model-Matrix
    }


    //  ----------  oeffentliche Methoden   -------------

    public void resetPos(GL3 gl)     // Position zuruecksetzen
    { M = Mat4.ID;
      gl.glUniformMatrix4fv(MId, 1, false, M.toArray(), 0);
    }


    public void rotate(GL3 gl, float phi, float x, float y, float z)
    {   M = M.postMultiply(Mat4.rotate(phi,x,y,z));
        gl.glUniformMatrix4fv(MId, 1, false, M.toArray(), 0);
    }


    public void translate(GL3 gl, float x, float y, float z)
    {   M = M.postMultiply(Mat4.translate(x,y,z));
        gl.glUniformMatrix4fv(MId, 1, false, M.toArray(), 0);
    }


    public void scale(GL3 gl, float sx, float sy, float sz)
    {   M = M.postMultiply(Mat4.scale(sx,sy,sz));
        gl.glUniformMatrix4fv(MId, 1, false, M.toArray(), 0);
    }


    public void setM(GL3 gl, Mat4 ModelMatrix)          // Model-Matrix
    {   this.M = ModelMatrix;
        gl.glUniformMatrix4fv(MId, 1, false, M.toArray(), 0);
    }

    public void multM(GL3 gl, Mat4 A)       // M = M * A (postmultiply)
    {   M = M.postMultiply(A);
        gl.glUniformMatrix4fv(MId, 1, false, M.toArray(), 0);
    }

    public void pushM()
    {  MStack.push(M);
    }

    public Mat4 popM(GL3 gl)
    {  M = MStack.pop();
       gl.glUniformMatrix4fv(MId, 1, false, M.toArray(), 0);
       return M;
    }


    //  ---------  Abfrage-Methoden ----------

    public Mat4 getM()                                         // ModelView-Matrix
    {  return M;
    }


    //  -------------  private Methoden  ---------------------------

    private void setupMatrices(GL3 gl, int pgm)
    {
       // ----- get shader variable identifiers  -------------
       MId = gl.glGetUniformLocation(pgm, "M");

       // -----  set uniform variables  -----------------------
       gl.glUniformMatrix4fv(MId, 1, false, Mat4.ID.toArray(), 0);
    };

}