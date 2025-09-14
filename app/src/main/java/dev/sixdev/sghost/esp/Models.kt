package dev.sixdev.sghost.esp

data class PairStartReq(val masterPassword: String, val appPublicKey: String)
data class PairStartRes(val nodeId: String, val espPublicKey: String)
data class WifiConfigReq(val ssid: String, val pass: String)
data class UploadReq(
    val toPeerId: String,
    val fromId: String,
    val mediaKind: String,
    val createdAt: Long,
    val blobB64: String
)
data class PullResItem(
    val fromId: String,
    val mediaKind: String,
    val createdAt: Long,
    val blobB64: String
)
