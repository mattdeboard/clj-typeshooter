(ns clj-typeshooter.macros)

(defmacro go-handle! [update-ch & body]
  "Execute `body' inside goroutine, then send data to channel `update-ch'.
This basically is callback functionality without the spaghetti. The
'callback function' is a consumer of `update-ch'."
  `(cljs.core.async.macros/go
    (do ~@body)
    (cljs.core.async/>! ~update-ch 1)))
