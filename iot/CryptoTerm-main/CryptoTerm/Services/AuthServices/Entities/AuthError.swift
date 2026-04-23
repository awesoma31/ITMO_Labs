import Foundation

enum AuthServiceError: LocalizedError {
    case badRequest(message: String?)
    case unauthorized
    case forbidden
    case unexpectedStatus(code: Int, message: String?)

    var errorDescription: String? {
        switch self {
        case .badRequest(let msg):
            return msg ?? "Неверные данные."
        case .unauthorized:
            return "Не авторизован (401)."
        case .forbidden:
            return "Доступ запрещён (403)."
        case .unexpectedStatus(let code, let msg):
            return msg ?? "Неожиданный ответ сервера (\(code))."
        }
    }
}
