import Foundation
import Alamofire

protocol MetricServiceProtocol {
    func fetchHashrate(
        accessToken: String,
        deviceId: String,
        from: Date?,
        to: Date?,
        bucket: String?,
        completion: @escaping (Result<[MetricPoint], Error>) -> Void
    )

    func fetchTemperature(
        accessToken: String,
        deviceId: String,
        from: Date?,
        to: Date?,
        bucket: String?,
        completion: @escaping (Result<[MetricPoint], Error>) -> Void
    )

    func fetchPowerConsumption(
        accessToken: String,
        deviceId: String,
        from: Date?,
        to: Date?,
        bucket: String?,
        completion: @escaping (Result<[MetricPoint], Error>) -> Void
    )
}

final class MetricService: MetricServiceProtocol {

    private let baseURL: String = "https://cryptoterm.ru"
    private let session: Session = .default

    // MARK: /api/metrics/hashrate/device/{deviceId}
    func fetchHashrate(
        accessToken: String,
        deviceId: String,
        from: Date? = nil,
        to: Date? = nil,
        bucket: String? = "1 hour",
        completion: @escaping (Result<[MetricPoint], Error>) -> Void
    ) {
        let url = "\(baseURL)/api/metrics/hashrate/device/\(deviceId)"
        request(
            requestUrl: url,
            accessToken: accessToken,
            from: from,
            to: to,
            bucket: bucket,
            completion: completion
        )
    }

    // MARK: /api/metrics/temperature/device/{deviceId}
    func fetchTemperature(
        accessToken: String,
        deviceId: String,
        from: Date? = nil,
        to: Date? = nil,
        bucket: String? = "1 hour",
        completion: @escaping (Result<[MetricPoint], Error>) -> Void
    ) {
        let url = "\(baseURL)/api/metrics/temperature/device/\(deviceId)"
        request(
            requestUrl: url,
            accessToken: accessToken,
            from: from,
            to: to,
            bucket: bucket,
            completion: completion
        )
    }

    // MARK: /api/metrics/power-consumption/device/{device_id}
    func fetchPowerConsumption(
        accessToken: String,
        deviceId: String,
        from: Date? = nil,
        to: Date? = nil,
        bucket: String? = "1 hour",
        completion: @escaping (Result<[MetricPoint], Error>) -> Void
    ) {
        let url = "\(baseURL)/api/metrics/power-consumption/device/\(deviceId)"
        request(
            requestUrl: url,
            accessToken: accessToken,
            from: from,
            to: to,
            bucket: bucket,
            completion: completion
        )
    }

    private func request(
        requestUrl: String,
        accessToken: String,
        from: Date? = nil,
        to: Date? = nil,
        bucket: String? = "1 hour",
        completion: @escaping (Result<[MetricPoint], Error>) -> Void
    ) {
        var params: [String: String] = [:]

        let dateFormatter = ISO8601DateFormatter()
        dateFormatter.timeZone = TimeZone(secondsFromGMT: 0)

        if let fromDate = from {
            params["from"] = dateFormatter.string(from: fromDate)
        }

        if let toDate = to {
            params["to"] = dateFormatter.string(from: toDate)
        }

        if let bucket = bucket {
            params["bucket"] = bucket
        }

        let headers: HTTPHeaders = [
            .authorization(bearerToken: accessToken),
            .accept("application/json")
        ]

        session.request(
            requestUrl,
            method: .get,
            parameters: params,
            encoder: URLEncodedFormParameterEncoder(destination: .queryString),
            headers: headers
        )
        .validate(statusCode: 200..<300)
        .responseDecodable(of: [MetricPoint].self, decoder: APIJSONDecoder.shared) { response in
            switch response.result {
            case .success(let data):
                completion(.success(data))
            case .failure(let error):
                completion(.failure(error))
            }
        }
    }
}
