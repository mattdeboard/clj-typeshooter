(ns clj-typeshooter.macros)

(defmacro go-handle! [update-ch & body]
  "Execute `body' inside goroutine, then send data to channel `update-ch'."
  `(cljs.core.async.macros/go
    (do ~@body)
    (cljs.core.async/>! ~update-ch 1)))
