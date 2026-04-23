import Foundation
import Alamofire

protocol AuthServiceProtocol {
    func register(
        username: String,
        email: String,
        password: String,
        telegram: String?,
        completion: @escaping (Result<AuthResponse, Error>) -> Void
    )

    func login(
        usernameOrEmail: String,
        password: String,
        completion: @escaping (Result<AuthResponse, Error>) -> Void
    )

    func refresh(
        refreshToken: String,
        completion: @escaping (Result<AuthResponse, Error>) -> Void
    )

    func logout(
        refreshToken: String,
        completion: @escaping (Result<Void, Error>) -> Void
    )
}

final class AuthService: AuthServiceProtocol {

    private let baseURL: String = "https://cryptoterm.ru"
    private let session: Session = Session.default

    // MARK: /api/auth/register
    func register(
        username: String,
        email: String,
        password: String,
        telegram: String? = nil,
        completion: @escaping (Result<AuthResponse, Error>) -> Void
    ) {
        let url = "\(baseURL)/api/auth/register"
        let params = RegisterRequest(username: username, email: email, password: password, telegram: telegram)

        let headers: HTTPHeaders = [
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
        .responseDecodable(of: AuthResponse.self) { response in
            switch response.result {
            case .success(let auth):
                completion(.success(auth))
            case .failure:
                completion(.failure(Self.mapError(response)))
            }
        }
    }

    // MARK: /api/auth/login
    func login(
        usernameOrEmail: String,
        password: String,
        completion: @escaping (Result<AuthResponse, Error>) -> Void
    ) {
        let url = "\(baseURL)/api/auth/login"
        let params = LoginRequest(usernameOrEmail: usernameOrEmail, password: password)

        let headers: HTTPHeaders = [
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
        .responseDecodable(of: AuthResponse.self) { response in
            switch response.result {
            case .success(let auth):
                completion(.success(auth))
            case .failure:
                completion(.failure(Self.mapError(response)))
            }
        }
    }

    // MARK: /api/auth/refresh
    func refresh(
        refreshToken: String,
        completion: @escaping (Result<AuthResponse, Error>) -> Void
    ) {
        let url = "\(baseURL)/api/auth/refresh"
        let params = RefreshTokenRequest(refreshToken: refreshToken)

        let headers: HTTPHeaders = [
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
        .responseDecodable(of: AuthResponse.self) { response in
            switch response.result {
            case .success(let auth):
                completion(.success(auth))
            case .failure:
                completion(.failure(Self.mapError(response)))
            }
        }
    }

    // MARK: /api/auth/logout
    func logout(
        refreshToken: String,
        completion: @escaping (Result<Void, Error>) -> Void
    ) {
        let url = "\(baseURL)/api/auth/logout"
        let params = LogoutRequest(refreshToken: refreshToken)

        let headers: HTTPHeaders = [
            .authorization(refreshToken),
            .accept("*/*")
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
            if let _ = response.error {
                completion(.failure(Self.mapError(response)))
                return
            }
            completion(.success(()))
        }
    }

    private static func mapError<T>(_ response: AFDataResponse<T>) -> Error {
        let status = response.response?.statusCode ?? -1

        let message: String? = response.data.flatMap { String(data: $0, encoding: .utf8) }

        switch status {
        case 400:
            return AuthServiceError.badRequest(message: message)
        case 401:
            return AuthServiceError.unauthorized
        case 403:
            return AuthServiceError.forbidden
        default:
            return response.error ?? AuthServiceError.unexpectedStatus(code: status, message: message)
        }
    }
}
