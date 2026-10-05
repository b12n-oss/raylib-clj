(ns bb.helpers
  "Shared helper functions for bb tasks")

;; =============================================================================
;; Terminal Colors
;; =============================================================================

(def bold "\033[1m")
(def green "\033[0;32m")
(def yellow "\033[1;33m")
(def red "\033[0;31m")
(def cyan "\033[0;36m")
(def magenta "\033[0;35m")
(def blue "\033[0;34m")
(def reset "\033[0m")

(defn color [c text] (str c text reset))

;; =============================================================================
;; Output Helpers
;; =============================================================================

(defn step [n text] (println (color yellow (str "Step " n ":")) text))
(defn success [text] (println (color green (str "✅ " text))))
(defn error-msg [text] (println (color red (str "❌ " text))))
(defn info [text] (println (color cyan (str "ℹ️  " text))))
(defn warn [text] (println (color yellow (str "⚠️  " text))))

(defn header [text]
  (println)
  (println (color bold text))
  (println (apply str (repeat (count text) "="))))

(defn section [emoji title]
  (println)
  (println (color magenta (str emoji " " title))))
