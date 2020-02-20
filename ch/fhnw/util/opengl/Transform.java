//  ---------   Transformationen (Matrizen M, V, P und Beleuchtungsparam.)  ------------
//                                                              E.Gutknecht, Feb. 2020
package ch.fhnw.util.opengl;

import com.jogamp.opengl.*;
import java.util.*;
import ch.fhnw.util.math.*;                                   // Vektor- und Matrix-Algebra

public class Transform
{

    //  --------------  Globale Daten  -------------------------------------

    // ---- Identifiers der Shader-Variablen
    private int MId, VId, PId;
    private int shadingLevelId, lightPositionId;
    private int ambientId, diffuseId, specularId, specExpId;

    // ---- Transformationsmatrizen
    private Mat4 M = Mat4.ID;                                 // ModelMatrix
    private Mat4 V = Mat4.ID;                                 // ViewMatrix
    private Mat4 P = Mat4.ID;                                 // ProjektionsMatrix

    private Stack<Mat4> MStack = new Stack<>();              // Matrix-Stack

    // -----  Beleuchtungsparameter
    private int shadingLevel = 0;                             // Beleuchtungs-Stufe 0=aus, 1=ambient u. diffus
    private Vec4 lightPosition = new Vec4(0, 0, 10, 1);       // Lichtquelle
    private float ambient = 0.2f;                             // ambientes Licht
    private float diffuse = 0.4f;                             // diffuse Reflexion
    private float specular = 0.4f;                            // diffuse Reflexion
    private float specExp = 20;                               // diffuse Reflexion


    //  ------------- Konstruktor  ---------------------------

    public Transform(GL3 gl, int programId)
    {  setupMatrices(gl, programId);                          // Model-Matrix
       setupLightingParms(gl, programId);               // Beleuchtungsparameter
    }


    // ------  Methoden fuer Matrix M (Objekt-System)  ------

    public void resetM(GL3 gl)     // Position zuruecksetzen
    { M = Mat4.ID;
      gl.glUniformMatrix4fv(MId, 1, false, M.toArray(), 0);
    }

    public void rotateM(GL3 gl, float phi, float x, float y, float z)
    {   M = M.postMultiply(Mat4.rotate(phi,x,y,z));
        gl.glUniformMatrix4fv(MId, 1, false, M.toArray(), 0);
    }

    public void translateM(GL3 gl, float x, float y, float z)
    {   M = M.postMultiply(Mat4.translate(x,y,z));
        gl.glUniformMatrix4fv(MId, 1, false, M.toArray(), 0);
    }

    public void scaleM(GL3 gl, float sx, float sy, float sz)
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

    public Mat4 getM()
    {  return M;
    }


    // ------ Methoden  fuer Matrizen V, P (Kamera-System) ------

    public void resetV(GL3 gl)        // Position zurucksetzen
    { V = Mat4.ID;
      gl.glUniformMatrix4fv(VId, 1, false, V.toArray(), 0);
    }

    // ------  View-Matrix fuer LookAt-Parameter
    public void lookAt(GL3 gl,Vec3 eye, Vec3 target, Vec3 up)
    {   V = Mat4.lookAt(eye,target,up);
        gl.glUniformMatrix4fv(VId, 1, false, V.toArray(), 0);
    }

    // ------  View-Matrix fuer Azimut-Elevation-Parameter
    public void lookAt2(GL3 gl, float dist, float azimut, float elevation)
    {   V = Mat4.lookAt2(dist,azimut,elevation);
        gl.glUniformMatrix4fv(VId, 1, false, V.toArray(), 0);
    }

   // ------  Projektionsmatrix fuer Orthogonal-Projektion
   public void ortho(GL3 gl, float xleft, float xright,
                     float ybottom, float ytop, float znear, float zfar)
   {  P = Mat4.ortho(xleft, xright, ybottom, ytop, znear, zfar);
      gl.glUniformMatrix4fv(PId, 1, false, P.toArray(), 0);
   }

   // ------  Projektionsmatrix fuer Zentralprojektion
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

    public Mat4 getV()              // ModelView-Matrix
    {  return V;
    }

    public Mat4 getP()             // Projektions-Matrix
    {  return P;
    }


    //  ----------  oeffentliche Methoden Beleuchtung  -------------

    public void setLightPosition(GL3 gl, float x, float y, float z)
    { Vec4 pos = new Vec4(x,y,z,1);
      Vec4 worldCoord =  M.transform(pos);  // Transformation in absolutes System
      Vec4 eyeCoord = V.transform(worldCoord);  // Transformation in Kamera-System
      lightPosition =  eyeCoord;
      gl.glUniform4fv(lightPositionId, 1, eyeCoord.toArray(),0);
    }


    public void setShadingLevel(GL3 gl, int level)    // 0: ohne Beleuchtung
    {   gl.glUniform1i(shadingLevelId, level);
    }

    // ------  Koeffizienten ambientes Licht und diffuse Reflexion
    public void setShadingParam(GL3 gl, float ambient,
                                float diffuse)
    {   this.ambient = ambient;
        this.diffuse = diffuse;
        gl.glUniform1f(ambientId, ambient);
        gl.glUniform1f(diffuseId, diffuse);
    }


    // ------  Koeffizienten spiegelnde Reflexion
    public void setShadingParam2(GL3 gl, float specular, float specExp)  // spiegelnde Reflexion
    {   this.specular = specular;
        this.specExp = specExp;
        gl.glUniform1f(specularId, specular);
        gl.glUniform1f(specExpId, specExp);
    }


    //  -------------  private Methoden  ---------------------------

    private void setupMatrices(GL3 gl, int pgm)
    {
       // ----- get shader variable identifiers  -------------
       MId = gl.glGetUniformLocation(pgm, "M");
       VId = gl.glGetUniformLocation(pgm, "V");
       PId = gl.glGetUniformLocation(pgm, "P");

       // -----  set uniform variables  -----------------------
       gl.glUniformMatrix4fv(MId, 1, false, Mat4.ID.toArray(), 0);
       gl.glUniformMatrix4fv(VId, 1, false, Mat4.ID.toArray(), 0);
       gl.glUniformMatrix4fv(PId, 1, false, Mat4.ID.toArray(), 0);
    };

    private void setupLightingParms(GL3 gl, int pgm)
    {
       // ----- get shader variable identifiers  -------------
       shadingLevelId = gl.glGetUniformLocation(pgm, "shadingLevel");
       lightPositionId = gl.glGetUniformLocation(pgm, "lightPosition");
       ambientId =  gl.glGetUniformLocation(pgm, "ambient");
       diffuseId =  gl.glGetUniformLocation(pgm, "diffuse");
       specularId =  gl.glGetUniformLocation(pgm, "specular");
       specExpId =  gl.glGetUniformLocation(pgm, "specExp");

       // -----  set uniform variables  -----------------------
       gl.glUniform1i(shadingLevelId, shadingLevel);
       gl.glUniform1f(ambientId, ambient);
       gl.glUniform1f(diffuseId, diffuse);
       gl.glUniform1f(specularId, specular);
       gl.glUniform1f(specExpId, specExp);
       gl.glUniformMatrix4fv(lightPositionId,
                1, false, lightPosition.toArray(), 0);
    }

}