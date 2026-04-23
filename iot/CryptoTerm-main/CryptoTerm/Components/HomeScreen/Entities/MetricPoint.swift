import Foundation

struct MetricPoint: Identifiable, Decodable, Equatable {
    let id: UUID = UUID()
    let time: Date
    let value: Double

    private enum CodingKeys: String, CodingKey {
        case time, value
    }
}
