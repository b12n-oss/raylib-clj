(ns net.b12n.raylib-clj.core.keyboard
  (:require
   [net.b12n.raylib-clj.core]
   [net.b12n.raylib-clj.internals :as ri]
   [coffi.mem :as mem]
   [coffi.ffi :refer [defcfn]]))

(defcfn is-key-pressed?
  "Check if a key has been pressed once"
  {:arglists '([key])}
  "IsKeyPressed"
  [::mem/int] ::ri/bool)

(defcfn is-key-down?
  "Check if a key is being pressed"
  {:arglists '([key])}
  "IsKeyDown"
  [::mem/int] ::ri/bool)

(defcfn is-key-released?
  "Check if a key has been released once"
  {:arglists '([key])}
  "IsKeyReleased"
  [::mem/int] ::ri/bool)

(defcfn is-key-up?
  "Check if a key is NOT being pressed"
  {:arglists '([key])}
  "IsKeyUp"
  [::mem/int] ::ri/bool)

(defcfn set-exit-key!
  "Set a custom key to exit program (default is ESC)"
  {:arglists '([key])}
  "SetExitKey"
  [::mem/int] ::mem/void)

(defcfn get-key-pressed
  "Get key pressed (keycode), call it multiple times for keys queued, returns 0 when the queue is empty"
  "GetKeyPressed"
  [] ::mem/int)

(defcfn get-char-pressed
  "Get char pressed (unicode), call it multiple times for chars queued, returns 0 when the queue is empty"
  "GetCharPressed"
  [] ::mem/int)
