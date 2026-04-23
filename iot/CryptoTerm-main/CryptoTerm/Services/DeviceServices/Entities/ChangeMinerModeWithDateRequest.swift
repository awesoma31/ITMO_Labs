import Foundation

struct ChangeMinerModeWithDateRequest: Encodable {
    let rpId: String
    let asicId: String
    let commandId: String
    let scheduledAt: String

    init(rpId: String, asicId: String, commandId: String, scheduledAt: Date) {
        self.rpId = rpId
        self.asicId = asicId
        self.commandId = commandId
        self.scheduledAt = APIFormatters.iso8601ms.string(from: scheduledAt)
    }
}

struct NewChangeMinerModeWithDateRequest: Encodable {
    let mode: String
    let scheduledAt: String
    let powerWatts: Int?
    let hashrate: Int?

    init(mode: String, scheduledAt: Date, powerWatts: Int?, hashrate: Int?) {
        self.mode = mode
        self.scheduledAt = APIFormatters.iso8601ms.string(from: scheduledAt)
        self.powerWatts = powerWatts
        self.hashrate = hashrate
    }

    enum CodingKeys: String, CodingKey {
        case mode = "mode"
        case scheduledAt = "scheduled_at"
        case powerWatts = "power_watts"
        case hashrate = "hashrate"
    }
}

fileprivate enum APIFormatters {
    static let iso8601ms: ISO8601DateFormatter = {
        let f = ISO8601DateFormatter()
        f.timeZone = TimeZone(secondsFromGMT: 0)
        f.formatOptions = [.withInternetDateTime, .withFractionalSeconds]
        return f
    }()
}
