import Foundation

struct GetLogsResponse: Decodable {
    let time: Date
    let level: String
    let message: String
}
