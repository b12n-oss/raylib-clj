# Contributing

Thanks for taking an interest. This is a set of Clojure bindings for
[raylib](https://github.com/raysan5/raylib), calling the real `libraylib` over
its C ABI through [coffi](https://github.com/IGJoshua/coffi) and JDK 22+'s
Foreign Function & Memory API.

The example programs live in
[raylib-clj-demo](https://github.com/b12n-oss/raylib-clj-demo). Send new
examples there. This repo takes binding fixes and new bindings.

## Setting up

You need a JDK 22 or newer; everything else is optional.

```sh
java -version     # must be 22+, for the Foreign Function & Memory API
clojure --version # Clojure CLI
bb --version      # babashka, optional, for the task runner
```

You do **not** need to install raylib. Prebuilt 6.0 binaries for macOS,
Linux, and Windows ship under `libs/` and are selected by OS/arch at load time
(see `src/net/b12n/raylib_clj/core.clj`). If you'd rather link a system raylib, put it
anywhere on the `-Djava.library.path` list in `deps.edn`.

On macOS, Gatekeeper may quarantine the bundled dylib. If a run dies on a
signature error:

```sh
codesign --force --sign - libs/macos/libraylib.6.0.0.dylib
```

## Before you open a PR

Run the gate. It is fast and opens no window:

```sh
bb check          # compile every namespace under src/, then clj-kondo
```

Step 1 requires all 30 namespaces; this is the check that catches a binding
namespace whose requires are broken, which is the easiest mistake to make and
the one a REPL session that loaded only one namespace will not reveal.

Step 2 is clj-kondo. **It must report 0 errors.** Warnings do not fail the
gate (there is 1 today), but don't add to them.

If clj-kondo reports an `Unresolved var` for something you know exists, that
is a bug in `.clj-kondo/hooks/raylib_ffi.clj`, not in your code; please say so
in the PR rather than working around it. That hook is what teaches clj-kondo to
see through `coffi.ffi/defcfn`; without it every `defcfn` use
reports as an unresolved var.

## Adding an example

Examples are in [raylib-clj-demo](https://github.com/b12n-oss/raylib-clj-demo),
one project per example, and its README has the recipe. For how a raylib-clj
game is structured, read
[docs/guide/example-architecture-patterns.md](docs/guide/example-architecture-patterns.md).

Two conventions worth knowing before you write any code here:

- **Side-effecting functions end in `!`, predicates in `?`.** The binding layer
  is consistent about this and the callers read much better for it.
- **Write against `src/net/b12n/raylib_clj/`, not raw coffi.** Add a new
  `defcfn` there in the namespace matching raylib's own module split
  (`core/`, `shapes/`, `text/`, `textures/`).

## Adding an FFI binding

If you're touching the binding layer, read
[`docs/guide/adding-ffi-bindings.md`](docs/guide/adding-ffi-bindings.md) first.
It has the C-to-coffi type table and, importantly, the recipe for pointer
in/out parameters; raylib passes several structs that way and the arena
handling is not obvious.

[`docs/guide/coffi-panama-internals.md`](docs/guide/coffi-panama-internals.md)
covers what actually happens under `defcfn`, if you want the layer below that.

## Licensing

This project is released under the **Eclipse Public License 2.0**. That is
inherited, not chosen: it began as
[ertugrulcetin/raylib-clojure-playground](https://github.com/ertugrulcetin/raylib-clojure-playground),
which declares EPL-2.0, and EPL-2.0 is copyleft at the file level. By
contributing, you agree your contribution is licensed under those terms.

Examples ported from raylib carry their upstream zlib/libpng origin, noted per
example in raylib-clj-demo; that does not change this project's own license.
Media assets under `resources/` also moved there, and they are not covered by
the project's license. Each carries its own terms, listed in that repo's
`resources/LICENSE.md`.
