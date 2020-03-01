(ns vis42.core
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(defn draw-circle [x y state]
  (let [radius (:radius state)]
    (q/ellipse x y radius radius)))

(defn draw-rectangle [x y state]
  (let [radius (:radius state)
        corner-radius (* 50 (+ 1 (q/sin (/ (:angle state) 4))))]
    (q/rect x y radius radius corner-radius)))

(def initial-state {:color 0
                    :angle 0
                    :radius 100
                    :grow true
                    :draw-shape draw-rectangle
                    :shapes []})

(defn setup []
  (q/frame-rate 30)
  (q/color-mode :hsb)
  initial-state)

(defn abs [x]
  (if (< x 0)
    (- x)
    x))

(defn diff [a b]
  (abs (- a b)))

(defn add-shape [shapes]
  (let [px (q/pmouse-x)
        py (q/pmouse-y)
        x (q/mouse-x)
        y (q/mouse-y)]
    (if (and (< (diff px x) 10)
             (< (diff py y) 10))
      (conj shapes {:x x :y y})
      shapes)))

(defn derive-state [state]
  (let [radius (:radius state)
          grow (cond
                 (< 50 radius 150) (:grow state)
                 (>= radius 150) false
                 :else true)
        draw-fn (if grow draw-circle draw-rectangle)
        shapes (add-shape (:shapes state))]
      {:color (mod (+ (:color state) 0.7) 255)
       :angle (+ (:angle state) 0.3)
       :radius (+ radius (if grow 1 -1))
       :grow grow
       :draw-shape draw-rectangle
       :shapes shapes}))

(defn update-state [state]
  (if (q/key-pressed?)
    initial-state
    (derive-state state)))

(defn draw-state [state]
  (q/background 0)
  (q/fill (:color state) 255 255)
  (let [angle (:angle state)
        r (+ (rand-int 70) 80)
        x (* r (q/cos angle))
        y (* r (q/sin angle))
        radius (:radius state)]
    (q/with-translation [(/ (q/width) 2)
                         (/ (q/height) 2)]
      (apply (:draw-shape state) [x y state]))
    (doseq [shape (:shapes state)]
        (q/rect (:x shape) (:y shape) 10 10))))

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
