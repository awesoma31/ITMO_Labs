import Alamofire
import Foundation

protocol ProfitServiceProtocol {
    func calculateProfit(
        accessToken: String,
        userId: String,
        from: Date?,
        to: Date?,
        completion: @escaping (Result<ProfitStatsResponse, Error>) -> Void
    )
}

final class ProfitService: ProfitServiceProtocol {

    private let baseURL: String = "https://cryptoterm.ru"
    private let session: Session = .default

    // MARK: /api/users/{user_id}/profit
    func calculateProfit(
        accessToken: String,
        userId: String,
        from: Date? = nil,
        to: Date? = nil,
        completion: @escaping (Result<ProfitStatsResponse, Error>) -> Void
    ) {
        let url = "\(baseURL)/api/users/\(userId)/profit"
        var params: [String: String] = [:]

        let dateFormatter = ISO8601DateFormatter()
        dateFormatter.timeZone = TimeZone(secondsFromGMT: 0)

        if let fromDate = from {
            params["from"] = dateFormatter.string(from: fromDate)
        }

        if let toDate = to {
            params["to"] = dateFormatter.string(from: toDate)
        }

        let headers: HTTPHeaders = [
            .authorization(bearerToken: accessToken),
            .accept("application/json")
        ]

        session.request(
            url,
            method: .get,
            parameters: params,
            encoder: URLEncodedFormParameterEncoder(destination: .queryString),
            headers: headers
        )
        .validate(statusCode: 200..<300)
        .responseDecodable(of: ProfitStatsResponse.self, decoder: APIJSONDecoder.shared) { response in
            switch response.result {
            case .success(let response):
                completion(.success(response))
            case .failure(let error):
                completion(.failure(error))
            }
        }
    }
}
