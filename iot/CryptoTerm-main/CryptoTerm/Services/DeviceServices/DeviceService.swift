import Foundation
import Alamofire

protocol DeviceServiceProtocol {
    func getUserDevices(
        accessToken: String,
        userId: String,
        completion: @escaping (Result<[DeviceResponse], Error>) -> Void
    )

    func changeDeviceMode(
        accessToken: String,
        minerId: String,
        mode: String,
        completion: @escaping (Result<Void, Error>) -> Void
    )

    func scheduleRegimeChange(
        accessToken: String,
        minerId: String,
        mode: String,
        scheduledAt: Date,
        completion: @escaping (Result<Void, Error>) -> Void
    )
}

final class DeviceService: DeviceServiceProtocol {

    private let baseURL: String = "https://cryptoterm.ru"
    private let session: Session = .default

    // MARK: /api/users/{userId}/devices/detailed
    func getUserDevices(
        accessToken: String,
        userId: String,
        completion: @escaping (Result<[DeviceResponse], Error>) -> Void
    ) {
        let url = "\(baseURL)/api/users/\(userId)/devices/detailed"

        let headers: HTTPHeaders = [
            .authorization(bearerToken: accessToken),
            .accept("application/json")
        ]

        session.request(
            url,
            method: .get,
            headers: headers
        )
        .validate(statusCode: 200..<300)
        .responseDecodable(of: [DeviceResponse].self, decoder: APIJSONDecoder.shared) { response in
            switch response.result {
            case .success(let devices):
                completion(.success(devices))
            case .failure(let error):
                completion(.failure(error))
            }
        }
    }

    // MARK: /api/v1/miners/{minerId}/change-mode
    func changeDeviceMode(
        accessToken: String,
        minerId: String,
        mode: String,
        completion: @escaping (Result<Void, Error>) -> Void
    ) {
        let url = "\(baseURL)/api/v1/miners/\(minerId)/change-mode"
        let params = NewChangeMinerModeRequest(mode: mode, powerWatts: nil, hashrate: nil)

        let headers: HTTPHeaders = [
            .authorization(bearerToken: accessToken),
            .contentType("application/json"),
            .accept("application/json")
        ]

        session.request(
            url,
            method: .post,
            parameters: params,
            encoder: JSONParameterEncoder.default,
            headers: headers
        )
        .validate(statusCode: 200..<300)
        .response { response in
            switch response.result {
            case .success:
                completion(.success(()))
            case .failure(let error):
                completion(.failure(error))
            }
        }
    }

    // MARK: /api/v1/miners/{minerId}/schedule-change-mode
    func scheduleRegimeChange(
        accessToken: String,
        minerId: String,
        mode: String,
        scheduledAt: Date,
        completion: @escaping (Result<Void, Error>) -> Void
    ) {
        let url = "\(baseURL)/api/v1/miners/\(minerId)/schedule-change-mode"
        let params = NewChangeMinerModeWithDateRequest(
            mode: mode,
            scheduledAt: scheduledAt,
            powerWatts: nil,
            hashrate: nil
        )

        let headers: HTTPHeaders = [
            .authorization(bearerToken: accessToken),
            .accept("application/json"),
            .contentType("application/json")
        ]

        session.request(
            url,
            method: .post,
            parameters: params,
            encoder: JSONParameterEncoder.default,
            headers: headers
        )
        .validate(statusCode: 200..<300)
        .response { response in
            switch response.result {
            case .success:
                completion(.success(()))
            case .failure(let error):
                completion(.failure(error))
            }
        }
    }
}
