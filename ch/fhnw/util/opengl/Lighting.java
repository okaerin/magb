//  -------------   Lichtquelle und Beleuchtungsparameter  ----------------
//                                                              E.Gutknecht, Feb. 2020
package ch.fhnw.util.opengl;

import com.jogamp.opengl.*;
import ch.fhnw.util.math.*;                                   // Vektor- und Matrix-Algebra

public class Lighting
{
    //  --------------  Globale Daten  -------------------------------------

    private int shadingLevel = 0;                             // Beleuchtungs-Stufe 0=aus, 1=ambient u. diffus
    private Vec4 lightPosition = new Vec4(0, 0, 10, 1);       // Lichtquelle
    private float ambient = 0.2f;                             // ambientes Licht
    private float diffuse = 0.4f;                             // diffuse Reflexion
    private float specular = 0.4f;                            // diffuse Reflexion
    private float specExp = 20;                               // diffuse Reflexion

    // ------ Identifiers fuer OpenGL-Objekte und Shader-Variablen  ------

    private int shadingLevelId, lightPositionId;              // Uniform Shader Variables
    private int ambientId, diffuseId, specularId, specExpId;  // Uniform Shader Variables


    //  ------------- Konstruktor  ---------------------------

    public Lighting(GL3 gl, int programId)              // Program-Identifier
    {
       setupLightingParms(gl, programId);               // Beleuchtungsparameter
    }


    //  ----------  oeffentliche Methoden   -------------

    public void setLightPosition(GL3 gl, Mat4 M, float x, float y, float z)
    { Vec4 pos = new Vec4(x,y,z,1);
      lightPosition =  M.transform(pos);  // Transformation in Kamera-System
      gl.glUniform4fv(lightPositionId, 1, lightPosition.toArray(),0);
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


    //  ---------  Abfrage-Methoden ----------

    public int getShadingLevel()
    {  return shadingLevel;
    }

    public float getAmbient()
    {  return ambient;
    }

    public float getDiffuse()
    {  return diffuse;
    }

    public Vec4 getLightPosition()
    {  return lightPosition;
    }

    public float[] getShadingParam()
    {  float[] param = { ambient, diffuse };
       return param;
    }


    public float[] getShadingParam2()
    {  float[] param = { specular, specExp };
       return param;
    }


    //  ------  private Methoden  ---------------

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