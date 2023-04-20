package de.flapdoodle.kfx

class Registration(private val action: () -> Unit) {
  fun remove() {
    action.invoke()
  }

  fun and(vararg registrations: Registration): Registration {
    return Registration {
      this.remove()
      registrations.forEach(Registration::remove)
    }
  }
}
