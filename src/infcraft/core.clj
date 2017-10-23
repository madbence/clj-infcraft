(ns infcraft.core
  (:import [com.jogamp.opengl GLProfile GLCapabilities GLEventListener GL3]
           [com.jogamp.opengl.util Animator GLBuffers]
           [com.jogamp.opengl.util.glsl ShaderCode ShaderProgram]
           [com.jogamp.newt.opengl GLWindow])
  (:gen-class))

(defn set-color!
  [buffer r g b a]
  (doto buffer
    (.put 0 r)
    (.put 1 g)
    (.put 2 b)
    (.put 3 a)))

(def vertex-data (float-array [-0.5  0   1 0 0
                                0    0.5 0 1 0
                                0.5 -0.5 0 0 1]))

(defn create-shader-program
  [context root vertex fragment]
  (let [vertex-shader (ShaderCode/create context GL3/GL_VERTEX_SHADER (class create-shader-program) root nil vertex "vert" nil true)
        fragment-shader (ShaderCode/create context GL3/GL_FRAGMENT_SHADER (class create-shader-program) root nil fragment "frag" nil true)
        shader-program (ShaderProgram.)]
    (.add shader-program vertex-shader)
    (.add shader-program fragment-shader)
    (.init shader-program context)
    (.program shader-program)
    (.link shader-program context System/err)
    shader-program))

(defn -main
  [& args]
  (let [profile (GLProfile/get GLProfile/GL3)
        capabilities (GLCapabilities. profile)
        window (GLWindow/create capabilities)
        animator (Animator. window)
        clear-color (GLBuffers/newDirectFloatBuffer 4)
        clear-depth (GLBuffers/newDirectFloatBuffer 1)
        vbo (GLBuffers/newDirectIntBuffer 1)
        vao (GLBuffers/newDirectIntBuffer 1)
        shader-program (GLBuffers/newDirectIntBuffer 1)]
    (.setTitle window "Hello world!")
    (.setSize window 800 600)
    (.setVisible window true)
    (set-color! clear-color 0 0 0 1)
    (.put clear-depth 0 1)
    (.addGLEventListener window (reify GLEventListener
                                  (display [this drawable]
                                    (let [gl (-> drawable .getGL .getGL3)]
                                      (.glClearBufferfv gl GL3/GL_COLOR 0 clear-color)
                                      (.glClearBufferfv gl GL3/GL_DEPTH 0 clear-color)
                                      (.glUseProgram gl (.get shader-program 0))
                                      (.glBindVertexArray gl (.get vao 0))
                                      (.glDrawArrays gl GL3/GL_TRIANGLES 0 3)
                                      (println (str "shader: " (.get shader-program 0)))))
                                  (dispose [this drawable] (println "dispose"))
                                  (init [this drawable]
                                    (let [gl (-> drawable .getGL .getGL3)
                                          vertex-buffer (GLBuffers/newDirectFloatBuffer vertex-data)
                                          shader (create-shader-program gl "shaders" "hello" "hello")]
                                      (.glGenBuffers gl 1 vbo)
                                      (.glGenVertexArrays gl 1 vao)
                                      (.glBindVertexArray gl (.get vao 0))
                                      (.glBindBuffer gl GL3/GL_ARRAY_BUFFER (.get vbo 0))
                                      (.glBufferData gl GL3/GL_ARRAY_BUFFER (* Float/BYTES (count vertex-data)) vertex-buffer GL3/GL_STATIC_DRAW)
                                      (.put shader-program 0 (.id shader))
                                      (.glEnableVertexAttribArray gl 0)
                                      (.glVertexAttribPointer gl 0 2 GL3/GL_FLOAT false 20 0)
                                      (.glEnableVertexArrayAttrib gl (.get vao 0) 1)
                                      (.glVertexAttribPointer gl 1 3 GL3/GL_FLOAT false 20 8)))
                                  (reshape [this drawable x y w h] (println "reshape"))))
    (.start animator)))
