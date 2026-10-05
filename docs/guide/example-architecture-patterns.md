# Example architecture patterns

This page describes how a raylib-clj game is structured. The examples it
quotes live in [raylib-clj-demo](https://github.com/b12n-oss/raylib-clj-demo),
not in this repo, so the links below point there.

## The shared skeleton

Most examples follow the same shape: start the embedded nREPL, open a
window, loop until the user closes it, clean up. A few, including `pong`,
`camera-2d` and `music-stream`, skip the embedded nREPL. Here's the `-main` of
[`asteroids/src/net/b12n/raylib_clj/scenes/asteroids.clj`](https://github.com/b12n-oss/raylib-clj-demo/blob/main/asteroids/src/net/b12n/raylib_clj/scenes/asteroids.clj)
(around line 525), verbatim:

```clojure
(defn -main [& args]
  (nrepl/start {:port 7888})
  (init)
  (loop []
    (let [game (tick (update-fps @game-atom))]
      (when-not (rcw/window-should-close?)
        (reset! game-atom game)
        (draw game)
        (recur))))
  ;; Cleanup
  (when @render-target
    (rtl/unload-render-texture! @render-target))
  (rcw/close-window!))
```

`nrepl/start` runs first, before the window even opens, so you can
connect a REPL to a game that's still starting up. `(init)` does the
one-time setup (`init-window!`, config flags, and, in asteroids'
case, allocating the letterboxed render texture and calling
`debug-stats/enable!`). Then the loop: compute the next game state
(`tick`), check `window-should-close?`, and, while the window is
still open, commit the new state to `game-atom` and draw the frame,
before recurring. When the loop exits (the user closed the window),
asteroids releases its render texture and calls `close-window!`.

Most examples are a variation on this shape: start nREPL once, init
the window once, loop `update -> draw -> check-close` until the window
closes, then clean up. Simpler examples skip the parts specific to
asteroids (the render texture, the letterboxing) but follow the same
overall skeleton, except for the examples noted above, which skip
the nREPL step entirely.

## State as an atom

Asteroids keeps its entire game state in one atom,
[`game-atom`](https://github.com/b12n-oss/raylib-clj-demo/blob/main/asteroids/src/net/b12n/raylib_clj/scenes/asteroids.clj), seeded from
`initial-state`:

```clojure
(defn initial-state []
  {:dt 0
   :time (System/nanoTime)
   :time-acc [1]
   :frame-counter -1
   :screen :title
   :ship (make-ship WIDTH HEIGHT)
   :asteroids (map (fn [_] (make-asteroid)) (range INITIAL_ASTEROIDS))
   :bullets []
   :alive true})

(def game-atom (atom (initial-state)))
```

Ship, asteroids, bullets, and the current screen all live in this one
map. The `-main` loop above reads it, computes a new value with
`tick`, and `reset!`s it back; the atom is the single source of
truth for "what's happening right now."

The functions that compute the *next* state are pure (deterministic,
no game-state mutation) even where they lean on an FFI call
underneath. `vector-add` and `check-point-circle` are two that are testable
straight from a REPL:

```clojure
(defn vector-add [v1 v2]
  [(+ (v1 0) (v2 0))
   (+ (v1 1) (v2 1))])
```

`vector-add` is plain Clojure arithmetic; `check-point-circle`
delegates its actual geometry to `rcol/check-collision-point-circle?`
(an FFI-backed call) but is still deterministic and doesn't touch
`game-atom` or draw anything; you can call either at a REPL with
made-up arguments and get the same answer every time. The *draw*
phase is the opposite: `draw` calls
`rcd/begin-drawing!`, a sequence of raylib draw calls, and
`rcd/end-drawing!`; every one of those is a side effect (it writes
pixels to the screen), and calling `draw` twice with the same game
state does not give you back a value to compare, it paints a frame.
Keeping the state-update functions pure is what makes them REPL- and
test-friendly; the draw phase can't be, because rendering is
inherently a side effect.

## Plugging in `debug-stats`

[`src/net/b12n/raylib_clj/debug_stats.clj`](../../src/net/b12n/raylib_clj/debug_stats.clj) is an optional F1
overlay plugin. Its own docstring is the usage guide, verbatim:

```
Debug stats overlay plugin.

Usage:
1. Require this namespace in your game ns
2. Call (debug-stats/enable!) once at startup
3. Call (debug-stats/update!) in your game tick function
4. Call (debug-stats/draw!) at the end of your draw function (inside begin/end-drawing)
5. Press F1 to toggle the stats overlay

Example:
(ns my-game
  (:require [net.b12n.raylib-clj.debug-stats :as debug-stats]))

(defn init []
  (debug-stats/enable!))

(defn tick [game]
  (debug-stats/update!)
  ;; ... your game logic
  )

(defn draw [game]
  (rcd/begin-drawing!)
  ;; ... your drawing code
  (debug-stats/draw!)
  (rcd/end-drawing!))
```

The asteroids scene follows this exactly: `(debug-stats/enable!)` at the
end of `init`, `(debug-stats/update!)` in its tick function, and
`(debug-stats/draw!)` as the last call inside each
`begin-drawing!`/`end-drawing!` pair.

## Plugging in the embedded nREPL

[`src/net/b12n/raylib_clj/nrepl.clj`](../../src/net/b12n/raylib_clj/nrepl.clj) wraps
`nrepl.server/start-server`:

```clojure
(defn start
  "Start a network repl for debugging on specified port followed by
  an optional parameters map. The :bind, :transport-fn, :handler,
  :ack-port and :greeting-fn will be forwarded to
  nrepl.server/start-server as they are.

  If the port is already in use, logs a warning and returns nil
  instead of throwing - this allows games to still run when another
  nREPL server is already using the port."
  [{:keys [port bind transport-fn handler ack-port greeting-fn]}]
  (try
    (log/info "starting nREPL server on port" port)
    (nrepl/start-server :port port
                        :bind bind
                        :transport-fn transport-fn
                        :handler handler
                        :ack-port ack-port
                        :greeting-fn greeting-fn)

    (catch java.net.BindException e
      (log/warn (str "nREPL port " port " already in use - continuing without embedded nREPL. "
                     "You can connect to the existing nREPL server if one is running."))
      nil)
    (catch Throwable t
      (log/error t "failed to start nREPL")
      (throw t))))
```

Called once in `-main` as `(nrepl/start {:port 7888})`. The
`BindException` catch is what makes port 7888 safe to reuse: if
another game (or another instance of the same one) is already
listening there, `start` logs a warning and returns `nil` instead of
crashing; the second game still runs, it just doesn't get its own
nREPL server. Any other exception during startup is logged and
re-thrown.

## Porting a new raylib C example

New examples go in
[raylib-clj-demo](https://github.com/b12n-oss/raylib-clj-demo), where each one
is its own project: a directory named after the example, holding a `deps.edn`
and `src/net/b12n/raylib_clj/scenes/<name>.clj`. The header of that repo's
`bb.edn` lists the tasks (`bb gen` rebuilds the generated files, `bb check`
compile-checks every scene). The recipe, as a numbered list:

1. Find the C source in raylib's
   [`examples/`](https://github.com/raysan5/raylib/tree/master/examples)
   tree.
2. Create the project directory in raylib-clj-demo and write the scene
   following the shared skeleton above. Name the C original in the namespace
   docstring (`Based on: shapes/shapes_bouncing_ball.c`).
3. Register it in that repo's `demos.edn`, then run `bb gen`.
4. Run `bb <name>` to see it in a real window.

## See also
- [`repl-workflow.md`](repl-workflow.md): live development against a game's
  embedded nREPL
