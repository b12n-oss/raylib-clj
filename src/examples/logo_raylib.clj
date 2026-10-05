(ns examples.logo-raylib
  "raylib [shapes] example - logo raylib

   Draws the raylib logo using basic shapes (rectangles and text).
   This is NOT a texture - it's drawn purely with shapes!

   Difficulty: 1/4
   Based on: shapes/shapes_logo_raylib.c"
  (:require
   [net.b12n.raylib-clj.core.window :as rcw]
   [net.b12n.raylib-clj.core.timing :as rct]
   [net.b12n.raylib-clj.core.drawing :as rcd]
   [net.b12n.raylib-clj.text.drawing :as rtd]
   [net.b12n.raylib-clj.shapes.basic :as rsb]
   [net.b12n.raylib-clj.colors :as colors]
   [net.b12n.raylib-clj.nrepl :as nrepl]
   [net.b12n.raylib-clj.debug-stats :as debug-stats]))

(def screen-width 800)
(def screen-height 450)

(defn init []
  (rcw/init-window! screen-width screen-height "raylib [shapes] example - logo raylib")
  (rct/set-target-fps! 60)
  (debug-stats/enable!))

(defn draw []
  (rcd/begin-drawing!)
  (rcd/clear-background! colors/raywhite)

  ;; Black outer rectangle
  (rsb/draw-rectangle! (- (quot screen-width 2) 128)
                       (- (quot screen-height 2) 128)
                       256 256 colors/black)
  ;; White inner rectangle
  (rsb/draw-rectangle! (- (quot screen-width 2) 112)
                       (- (quot screen-height 2) 112)
                       224 224 colors/raywhite)
  ;; "raylib" text
  (rtd/draw-text! "raylib" (- (quot screen-width 2) 44)
                  (+ (quot screen-height 2) 48) 50 colors/black)

  (rtd/draw-text! "this is NOT a texture!" 350 370 10 colors/gray)

  (debug-stats/draw!)
  (rcd/end-drawing!))

(defn -main [& _args]
  (nrepl/start {:port 7888})
  (init)
  (loop []
    (when-not (rcw/window-should-close?)
      (debug-stats/update!)
      (draw)
      (recur)))
  (rcw/close-window!))
