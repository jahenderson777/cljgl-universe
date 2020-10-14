(ns cljgl-universe.core
  (:import cljgl_universe.Util
           (org.lwjgl BufferUtils)
           (org.lwjgl.opengl GL GL11)
           (org.lwjgl.glfw GLFW GLFWErrorCallback GLFWKeyCallback)))

(defonce *state (atom {:mouse-x 0
                       :mouse-y 0
                       :pressed-keys #{}
                       :x-velocity 0
                       :y-velocity 0
                       :player-x 0
                       :player-y 0
                       :can-jump? false
                       :direction :right
                       :player-images {}
                       :player-image-key :walk1}))

(defn init [game])
(defn tick [game]
  (GL11/glClearColor 0.8 0.0 0.0 1.0)
  (GL11/glClear GL11/GL_COLOR_BUFFER_BIT)
  ;(println game)
  game)

