//  -------------   Vertex-Array -------------------
//                                                              E.Gutknecht, Feb. 2020
package ch.fhnw.util.opengl;

import com.jogamp.opengl.*;
import com.jogamp.common.nio.*;
import java.nio.*;

public class VertexArray
{
    //  --------------  Globale Daten  -------------------------------------
    private int maxVerts;                                     // max. Anzahl Vertices im Vertex-Array
    private int nVertices = 0;                                // momentane Anzahl Vertices

    // ------ Identifiers fuer OpenGL-Objekte und Shader-Variablen  ------

    private int vaoId;                                        //  OpenGL VertexArray Object
    private int vertexBufId;                                  //  OpenGL Vertex Buffer
    private int vPositionId, vColorId, vNormalId;             //  Vertex Attribute S


    //  --------  Vertex-Array (fuer die Attribute Position, Color, Normal)  ------------

    private FloatBuffer vertexBuf;                            // Vertex-Array
    private int vAttribSize = 4*Float.SIZE/8;                 // Anz. Bytes eines Vertex-Attributes
    private int vertexSize = 3*vAttribSize;                   // Anz. Bytes eines Vertex
    private int bufSize;

    private float[] currentColor = { 1,1,1,1};                // aktuelle Farbe fuer Vertices
    private float[] currentNormal = { 1,0,0,0};               // aktuelle Normale Vertices


    //  ------------- Konstruktor  ---------------------------

    public VertexArray(GL3 gl,
                   int programId,                             // Program-Identifier
                   int maxVerts)                              // max. Anzahl Vertices im Vertex-Array
    {  this.maxVerts = maxVerts;
       bufSize = maxVerts * vertexSize;
       vertexBuf = Buffers.newDirectFloatBuffer(bufSize);
       setupVertexBuffer(gl, programId, bufSize);             // OpenGL Vertex-Buffer
    }


    //  ----------  oeffentliche Methoden   -------------


    public void setColor(float r, float g, float b)             // aktuelle Vertexfarbe setzen
    {  currentColor[0] = r;
       currentColor[1] = g;
       currentColor[2] = b;
       currentColor[3] = 1;
    }

    public void setNormal(float x, float y, float z)             // aktuelle Vertexfarbe setzen
    {  currentNormal[0] = x;
       currentNormal[1] = y;
       currentNormal[2] = z;
       currentNormal[3] = 0;
    }


    public void putVertex(float x, float y, float z)            // Vertex-Daten in Buffer speichern
    {  vertexBuf.put(x);
       vertexBuf.put(y);
       vertexBuf.put(z);
       vertexBuf.put(1);
       vertexBuf.put(currentColor);                              // Farbe
       vertexBuf.put(currentNormal);                             // Normale
       nVertices++;
    }


    public void copyBuffer(GL3 gl)                              // Vertex-Array in OpenGL-Buffer kopieren
    {  vertexBuf.rewind();
       if ( nVertices > maxVerts )
         throw new IndexOutOfBoundsException();
       gl.glBindVertexArray(vaoId);
       gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vertexBufId);
       gl.glBufferSubData(GL3.GL_ARRAY_BUFFER, 0, nVertices*vertexSize, vertexBuf);
    }


    public void rewindBuffer(GL3 gl)                            // Bufferposition zuruecksetzen
    {  vertexBuf.rewind();
       nVertices = 0;
    }


    public void drawArrays(GL3 gl, int figureType)              // Rendering-Pipeline starten
    {   gl.glDrawArrays(figureType, 0, nVertices);
    }


    public void drawArrays2(GL3 gl, int figureType, int startVertex,   // Startindex im Array
                            int nVertices)                             // Anzahl Vertices
    {   gl.glDrawArrays(figureType, startVertex, nVertices);
    }


    public void bindBuffer(GL3 gl)                              // activate Buffer
    {  gl.glBindVertexArray(vaoId);
       gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vertexBufId);
    }


    //  ---------  Abfrage-Methoden ----------

    public float[] getCurrentColor()
    {  float[] c = { currentColor[0],
                     currentColor[1], currentColor[2] };
       return c;
    }

    public float[] getCurrentNormal()
    {  float[] n = { currentNormal[0],
                     currentNormal[1], currentNormal[2]};
       return n;
    }


    public int getMaxVerts()                                   // max. Anzahl Vertices im Array
    {  return maxVerts;
    }

    public int getNumberOfVerts()                              // momentane Anzahl Vertices im Array
    {  return nVertices;
    }


    //  ---------  Zeichenmethoden  ------------------------------

    public void drawAxis(GL3 gl, float a, float b, float c)                   // Koordinatenachsen zeichnen
    {  rewindBuffer(gl);
       putVertex(0,0,0);           // Eckpunkte in VertexArray speichern
       putVertex(a,0,0);
       putVertex(0,0,0);
       putVertex(0,b,0);
       putVertex(0,0,0);
       putVertex(0,0,c);
       copyBuffer(gl);
       drawArrays(gl, GL3.GL_LINES);
    }

    //  -------------  private Methoden  ---------------------------

    void setupVertexBuffer(GL3 gl, int pgm, int bufSize)             // OpenGL VertexBuffer
    {
      // ----- generate VertexArrayObject  -------------
      int[] vaoIdArray = new int[1];
      gl.glGenVertexArrays(1, vaoIdArray, 0);
      vaoId = vaoIdArray[0];
      gl.glBindVertexArray(vaoId);

      // ----- generate BufferObject  -------------
      int[] bufIdArray = new int[1];
      gl.glGenBuffers(1, bufIdArray, 0);
      vertexBufId = bufIdArray[0];
      gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vertexBufId);
      gl.glBufferData(GL3.GL_ARRAY_BUFFER, bufSize,           // Speicher allozieren
                            null, GL3.GL_STATIC_DRAW);

      vPositionId = defineAttribute(gl, pgm, "vPosition", vertexSize, 0);
      vColorId = defineAttribute(gl, pgm, "vColor", vertexSize,  vAttribSize);
      vNormalId = defineAttribute(gl, pgm, "vNormal", vertexSize, 2*vAttribSize);
    }


    int defineAttribute(GL3 gl, int pgm, String attribName, int vertexSize, int offset)
    {  int attribId = gl.glGetAttribLocation(pgm, attribName);
       if ( attribId >= 0 )
       {  gl.glEnableVertexAttribArray(attribId);
          gl.glVertexAttribPointer(attribId, 4, GL3.GL_FLOAT, false, vertexSize, offset);
          System.out. println("Attribute " + attribName + " enabled");
       }
       else
          System.out. println("Attribute " + attribName + " not enabled");
       return attribId;
    }
}