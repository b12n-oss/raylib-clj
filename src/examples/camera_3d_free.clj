(ns examples.camera-3d-free
  "raylib [core] example - 3d camera free

   Free-form 3D camera with mouse controls.

   Difficulty: ⭐☆☆☆ (1/4)
   Based on: core/core_3d_camera_free.c"
  (:require
   [net.b12n.raylib-clj.core.window :as rcw]
   [net.b12n.raylib-clj.core.timing :as rct]
   [net.b12n.raylib-clj.core.drawing :as rcd]
   [net.b12n.raylib-clj.core.keyboard :as rck]
   [net.b12n.raylib-clj.core.camera3d :as rc3d]
   [net.b12n.raylib-clj.core.cursor :as rcur]
   [net.b12n.raylib-clj.text.drawing :as rtd]
   [net.b12n.raylib-clj.shapes.basic :as rsb]
   [net.b12n.raylib-clj.utils :as ru]
   [net.b12n.raylib-clj.enums :as enums]
   [net.b12n.raylib-clj.colors :as colors]))

(def screen-width 800)
(def screen-height 450)

(defn -main [& _args]
  (rcw/init-window! screen-width screen-height "raylib [core] example - 3d camera free")

  ;; Define camera
  (let [initial-camera {:position {:x 10.0
                                   :y 10.0
                                   :z 10.0}
                        :target {:x 0.0
                                 :y 0.0
                                 :z 0.0}
                        :up {:x 0.0
                             :y 1.0
                             :z 0.0}
                        :fovy 45.0
                        :projection rc3d/CAMERA_PERSPECTIVE}
        cube-position {:x 0.0
                       :y 0.0
                       :z 0.0}]

    (rcur/disable-cursor!) ; Lock cursor for camera control
    (rct/set-target-fps! 60)

    (loop [camera initial-camera]
      (if (rcw/window-should-close?)
        (rcw/close-window!)
        (let [;; Update camera with free mode
              updated-camera (rc3d/update-camera camera rc3d/CAMERA_FREE)

              ;; Reset camera target on Z key
              final-camera (if (rck/is-key-pressed? (:z enums/keyboard-key))
                             (assoc updated-camera :target {:x 0.0
                                                            :y 0.0
                                                            :z 0.0})
                             updated-camera)]

          ;; Draw
          (rcd/begin-drawing!)
          (rcd/clear-background! colors/raywhite)

          (rc3d/begin-mode-3d! final-camera)

          ;; Draw cube
          (rc3d/draw-cube! cube-position 2.0 2.0 2.0 colors/red)
          (rc3d/draw-cube-wires! cube-position 2.0 2.0 2.0 colors/maroon)

          ;; Draw grid
          (rc3d/draw-grid! 10 1.0)

          (rc3d/end-mode-3d!)

          ;; Draw info panel
          (rsb/draw-rectangle! 10 10 320 93 (ru/fade colors/skyblue 0.5))
          (rsb/draw-rectangle-lines! 10 10 320 93 colors/blue)

          (rtd/draw-text! "Free camera default controls:" 20 20 10 colors/black)
          (rtd/draw-text! "- Mouse Wheel to Zoom in-out" 40 40 10 colors/darkgray)
          (rtd/draw-text! "- Mouse Wheel Pressed to Pan" 40 60 10 colors/darkgray)
          (rtd/draw-text! "- Z to zoom to (0, 0, 0)" 40 80 10 colors/darkgray)

          (rcd/end-drawing!)
          (recur final-camera))))))
