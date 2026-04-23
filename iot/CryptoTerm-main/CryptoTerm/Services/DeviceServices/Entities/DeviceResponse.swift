import Foundation

struct DeviceResponse: Decodable {
    let id: String
    let name: String
    let ipAddress: String
    let registeredAt: Date
    let minerIds: [String]

    enum CodingKeys: String, CodingKey {
        case id = "id"
        case name = "name"
        case ipAddress = "ip_address"
        case registeredAt = "registered_at"
        case minerIds = "miner_ids"
    }
}
