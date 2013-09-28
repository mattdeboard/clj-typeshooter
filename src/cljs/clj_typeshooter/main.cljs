(ns clj-typeshooter.main
  (:use-macros
   [clj-typeshooter.macros :only [go-handle!]]
   [dommy.macros :only [sel sel1]])
  (:require
   [cljs.core.async :as async :refer [>! <! chan]]
   [clj-typeshooter.words :as words]
   [dommy.core :as dommy]
   [simplegame :as sg])
  (:require-macros
   [cljs.core.async.macros :as m :refer [go]]))

(def rocket (atom {:state {:speed 0 :x-coord 250 :y-coord -850}}))
(def game-state (atom {:active false}))
(def start-loop-event (new js/CustomEvent "startloop"))

(defn render-rocket [game]
  (let [sprite (new sg/Sprite game "img/spaceship.png" 50 50)
        state (:state @rocket)]
    (doto sprite
      (.setX (:x-coord state))
      (.setY (:y-coord state))
      (.setSpeed (:speed state))
      (.update))
    (swap! rocket #(assoc % :sprite sprite))))

(defn update []
  (.clear (:game @game-state))
  (.update (:sprite @rocket)))

;; chan consumers
(defn start-game [ch]
  (go
   (loop []
     (let [val (<! ch)]
       (.start (:game @game-state) update)
       (swap! game-state #(assoc % :active true))
       (.dispatchEvent (sel1 :body) start-loop-event))
     (recur))))

(defn speedometer [ch]
  (go
   (while true
     (let [val (<! ch)
           sprite (:sprite @rocket)
           speed (+ (.getSpeed sprite) val)
           updater #(merge % {:speed speed})]
       (.setSpeed sprite speed)
       (swap! rocket updater)))))

(defn update-loop [ch]
  (go
   (loop []
     (let [val (<! ch)]
       (if (= "start" val)
         (update)
         (recur))))))

;; chan producers
(defn handle-speed [ch update-ch event]
  (let [code (.-keyCode event)]
    (go-handle! update-ch
                (cond (= code 37) (>! ch -1) (= code 39) (>! ch 1) :else nil))))

(defn handle-start-click [ch update-ch event]
  (go-handle! update-ch (>! ch {:state 1})))

;; chan definitions & signal bindings
(defn init-chan [elem event-type handler update-ch]
  "Initialize channel for handling UI event `event-type' on DOM element `elem'
to be handled by event-handler `handler'."
  (let [ch (chan)
        f (partial handler ch update-ch)]
    (dommy/listen! elem event-type f)
    ch))

;; admin (render page, etc.)
(defn init []
  (let [game (new sg/Scene)
        sprite (render-rocket game)]
    (doto game
      (.setSizePos 500 800 200 40)
      (.setBG "black"))
    (swap! game-state #(assoc % :game game))))

(defn main []
  ;; Call `init' on once `window' is fully loaded.
  (dommy/listen! js/window :load init)
  (let [update-chan (chan)
        accel-chan (init-chan (sel1 :body) :keydown handle-speed update-chan)
        start-chan (init-chan (sel1 :button#start) :click handle-start-click
                              update-chan)]
    (speedometer accel-chan)
    (start-game start-chan)
    (update-canvas update-chan)))

(main)
