# in, out, reified: A Practical Guide to Kotlin Generics

**Building a simple event bus to understand variance, once and for all**

---

### Introduction

Kotlin's standard library is full of generic signatures — `List<out E>`, `Comparable<in T>`, `Sequence<out T>`. The `in` and `out` keywords appear constantly, but what do they actually mean? And when does a regular type parameter need one of these modifiers?

These questions led me to dig deeper into Kotlin's [variance system](https://kotlinlang.org/docs/generics.html). Despite years of working with generics daily, the underlying mechanics had remained somewhat fuzzy. Fixing that turned out to require just the right example.

This post builds a simple event bus from scratch. The example covers all three variance types (invariant, covariant, and contravariant) in a way that maps directly to real-world usage. A GitHub Gist with the full implementation is linked at the end.

---

### The Mental Model

Before diving into code, it helps to establish what variance actually controls: the direction of type substitution.

**`out T` (Covariance):** The class only produces `T`, never consumes it. This makes it safe to substitute a subtype. A function returning `List<out Animal>` can return a `List<Dog>` because reading a Dog as an Animal works fine. The direction flows from specific to general: subtype → supertype.

**`in T` (Contravariance):** The class only consumes `T`, never produces it. This makes it safe to substitute a supertype. A function accepting `Comparable<in String>` can accept `Comparable<Any>` because a comparator that handles any object can certainly handle strings. The direction flows from general to specific: supertype → subtype.

**`T` (Invariant):** The class both produces and consumes `T`. No substitution is safe in either direction. `MutableList<T>` is invariant because you both read from and write to it.

The compiler enforces these constraints to prevent runtime `ClassCastException` errors. Java developers might recognize this pattern as PECS (Producer Extends, Consumer Super), a mnemonic introduced by Joshua Bloch in [Effective Java](https://www.oreilly.com/library/view/effective-java/9780134686097/) (Item 31). Kotlin's `in` and `out` keywords make the same concept explicit and readable.

---

### Why Event Bus

An event bus is a familiar pattern for most developers. At its core, it allows publishers to send events and subscribers to receive them. This natural separation of producers and consumers makes it an ideal example for exploring variance.

The implementation in this post is intentionally minimal: single-threaded, no external dependencies, pure Kotlin stdlib. Each operation maps to a different variance type: the bus itself is invariant, subscribing demonstrates covariance, and publishing demonstrates contravariance.

---

### Building the Bus

Let's build the event bus piece by piece, examining how each component uses variance differently.

**The Invariant Container**

```kotlin
class EventBus<T> {
    private val subscribers = mutableMapOf<String, (T) -> Unit>()
}
```

The `EventBus<T>` class has no variance modifier. It stores handlers that accept `T` and later invokes them with events of type `T`. Because it both writes to the map and reads from it, the type parameter must remain invariant.

**Covariant Output**

```kotlin
interface Subscription<out T> {
    val eventType: Class<T>
    fun unsubscribe()
}
```

The `Subscription<out T>` interface only produces information. It never accepts a `T` as input. The `out` modifier makes it safe to treat a `Subscription<Dog>` as a `Subscription<Animal>`.

**Contravariant Input**

```kotlin
interface EventConsumer<in T> {
    fun onEvent(event: T)
}
```

The `EventConsumer<in T>` interface only consumes events. It never returns a `T`. The `in` modifier makes it safe to pass an `EventConsumer<Animal>` where an `EventConsumer<Dog>` is expected. If it can handle any animal, it can certainly handle a dog.

**Putting It Together**

```kotlin
class EventBus<T>(private val eventType: Class<T>) {
    private val subscribers = mutableMapOf<String, EventConsumer<T>>()

    fun subscribe(consumer: EventConsumer<T>): Subscription<T> {
        val id = UUID.randomUUID().toString()
        subscribers[id] = consumer
        return object : Subscription<T> {
            override val eventType: Class<T> = this@EventBus.eventType
            override fun unsubscribe() {
                subscribers.remove(id)
            }
        }
    }

    fun publish(event: T) {
        subscribers.values.forEach { it.onEvent(event) }
    }
}
```

Three variance types, one cohesive example:

- `EventBus<T>` is invariant (stores and invokes)
- `Subscription<out T>` is covariant (only produces)
- `EventConsumer<in T>` is contravariant (only consumes)

---

### Star Projection

Sometimes the specific type parameter doesn't matter. Kotlin provides star projection for these cases:

```kotlin
fun unsubscribeAll(subscriptions: List<Subscription<*>>) {
    subscriptions.forEach { it.unsubscribe() }
}
```

`Subscription<*>` means "a Subscription of some type, but I don't know or care which." This appears frequently when managing heterogeneous collections. A subscription manager might hold `Subscription<UserEvent>`, `Subscription<NetworkEvent>`, and others. Unsubscribing doesn't require knowing the event type.

The tradeoff: you can read from a star-projected type as `Any?`, but you cannot safely write to it. For read purposes, `<*>` is equivalent to `<out Any?>`. Star projection trades type specificity for flexibility.

---

### The Reified Unlock

Generic type parameters are erased at runtime. This means you can't normally inspect or use `T` as a class reference inside a function. Kotlin's `reified` keyword solves this, but it requires the function to be `inline` (the compiler inlines the call site, preserving type information).

```kotlin
inline fun <reified T : Any> subscribe(callback: (T) -> Unit): Subscription<T> {
    println("Subscribing to: ${T::class.simpleName}")
    // T::class is now available at runtime
}
```

The `reified` modifier makes `T` available at runtime. No need to pass a `Class<T>` parameter explicitly.

This pattern appears throughout Kotlin libraries:

```kotlin
// Android
inline fun <reified T : Parcelable> Intent.getParcelableExtra(name: String): T?

// Kotlin Serialization
inline fun <reified T> Json.decodeFromString(string: String): T

// Koin DI
inline fun <reified T : Any> get(): T
```

Any API that needs runtime access to a generic type benefits from `reified`.

---

### Summary

The one rule: if you only output `T`, use `out`. If you only input `T`, use `in`. If you do both, leave it alone.

<!-- Replace this with the generated cheat sheet image -->
| Modifier | Role | Safe Substitution | Example |
|----------|------|-------------------|---------|
| `out T` | Producer | Subtype → Supertype | `List<out T>` |
| `in T` | Consumer | Supertype → Subtype | `Comparable<in T>` |
| `T` | Both | None | `MutableList<T>` |
| `*` | Unknown | Read as `Any?` | `Class<*>` |

The complete EventBus implementation is available as a [GitHub Gist](YOUR_GIST_LINK).

Understanding these concepts makes consuming APIs easier, compiler errors less mysterious, and opens the door to writing more flexible code.
