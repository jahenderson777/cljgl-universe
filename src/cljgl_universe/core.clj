(ns cljgl-universe
  (:require [clojure2d.core :as d]
            [clojure2d.color :as c]
            [clojure2d.pixels :as p]
            [fastmath.random :as r]
            [fastmath.core :as m])
  (:import [fastmath.vector Vec2 Vec4]
           cljgl_universe.UniverseEngine))

(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
;(m/use-primitive-operators)

(def size 1024)
(def num-light-points 5000)
(def num-dark-points 8000)

(def u (cljgl_universe.UniverseEngine. size num-light-points num-dark-points))
(.randomizeLightPoints u)
(.randomizeDarkPoints u)
(.indexPoints u)

(def p (atom [512 512]))

(defn draw-grid [canvas]
  (doseq [l [64 256]] 
    (d/set-color canvas (c/color 0 255 0 l))
    (d/set-stroke canvas 1.0)
    (loop [x (d/width canvas)]
      (when (> x 0.0)
        (d/line canvas x 0 x 1024)
        (d/line canvas 0 x 1024 x)
        (recur (- x l))))))

(defn draw-test-bounds [canvas]
  (d/set-color canvas :red)
  (d/set-stroke canvas 1.0)
  (let [[px py] @p
        x-bounds (partition 2 (.bounds h px))
        y-bounds (partition 2 (.bounds h py))]
    (doall
     (map (fn [[x-start x-end] [y-start y-end] d]
            (d/rect canvas
                  (* d x-start)
                  (* d y-start)
                  (- (* d x-end) (* d x-start))
                  (- (* d y-end) (* d y-start))
                  true))
          x-bounds y-bounds (iterate #(* 4 %) 16)))))

(defn draw-points [canvas]
  (.indexPoints u)
  (.calcLightPoints u)
  (.calcDarkPoints u)
  (.switchBuffers u)
  (let [^floats lx (.getLightPointsX u)
        ^floats ly (.getLightPointsY u)
        ^floats dx (.getDarkPointsX u)
        ^floats dy (.getDarkPointsY u)]
    (dotimes [i num-light-points]
      (let [^ints col (UniverseEngine/hslColor (/ (aget (.-lightPointsForce u) i) 5000.0) 1.0 0.65)]
        (d/set-color canvas (aget col 0) (aget col 1) (aget col 2)))
  
      (d/rect canvas (aget lx i) (aget ly i) 1 1)
      )
    (d/set-color canvas :black)
    (dotimes [i num-dark-points]
      (d/rect canvas (aget dx i) (aget dy i) 2 2))))

(comment
  (let [draw (fn [canvas window frame state]
               (when (clojure2d.core/mouse-pressed? window)
                 (.randomizeLightPoints u)
                 (.randomizeDarkPoints u)
                 (reset! p [(d/mouse-x window) (d/mouse-y window)]))
               (d/set-background canvas 110 110 110 28)
               (draw-points canvas)
               ;(draw-grid canvas)
               ;(draw-test-bounds canvas)

               ;(d/save canvas (d/next-filename "results/spin/" ".jpg"))
               )]
    (d/show-window {:canvas (d/canvas size size)
                    :draw-fn draw
                    :setup (fn [canvas _]
                             (d/set-background canvas :white)
                             nil)}))
  )