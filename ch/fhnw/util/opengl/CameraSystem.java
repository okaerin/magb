//  ------------- Kamera-System (Matrizen V und P)  -------------------
//                                                              E.Gutknecht, Feb. 2020
package ch.fhnw.util.opengl;

import com.jogamp.opengl.*;
//import java.util.*;
import ch.fhnw.util.math.*;                                   // Vektor- und Matrix-Algebra

public class CameraSystem
{
    //  --------------  Globale Daten  -------------------------------------

    private Mat4 V = Mat4.ID;                                 // ViewMatrix
    private Mat4 P = Mat4.ID;                                 // ProjektionsMatrix

    // ------ Identifiers fuer OpenGL-Objekte und Shader-Variablen  ------

    private int VId, PId;                                //  Uniform Shader Variables


    //  ------------- Konstruktor  ---------------------------

    public CameraSystem(GL3 gl, int programId)           // Program-Identifier
    {
       setupMatrices(gl, programId);                     // View- und Projektions-Matrix
    }


    //  ----------  oeffentliche Methoden   -------------

   public void resetPos(GL3 gl)             // Position zuruecksetzen
   { V = Mat4.ID;
     gl.glUniformMatrix4fv(VId, 1, false, V.toArray(), 0);
   }


   // ------  Kamera-System positionieren
   public void lookAt(GL3 gl,Vec3 eye, Vec3 target, Vec3 up)
   {   V = Mat4.lookAt(eye,target,up);
       gl.glUniformMatrix4fv(VId, 1, false, V.toArray(), 0);
   }

   public void lookAt2(GL3 gl, float dist, float azimut, float elevation)
   {   V = Mat4.lookAt2(dist,azimut,elevation);
       gl.glUniformMatrix4fv(VId, 1, false, V.toArray(), 0);
   }

   // ------  Orthogonal-Projektion
   public void ortho(GL3 gl, float xleft, float xright,
                     float ybottom, float ytop, float znear, float zfar)
   {  P = Mat4.ortho(xleft, xright, ybottom, ytop, znear, zfar);
      gl.glUniformMatrix4fv(PId, 1, false, P.toArray(), 0);
   }

   // ------  Zentralprojektion
   public void perspective(GL3 gl, float xleft, float xright,
                     float ybottom, float ytop, float znear, float zfar)
   {  P = Mat4.perspective(xleft, xright, ybottom, ytop, znear, zfar);
      gl.glUniformMatrix4fv(PId, 1, false, P.toArray(), 0);
   }


    public void setV(GL3 gl, Mat4 ViewMatrix)          //  View-Matrix
    {   this.V = ViewMatrix;
        gl.glUniformMatrix4fv(VId, 1, false, V.toArray(), 0);
    }

    public void setP(GL3 gl, Mat4 ProjectionMatrix)   // Projektions-Matrix
    {   this.P = ProjectionMatrix;
        gl.glUniformMatrix4fv(PId, 1, false, P.toArray(), 0);
    }


    //  ---------  Abfrage-Methoden ----------

    public Mat4 getV()              // ModelView-Matrix
    {  return V;
    }

    public Mat4 getP()             // Projektions-Matrix
    {  return P;
    }

    //  -------------  private Methoden  ---------------------------

    private void setupMatrices(GL3 gl, int pgm)
    {
       // ----- get shader variable identifiers  -------------
       VId = gl.glGetUniformLocation(pgm, "V");
       PId = gl.glGetUniformLocation(pgm, "P");

       // -----  set uniform variables  -----------------------
       gl.glUniformMatrix4fv(VId, 1, false, Mat4.ID.toArray(), 0);
       gl.glUniformMatrix4fv(PId, 1, false, Mat4.ID.toArray(), 0);
    }
}