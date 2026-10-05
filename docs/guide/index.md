# raylib-clj Guide

User-facing documentation for `raylib-clj`: **Clojure** bindings for
**[raylib](https://www.raylib.com/)**, calling raylib's C library directly via
**[coffi](https://github.com/IGJoshua/coffi)** over JDK 22+'s Foreign
Function & Memory API (Project Panama). No wrapper library, no codegen:
`coffi`'s `defcfn` binds each raylib C function directly.

## Why this exists

One idea (raylib examples that reach the C library
directly, with no wrapper layer in between) explored on three Clojure
runtimes, one repo each. The examples for this one live in
[raylib-clj-demo](https://github.com/b12n-oss/raylib-clj-demo).

This is the JVM one: JDK 22+'s Panama Foreign Function & Memory API via
`coffi`, where a binding is a `defcfn` form and a C struct arrives as a
plain Clojure map. [`raylib-jlt`](https://github.com/jlt-commons/raylib-jlt)
does it on Chez Scheme through jolt's `jolt.ffi`, with no JVM at all.
[`raylib-jnk`](https://github.com/b12n-oss/raylib-jnk) does it in
jank, which compiles through C++/LLVM to a native binary and so has no FFI
layer to speak of; it includes `raylib.h` and calls the C++ directly.

Reading them side by side is the interesting part: the same example, drawn
three ways, shows exactly where each runtime puts the boundary. The pages
below cover the JVM/Panama side: what `defcfn` actually does, how structs
and pointers cross, and how to add a new binding.

## What raylib-clj is

A `.clj` (JVM Clojure) project:

```clojure
(require '[net.b12n.raylib-clj.core.window :as rcw]
         '[net.b12n.raylib-clj.core.drawing :as rcd]
         '[net.b12n.raylib-clj.colors :as colors])

(rcw/init-window! 800 450 "Hello")
(loop []
  (when-not (rcw/window-should-close?)
    (rcd/begin-drawing!)
    (rcd/clear-background! colors/raywhite)
    (rcd/end-drawing!)
    (recur)))
(rcw/close-window!)
```

The FFI bindings are in `src/net/b12n/raylib_clj/`. The 113 example
programs built on them, a mix of original games and ports of official raylib C
examples, are in
[raylib-clj-demo](https://github.com/b12n-oss/raylib-clj-demo), one project per
example with its own animated GIF.

## Pages

### Orientation
- [`getting-started.md`](getting-started.md): install JDK 22+, the
  Clojure CLI, Babashka; IDE setup
- [`architecture.md`](architecture.md): module layout, the FFI/native
  library flow, bundled libraries

### FFI internals
- [`adding-ffi-bindings.md`](adding-ffi-bindings.md): `defcfn`/`defalias`,
  the C-to-coffi type table, pointer in/out params, a worked example
- [`coffi-panama-internals.md`](coffi-panama-internals.md): what
  happens under `defcfn` on the JDK Panama FFI, memory arenas, why
  JDK 22+

### Working with games
- [`example-architecture-patterns.md`](example-architecture-patterns.md):
  how a raylib-clj game is structured: state-as-atom, `debug-stats`/embedded
  nREPL integration, the porting recipe. The examples are in raylib-clj-demo.
- [`repl-workflow.md`](repl-workflow.md): embedded vs standalone REPL,
  live game development

### Support
- [`troubleshooting.md`](troubleshooting.md): common errors and fixes

## See also

The same idea on the other two Clojure runtimes:

- [`raylib-jlt`](https://github.com/jlt-commons/raylib-jlt): in Jolt
  (native Clojure on Chez Scheme, no JVM), over `jolt.ffi`. It moved to the
  jlt-commons organization, and its docs with it.
  [jlt-commons.github.io/raylib-jlt](https://jlt-commons.github.io/raylib-jlt/)
- [`raylib-jnk`](https://github.com/b12n-oss/raylib-jnk): in jank
  (native Clojure via C++/LLVM), calling raylib as ordinary C++ through
  `(:include "raylib.h")`, no FFI layer at all.
  [b12n-oss.github.io/raylib-jnk](https://b12n-oss.github.io/raylib-jnk/)
