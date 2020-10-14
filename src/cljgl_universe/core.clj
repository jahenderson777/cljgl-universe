(ns cljgl-universe
  (:require [clojure2d.core :as d]
            [clojure2d.color :as c]
            [clojure2d.pixels :as p]
            [fastmath.random :as r]
            [fastmath.core :as m])
  (:import [fastmath.vector Vec2]
           cljgl_universe.Helper))

(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
(m/use-primitive-operators)

(def size 1024)
(def num-light-points 4500)
(def num-dark-points 9000)
(def h (cljgl_universe.Helper. size num-light-points num-dark-points))
(.randomizeLightPoints h)
(.randomizeDarkPoints h)
(.indexPoints h)

(defn draw-grid [canvas]
  (loop [l 16]
    (when (< l 512)
      (d/set-color canvas (c/color 0 255 0 l))
      (d/set-stroke canvas 1.0)
      (loop [x (d/width canvas)]
        (when (> x 0.0)
          (d/line canvas x 0 x 1024)
          (d/line canvas 0 x 1024 x)
          (recur (- x l))))
      (recur (* 4 l)))))

(defn draw-points [canvas]
  (.calcLightPoints h)
  (.calcDarkPoints h)
  (.switchBuffers h)
  (let [^floats lx (.getLightPointsX h)
        ^floats ly (.getLightPointsY h)
        ^floats dx (.getDarkPointsX h)
        ^floats dy (.getDarkPointsY h)]
    (d/set-color canvas :white)
    (dotimes [i num-light-points]
      (d/rect canvas (aget lx i) (aget ly i) 2 2))
    (d/set-color canvas :black)
    (dotimes [i num-dark-points]
      (d/rect canvas (aget dx i) (aget dy i) 2 2))))

(comment
  (let [draw (fn [canvas window frame state]
               #_(when (clojure2d.core/mouse-pressed? window)
                   (reset! p [(d/mouse-x window) (d/mouse-y window)]))
               (d/set-background canvas 120 120 120 60)
               ;(draw-grid canvas)
               (draw-points canvas)
               
               )]
    (d/show-window {:canvas (d/canvas size size)
                    :draw-fn draw
                    :setup (fn [canvas _]
                             (d/set-background canvas :white)
                             nil)}))
  )