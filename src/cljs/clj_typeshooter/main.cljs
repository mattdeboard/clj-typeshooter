(ns clj-typeshooter.main
 (:use-macros
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
(defn adjust-speed [ch event]
  (let [code (.-keyCode event)]
    (go
     (cond
      (= code 37) (>! ch -1)
      (= code 39) (>! ch 1)
      :else nil))))

(defn start-click [ch event]
  (go (>! ch {:state 1})))

(defn update-ping [ch event]
  (while (true? (:active @game-state))
    (go (>! ch "start"))))

;; chan definitions & signal bindings
(defn speed-chan [elem event-type]
  (let [ch (chan)
        f (partial adjust-speed ch)]
    (dommy/listen! elem event-type f)
    ch))

(defn start-chan [elem event-type]
  (let [ch (chan)
        f (partial start-click ch)]
    (dommy/listen! elem event-type f)
    ch))

(defn loop-chan [elem event-type]
  (let [ch (chan)
        f (partial update-ping ch)]
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
  (dommy/listen! js/window :load init)
  (let [accel-ch (speed-chan (sel1 :body) :keydown)
        start-ch (start-chan (sel1 :button#start) :click)
        loop-ch (loop-chan (sel1 :body) "startloop")]
    (speedometer accel-ch)
    (start-game start-ch)
    (update-loop loop-ch)))


(main)
