package dev.sixdev.sghost.esp

class PairingFailedException(val code: Int) : Exception("Pairing failed with HTTP code: $code")
class EmptyBodyException : Exception("The server returned an empty response.")
class BadJsonException : Exception("Failed to parse JSON from server response.")
class WifiConfigFailedException(message: String, cause: Throwable? = null) : Exception(message, cause)
