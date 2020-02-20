//  ------------  Vertex- und Fragment-Shaders  -----------------------------------------
//                                                              E.Gutknecht, Feb. 2020
package ch.fhnw.util.opengl;

import com.jogamp.opengl.*;

public class MyShaders
{

    /*  -----------  Vertex-Shader (Pass-through Shader) ---------- */
    public static String vShader0 =
    "   #version 330                  /* Shader Language Version */   \n" +
    "   in vec4 vPosition, vColor;    /* Vertex-Attribute */          \n" +
    "   out vec4 fColor;              /* Fragment-Farbe */            \n" +
    "   void main()                                                   \n" +
    "   {  gl_Position = vPosition;                                   \n" +
    "      fColor = vColor;                                           \n" +
    "   }";


    /* -----------  Fragment-Shader (Pass-through Shader) ---------  */
    public static String fShader0 =
    "    #version 330                   \n" +
    "    in  vec4 fColor;               \n" +
    "    out vec4 fragColor;            \n" +
    "    void main()                    \n" +
    "    {  fragColor = fColor;         \n" +
    "    }";


    /* -----------  Fragment-Shader (mit Beleuchtungsrechnung) ---------  */
    public static String fShader1 =
    "   #version 330                   \n" +
    "   in  vec4 fPosition, fColor, fNormal; \n" +
    "   uniform vec4 lightPosition;                          /* Position Lichtquelle (im Cam.System) */   \n" +
    "   uniform int shadingLevel;                            /* 0 ohne Beleucht, 1 mit Beleucht.     */   \n" +
    "   uniform float ambient;                               /* ambientes Licht */                        \n" +
    "   uniform float diffuse;                               /* diffuse Reflexion */                      \n" +
    "   uniform float specular;                              /* spiegelnde Reflexion */                   \n" +
    "   uniform float specExp;                               /* Shininess (Exponent) */                   \n" +
    "   vec3 whiteColor = vec3(1,1,1);  \n" +
    "   out vec4 fragColor;     /* Output-Farbe */        \n" +
    "   void main()                     \n" +
    "   { if (shadingLevel < 1.0)       \n" +
    "     {  fragColor = fColor;      /* ohne Beleuchtung */   \n" +
    "        return;                     \n" +
    "     }                              \n" +
    "     /*  -----  Beleuchtungsrechnung  ----------   */   \n" +
    "     vec3 toEye = -normalize(fPosition.xyz);            \n" +
    "     vec3 normal = normalize(fNormal.xyz);               \n" +
    "     vec3 toLight = normalize(lightPosition.xyz - fPosition.xyz);    \n" +
    "     /*  -----  diffuse Reflexion  -------     */                 \n" +
    "     float ndotl = dot(normal, toLight);     /* Skalarprod */ \n" +
    "     float ndote = dot(normal, toEye);     \n" +
    "     if (ndotl < 0.0 || ndotl*ndote < 0.0) \n" +
    "     {  fragColor = vec4(ambient*fColor.x,ambient*fColor.y,ambient*fColor.z,1);\n" +
    "        return;  \n" +
    "     }  \n" +
    "     float diffuseIntens = diffuse *  ndotl;      /* diffuse Reflexion */ \n" +
    "     vec3 computedColor = (ambient + diffuseIntens)*fColor.xyz;    \n" +
    "     vec3 halfBetween = normalize(toLight + toEye);      \n" +
    "     /* ------  spiegelnde Reflexion  -------    */       \n" +
    "     float ndoth = dot(normal,halfBetween);           /* Skalarprod */  \n" +
    "     if ( ndoth > 0.0 )                                                                    \n" +
    "     { float specularIntens = specular*pow( ndoth, specExp);                               \n" +
    "       computedColor += specularIntens * whiteColor;                                       \n" +
    "     }                                                                                     \n" +
    "     computedColor = min(computedColor, whiteColor);                                       \n" +
    "     fragColor = vec4(computedColor.r, computedColor.g, computedColor.b,1.0);              \n" +
    "   }";


   /* -----------  Vertex-Shader  (Vertex-Transf. und Phong-Beleuchtung)  ------  */
    public static String vShader1 =
    "   #version 330                              /* Shader Language Version */                \n" +
    "   uniform mat4 V, M, P;                     /* Transformations-Matrizen */               \n" +
    "   in vec4 vPosition, vColor, vNormal;       /* Vertex-Attribute */                       \n" +
    "   out vec4 fPosition, fNormal, fColor;      /* fuer Fragment-Shader */                         \n" +
    "                                                                                           \n" +
    "   /* ------  main-function  --------           */                                         \n" +
    "   void main()                                                                             \n" +
    "   {  vec4 vWorldCoord = M * vPosition;      /* ModelView-Transformation */   \n" +
    "      vec4 vEyeCoord = V * vWorldCoord;      /* View-Transformation */       \n" +
    "      fPosition = vEyeCoord;                                                 \n" +
    "      gl_Position = P * vEyeCoord;           /* Projektion */                 \n" +
    "      vec4 nWorldCoord = M * vNormal;        /* ModelView-Transf. der Normalen */   \n" +
    "      fNormal = V * nWorldCoord;                                          \n" +
    "      fColor = vColor;                                               \n" +
    "   }";


    /* -----------  Vertex-Shader mit Vertex-Transformationen  */
    public static String vShader1a =
    "   #version 330                                                              \n" +
    "   uniform mat4 V, M, P;                    /* Transformations-Matrizen */   \n" +
    "   in vec4 vPosition, vColor, vNormal;      /* Vertex-Attribute */           \n" +
    "   out vec4 fColor;                         /* Fragment-Farbe */             \n" +
    "   void main()                                                               \n" +
    "   {  vec4 worldCoord = M * vPosition;      /* ModelView-Transformation */   \n" +
    "      vec4 eyeCoord = V * worldCoord;       /* View-Transformation */       \n" +
    "      gl_Position = P * eyeCoord;           /* Projektion */                 \n" +
    "      fColor = vColor;                                                       \n" +
    "   }";



    /* -----------  Vertex-Shader mit Vertex-Transformationen und Beleuchtung        ------  */
    public static String vShader1b =
    "   #version 330                                         /* Shader Language Version */                \n" +
    "   uniform mat4 V, M, P;                                /* Transformations-Matrizen */               \n" +
    "   uniform vec4 lightPosition;                          /* Position Lichtquelle (im Cam.System) */   \n" +
    "   uniform int shadingLevel;                            /* 0 ohne Beleucht, 1 mit Beleucht.     */   \n" +
    "   uniform float ambient;                               /* ambientes Licht */                        \n" +
    "   uniform float diffuse;                               /* diffuse Reflexion */                      \n" +
    "   uniform float specular;                              /* spiegelnde Reflexion */                   \n" +
    "   uniform float specExp;                               /* Shininess (Exponent) */                   \n" +
    "   in vec4 vPosition, vColor, vNormal;                  /* Vertex-Attribute */                       \n" +
    "   out vec4 fColor;                                     /* Fragment-Farbe */                         \n" +
    "   vec3 whiteColor = vec3(1,1,1);                                                                    \n" +
    "                                                                                           \n" +
    "   /* ------  main-function  --------           */                                         \n" +
    "   void main()                                                                             \n" +
    "   {  vec4 vWorldCoord = M * vPosition;      /* ModelView-Transformation */   \n" +
    "      vec4 vEyeCoord = V * vWorldCoord;       /* View-Transformation */       \n" +
    "      gl_Position = P * vEyeCoord;           /* Projektion */                 \n" +
    "      if ( shadingLevel < 1 )                                                              \n" +
    "      {  fColor = vColor;                               /* ohne Beleuchtung */            \n" +
    "         return;                                                                           \n" +
    "      }                                                                                   \n" +
    "      /*  -----  Beleuchtungsrechnung  ----------   */                                     \n" +
    "      vec4 tmp = M * vNormal;               /* ModelView-Transf. der Normalen */           \n" +
    "      tmp = V * tmp;                                          \n" +
    "      vec3 normal = normalize(tmp.xyz);                                          \n" +
    "      vec3 toLight = normalize(lightPosition.xyz - vEyeCoord.xyz);                            \n" +
    "      /*  -----  diffuse Reflexion  -------     */                                         \n" +
    "      float ndotl = max(0.0, dot(normal, toLight));     /* Skalarprod */                   \n" +
    "      float diffuseIntens = diffuse *  ndotl;      /* diffuse Reflexion */                 \n" +
    "      vec3 computedColor = (ambient + diffuseIntens)*vColor.xyz;                           \n" +
    "      vec3 toEye = -normalize(vEyeCoord.xyz);                                                 \n" +
    "      vec3 halfBetween = normalize(toLight + toEye);                                        \n" +
    "      /* ------  spiegelnde Reflexion  -------    */                                        \n" +
    "      float ndoth = dot(normal,halfBetween);           /* Skalarprod */                     \n" +
    "      if ( ndoth > 0.0 )                                                                    \n" +
    "      { float specularIntens = specular*pow( ndoth, specExp);                               \n" +
    "        computedColor += specularIntens * whiteColor;                                       \n" +
    "      }                                                                                     \n" +
    "      computedColor = min(computedColor, whiteColor);                                       \n" +
    "      fColor = vec4(computedColor.r, computedColor.g, computedColor.b,1.0);                  \n" +
    "   }";


    public static int initShaders(GL3 gl,
                                   String vShader,   // Vertex-Shader
                                   String fShader)   // Fragment-Shader
    {
       int vShaderId = gl.glCreateShader(GL3.GL_VERTEX_SHADER);
       int fShaderId = gl.glCreateShader(GL3.GL_FRAGMENT_SHADER);


       gl.glShaderSource(vShaderId, 1, new String[] { vShader }, null);
       gl.glCompileShader(vShaderId);                                      // Compile Vertex Shader
       System.out.println("VertexShaderLog:");
       System.out.println(getShaderInfoLog(gl, vShaderId));
       System.out.println();


       gl.glShaderSource(fShaderId, 1, new String[] { fShader }, null);
       gl.glCompileShader(fShaderId);                                     // Compile Fragment Shader
       System.out.println("FragmentShaderLog:");
       System.out.println(getShaderInfoLog(gl, fShaderId));
       System.out.println();

       int programId = gl.glCreateProgram();
       gl.glAttachShader(programId, vShaderId);
       gl.glAttachShader(programId, fShaderId);
       gl.glLinkProgram(programId);                                       // Link Program
       gl.glUseProgram(programId);                                        // Activate Programmable Pipeline
       System.out.println("ProgramInfoLog:");
       System.out.println(getProgramInfoLog(gl, programId));
       System.out.println();
       return programId;
    }


    public static String getProgramInfoLog(GL3 gl, int obj)               // Info- and Error-Messages
    {
       int params[] = new int[1];
       gl.glGetProgramiv(obj, GL3.GL_INFO_LOG_LENGTH, params, 0);         // get log-length
       int logLen = params[0];
       if (logLen <= 0)
         return "";
       byte[] bytes = new byte[logLen + 1];
       int[] retLength = new int[1];
       gl.glGetProgramInfoLog(obj, logLen, retLength, 0, bytes, 0);       // get log-data
       String logMessage = new String(bytes);
       int iend = logMessage.indexOf(0);
       if (iend < 0 ) iend = 0;
       return logMessage.substring(0,iend);
    }


    static public String getShaderInfoLog(GL3 gl, int obj)               // Info- and Error-Messages
    {  int params[] = new int[1];
       gl.glGetShaderiv(obj, GL3.GL_INFO_LOG_LENGTH, params, 0);         // get log-length
       int logLen = params[0];
       if (logLen <= 0)
         return "";
       // Get the log
       byte[] bytes = new byte[logLen + 1];
       int[] retLength = new int[1];
       gl.glGetShaderInfoLog(obj, logLen, retLength, 0, bytes, 0);       // get log-data
       String logMessage = new String(bytes);
       int iend = logMessage.indexOf(0);
       if (iend < 0 ) iend = 0;
       return logMessage.substring(0,iend);
    }

}