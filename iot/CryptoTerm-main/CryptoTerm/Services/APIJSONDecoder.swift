import Foundation

enum APIJSONDecoder {
    static let shared: JSONDecoder = {
        let d = JSONDecoder()

        d.dateDecodingStrategy = .custom { decoder in
            let container = try decoder.singleValueContainer()
            let str = try container.decode(String.self)

            let moscowTimeZone = TimeZone(identifier: "Europe/Moscow") ?? TimeZone.current

            let f1 = ISO8601DateFormatter()
            f1.timeZone = moscowTimeZone

            if let date = f1.date(from: str) { return date }

            let f2 = ISO8601DateFormatter()
            f2.formatOptions = [.withInternetDateTime, .withFractionalSeconds]
            f2.timeZone = moscowTimeZone

            if let date = f2.date(from: str) { return date }

            throw DecodingError.dataCorruptedError(
                in: container,
                debugDescription: "Invalid ISO8601 date: \(str)"
            )
        }

        return d
    }()
}
