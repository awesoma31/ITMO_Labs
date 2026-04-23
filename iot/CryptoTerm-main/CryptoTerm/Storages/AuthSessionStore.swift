import Foundation
import KeychainAccess

protocol AuthSessionStoring {
    func save(_ response: AuthResponse)
    func readSession() -> SessionState?
    func clear()
}

final class AuthSessionStore: AuthSessionStoring {

    private let defaults: UserDefaults
    private let keychain: Keychain

    init(
        defaults: UserDefaults = .standard,
        service: String = Bundle.main.bundleIdentifier ?? "App",
        accessGroup: String? = nil
    ) {
        self.defaults = defaults
        self.keychain = Keychain(service: service, accessGroup: accessGroup).accessibility(.afterFirstUnlock)
    }

    func save(_ response: AuthResponse) {
        defaults.set(response.userId, forKey: DefaultsKeys.userId)
        defaults.set(response.username, forKey: DefaultsKeys.username)
        defaults.set(response.email, forKey: DefaultsKeys.email)
        (try? keychain.set(response.accessToken, key: KeychainKeys.accessToken)) ?? print("Проблема с сохранением")
        (try? keychain.set(response.refreshToken, key: KeychainKeys.refreshToken)) ?? print("Проблема с сохранением")
    }

    func readSession() -> SessionState? {
        guard
            let userId = defaults.string(forKey: DefaultsKeys.userId),
            let username = defaults.string(forKey: DefaultsKeys.username),
            let email = defaults.string(forKey: DefaultsKeys.email),
            let accessToken = keychain[KeychainKeys.accessToken],
            let refreshToken = keychain[KeychainKeys.refreshToken]
        else { return nil }

        return SessionState(
            userId: userId,
            username: username,
            email: email,
            accessToken: accessToken,
            refreshToken: refreshToken
        )
    }

    func clear() {
        defaults.removeObject(forKey: DefaultsKeys.userId)
        defaults.removeObject(forKey: DefaultsKeys.username)
        defaults.removeObject(forKey: DefaultsKeys.email)
        (try? keychain.remove(KeychainKeys.accessToken)) ?? print("Проблема с очисткой")
        (try? keychain.remove(KeychainKeys.refreshToken)) ?? print("Проблема с очисткой")
    }
}

// MARK: - DefaultsKeys

private extension AuthSessionStore {
    enum DefaultsKeys {
        static let userId = "auth.userId"
        static let username = "auth.username"
        static let email = "auth.email"
    }
}

// MARK: - KeychainKeys

private extension AuthSessionStore {
    enum KeychainKeys {
        static let accessToken = "auth.accessToken"
        static let refreshToken = "auth.refreshToken"
    }
}
