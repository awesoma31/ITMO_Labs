import Foundation

enum DateFormatters {
    static let msk: DateFormatter = {
        let f = DateFormatter()
        f.locale = Locale(identifier: "ru_RU")
        f.timeZone = TimeZone(identifier: "Europe/Moscow")!
        f.dateFormat = "dd.MM.yyyy HH:mm"
        return f
    }()
}
