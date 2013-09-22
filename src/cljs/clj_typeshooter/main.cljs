(ns clj-typeshooter.main)
  (:use-macros [dommy.macros :only [sel sel1]])
  (:require [cljs.core.async :as async :refer [>! <! chan close!]]
            [clj-typeshooter.words :as words]
            [dommy.utils :as utils]
            [dommy.core :as dommy]
            [simplegame :as sg])
  (:require-macros [cljs.core.async.macros :as m :refer [go alt!]]))

(def load-chan (chan))
(def click-chan (chan))

(defn speedometer [ch]
  (go
   (while true
     (let [val (<! ch)]
       (.log js/console val)))))

(defn adjust-speed [ch event]
  (let [code (.-keyCode event)]
    (go
     (cond
      (= code 37) (>! ch -1)
      (= code 39) (>! ch 1)
      :else nil))))
  
(defn speed-chan [elem event-type]
  (let [ch (chan 100)
        f (partial adjust-speed ch)]
    (dommy/listen! elem event-type f)))

(defn init []
  (let [game (new sg/Scene)]
    (doto game
      (.setSizePos 500 800 200 40)
      (.setBG "black"))
    game))

(defn main []
  (dommy/listen! js/window :load init)
  (let [ch (speed-chan (sel1 :body) :keydown)]
    (go (loop []
          (.log js/console (<! ch))
          (recur)))))

(main)
