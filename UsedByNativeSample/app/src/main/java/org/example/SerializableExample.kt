package org.example

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

class CustomSerializableData(var payload: String) : Serializable {

    @Transient
    var isCustomDeserialized: Boolean = false

    private fun writeObject(out: ObjectOutputStream) {
        out.defaultWriteObject()
    }

    private fun readObject(`in`: ObjectInputStream) {
        `in`.defaultReadObject()
        // If this method is stripped/renamed by R8, this flag remains false
        isCustomDeserialized = true
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}

object SerializableTester {
    fun testSerialization() {
        val original = CustomSerializableData("SecretPayload")
        
        // Serialize
        val baos = ByteArrayOutputStream()
        val oos = ObjectOutputStream(baos)
        oos.writeObject(original)
        oos.close()

        // Deserialize
        val bais = ByteArrayInputStream(baos.toByteArray())
        val ois = ObjectInputStream(bais)
        val deserialized = ois.readObject() as CustomSerializableData
        ois.close()

        // Verify that custom readObject was called.
        // If the keep rule is missing, R8 will strip/rename readObject,
        // and Java serialization will silently fall back to default deserialization,
        // leaving isCustomDeserialized as false!
        if (!deserialized.isCustomDeserialized) {
            throw RuntimeException("CRASH: Custom readObject was not called! Keep rule for Serializable is missing or failing.")
        }
    }
}
