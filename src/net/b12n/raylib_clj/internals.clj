(ns net.b12n.raylib-clj.internals
  (:require [coffi.mem :as mem]))

;; ubyte
(defmethod mem/primitive-type ::ubyte
  [_type]
  ::mem/byte)

(defmethod mem/serialize* ::ubyte
  [obj _type _scope]
  (unchecked-byte obj))

(defmethod mem/deserialize* ::ubyte
  [obj _type]
  (Byte/toUnsignedLong obj))

;; bool
(defmethod mem/primitive-type ::bool
  [_type]
  ::mem/byte)

(defmethod mem/serialize* ::bool
  [obj _type _scope]
  (byte (if obj 1 0)))

(defmethod mem/deserialize* ::bool
  [obj _type]
  (not (zero? obj)))
