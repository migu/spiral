(ns vis42.core
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(defn draw-circle [x y state]
  (let [radius (:radius state)]
    (q/ellipse x y radius radius)))

(defn draw-rectangle [x y state]
  (let [radius (:radius state)]
    (q/rect x y radius radius (* 50 (+ 1 (q/sin (:angle state)))))))

(defn setup []
  (q/frame-rate 30)
  (q/color-mode :hsb)
  {:color 0
   :angle 0
   :radius 100
   :grow true
   :draw-shape draw-rectangle})

(defn update-state [state]
  (let [radius (:radius state)
        grow (cond
               (< 50 radius 150) (:grow state)
               (>= radius 150) false
               :else true)
        draw-fn (if grow draw-circle draw-rectangle)]
    {:color (mod (+ (:color state) 0.7) 255)
     :angle (+ (:angle state) 0.3)
     :radius (+ radius (if grow 1 -1))
     :grow grow
     :draw-shape draw-rectangle}))

(defn draw-state [state]
  (q/background 240)
  (q/fill (:color state) 255 255)
  (let [angle (:angle state)
        r (+ (rand-int 70) 80)
        x (* r (q/cos angle))
        y (* r (q/sin angle))
        radius (:radius state)]
    (q/with-translation [(/ (q/width) 2)
                         (/ (q/height) 2)]
      (apply (:draw-shape state) [x y state]))))


(q/defsketch vis42
  :title "You spin my circle right round"
  :size [500 500]
  :setup setup
  :update update-state
  :draw draw-state
  :features [:keep-on-top]
  ; This sketch uses functional-mode middleware.
  ; Check quil wiki for more info about middlewares and particularly
  ; fun-mode.
  :middleware [m/fun-mode])
