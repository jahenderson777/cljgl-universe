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
(def num-light-points 4500)
(def num-dark-points 8200)

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
        x-bounds (partition 2 (.bounds u px))
        y-bounds (partition 2 (.bounds u py))]
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
        ^floats lxb (.getLightPointsXb u)
        ^floats lyb (.getLightPointsYb u)
        ^floats dx (.getDarkPointsX u)
        ^floats dy (.getDarkPointsY u)]
    (dotimes [i num-light-points]
      (d/set-color canvas
                   (aget (.-lightPointsRed u) i)
                   (aget (.-lightPointsGreen u) i)
                   (aget (.-lightPointsBlue u) i))
      (d/line canvas 
              (aget lxb i) (aget lyb i)
              (aget lx i) (aget ly i))
      ;(d/rect canvas (aget lx i) (aget ly i) 1 1)
      )
    (d/set-color canvas 90 90 90)
    (dotimes [i num-dark-points]
      (d/rect canvas (aget dx i) (aget dy i) 2 2))))

(comment
  (let [draw (fn [canvas window frame state]
               (when (clojure2d.core/mouse-pressed? window)
                 (.randomizeLightPoints u)
                 (.randomizeDarkPoints u)
                 (reset! p [(d/mouse-x window) (d/mouse-y window)]))
               ;(d/set-background canvas 90 90 90 25)
               (d/set-background canvas 0 0 0 28)
               (draw-points canvas)
               ;(draw-grid canvas)
               ;(draw-test-bounds canvas)

               (d/save canvas (d/next-filename "results/colour/" ".jpg"))
               )]
    (d/show-window {:canvas (d/canvas size size)
                    :draw-fn draw
                    :setup (fn [canvas _]
                             (d/set-background canvas :white)
                             nil)}))
  )