import Alamofire
import Foundation

protocol CommandServiceProtocol {
    func deleteCommand(
        accessToken: String,
        cmdId: String,
        completion: @escaping (Result<Void, Error>) -> Void
    )

    func getCommandsHistory(
        accessToken: String,
        minerId: String,
        commandStatus: CommandStatus,
        completion: @escaping (Result<[AsicCommandResponse], Error>) -> Void
    )
}

final class CommandService: CommandServiceProtocol {

    private let baseURL: String = "https://cryptoterm.ru"
    private let session: Session = .default

    // MARK: /api/v1/asic-commands/commands/{cmdId}
    func deleteCommand(
        accessToken: String,
        cmdId: String,
        completion: @escaping (Result<Void, Error>) -> Void
    ) {
        let url = "\(baseURL)/api/v1/asic-commands/commands/\(cmdId)"

        let headers: HTTPHeaders = [
            .authorization(bearerToken: accessToken),
            .accept("*/*")
        ]

        session.request(url, method: .delete, headers: headers)
            .validate(statusCode: 200..<300)
            .response(queue: .main) { response in
                switch response.result {
                case .success:
                    completion(.success(()))
                case .failure(let error):
                    completion(.failure(error))
                }
            }
    }

    // MARK: /api/v1/asic-commands/history/miner/{minerId}
    func getCommandsHistory(
        accessToken: String,
        minerId: String,
        commandStatus: CommandStatus,
        completion: @escaping (Result<[AsicCommandResponse], Error>) -> Void
    ) {
        let url = "\(baseURL)/api/v1/asic-commands/history/miner/\(minerId)"
        let params = AsicCommandRequest(status: commandStatus.rawValue)

        let headers: HTTPHeaders = [
            .authorization(bearerToken: accessToken),
            .accept("*/*"),
        ]

        session.request(
            url,
            method: .get,
            parameters: params,
            encoder: URLEncodedFormParameterEncoder(destination: .queryString),
            headers: headers
        )
        .validate(statusCode: 200..<300)
        .responseDecodable(
            of: [AsicCommandResponse].self,
            decoder: JSONDecoder.asicCommandDecoder()
        ) { response in
            switch response.result {
            case .success(let items):
                completion(.success(items))
            case .failure(let error):
                completion(.failure(error))
            }
        }
    }
}
