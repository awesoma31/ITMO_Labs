import Foundation

struct GetLogsRequest: Encodable {
    let deviceId: String
    let from: Date?
    let to: Date?

    enum CodingKeys: String, CodingKey {
        case deviceId = "device_id"
        case from = "from"
        case to = "to"
    }
}
