struct ChangeMinerModeRequest: Encodable {
    let rpId: String
    let asicId: String
    let mode: String
}

struct NewChangeMinerModeRequest: Encodable {
    let mode: String
    let powerWatts: Int?
    let hashrate: Int?
}
