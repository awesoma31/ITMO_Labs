import Foundation

struct AsicCommandResponse: Codable {
    let asic: AsicConfig
    let steps: [CommandStep]
    let policy: RetryPolicy

    let signature: JSONValue?
    let status: CommandStatus
    let result: JSONValue?

    let cmdId: UUID
    let deviceId: UUID

    let createdAt: Date
    let updatedAt: Date
    let executedAt: Date?
    let scheduledAt: Date?
    let powerMode: String

    enum CodingKeys: String, CodingKey {
        case asic, steps, policy, signature, status, result
        case cmdId = "cmd_id"
        case deviceId = "device_id"
        case createdAt = "created_at"
        case updatedAt = "updated_at"
        case executedAt = "executed_at"
        case scheduledAt = "scheduled_at"
        case powerMode = "power_mode"
    }
}

struct AsicConfig: Codable {
    let firmware: String
    let port: Int
    let scheme: String
}

struct CommandStep: Codable {
    let id: String
    let request: StepRequest
    let extract: [String: String]?
}

struct StepRequest: Codable {
    let method: HTTPMethod
    let path: String
    let headers: [String: String]
    let body: JSONValue
    let timeoutMs: Int

    enum CodingKeys: String, CodingKey {
        case method, path, headers, body
        case timeoutMs = "timeout_ms"
    }
}

struct RetryPolicy: Codable {
    let maxRetries: Int
    let retryDelayMs: Int

    enum CodingKeys: String, CodingKey {
        case maxRetries = "max_retries"
        case retryDelayMs = "retry_delay_ms"
    }
}

struct AsicCommandRequest: Codable {
    let status: String
}

enum CommandStatus: String, Codable {
    case pending = "PENDING"
    case scheduled = "SCHEDULED"
    case sent = "SENT"
    case executing = "EXECUTING"
    case success = "SUCCESS"
    case failed = "FAILED"
    case canceled = "CANCELED"
    case unknown

    init(from decoder: Decoder) throws {
        let raw = try decoder.singleValueContainer().decode(String.self)
        self = CommandStatus(rawValue: raw) ?? .unknown
    }
}

enum HTTPMethod: String, Codable {
    case get = "GET"
    case post = "POST"
    case put = "PUT"
    case patch = "PATCH"
    case delete = "DELETE"
}

enum JSONValue: Codable, Equatable {
    case null
    case bool(Bool)
    case int(Int)
    case double(Double)
    case string(String)
    case array([JSONValue])
    case object([String: JSONValue])

    init(from decoder: Decoder) throws {
        let c = try decoder.singleValueContainer()

        if c.decodeNil() { self = .null; return }
        if let v = try? c.decode(Bool.self) { self = .bool(v); return }
        if let v = try? c.decode(Int.self) { self = .int(v); return }
        if let v = try? c.decode(Double.self) { self = .double(v); return }
        if let v = try? c.decode(String.self) { self = .string(v); return }
        if let v = try? c.decode([JSONValue].self) { self = .array(v); return }
        if let v = try? c.decode([String: JSONValue].self) { self = .object(v); return }

        throw DecodingError.typeMismatch(
            JSONValue.self,
            .init(codingPath: decoder.codingPath, debugDescription: "Unsupported JSON value")
        )
    }

    func encode(to encoder: Encoder) throws {
        var c = encoder.singleValueContainer()
        switch self {
        case .null: try c.encodeNil()
        case .bool(let v): try c.encode(v)
        case .int(let v): try c.encode(v)
        case .double(let v): try c.encode(v)
        case .string(let v): try c.encode(v)
        case .array(let v): try c.encode(v)
        case .object(let v): try c.encode(v)
        }
    }
}

extension JSONDecoder {
    static func asicCommandDecoder() -> JSONDecoder {
        let d = JSONDecoder()
        d.dateDecodingStrategy = .custom { decoder in
            let s = try decoder.singleValueContainer().decode(String.self)

            let f1 = ISO8601DateFormatter()
            f1.formatOptions = [.withInternetDateTime, .withFractionalSeconds]
            if let date = f1.date(from: s) { return date }

            let f2 = ISO8601DateFormatter()
            f2.formatOptions = [.withInternetDateTime]
            if let date = f2.date(from: s) { return date }

            throw DecodingError.dataCorruptedError(
                in: try decoder.singleValueContainer(),
                debugDescription: "Invalid ISO8601 date: \(s)"
            )
        }
        return d
    }
}
