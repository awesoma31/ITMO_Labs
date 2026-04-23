protocol AuthInteractorInput: AnyObject {
    func onLogIn(email: String, password: String, username: String)
    func onSignIn(email: String, password: String)
}

typealias AuthInteractorOutput = AuthPresenterInput

final class AuthInteractor {
    
    // MARK: Public properties
    
    var presenter: AuthInteractorOutput?
    
    // MARK: Private propirties
    
    private let store: AuthSessionStoring
    private let authService: AuthServiceProtocol

    // MARK: Init

    init(store: AuthSessionStoring, authService: AuthServiceProtocol) {
        self.store = store
        self.authService = authService
    }
    
}

// MARK: - AuthInteractorInput

extension AuthInteractor: AuthInteractorInput {

    func onSignIn(email: String, password: String) {
        authService.login(
            usernameOrEmail: email,
            password: password
        ) { [weak self] result in
            self?.presenter?.setLoading(false)
            switch result {
            case .success(let auth):
                self?.store.save(auth)
                self?.presenter?.successLogInOrSignIn()
            case .failure(let error):
                switch error {
                case AuthServiceError.badRequest(let message):
                    self?.presenter?.failureSignIn(message: message ?? "Incorrect data or user already exists")
                case AuthServiceError.forbidden:
                    self?.presenter?.failureSignIn(message: "Access denied")
                default:
                    self?.presenter?.failureSignIn(message: "Unknown server error")
                }
            }
        }
    }

    func onLogIn(email: String, password: String, username: String) {
        authService.register(
            username: username,
            email: email,
            password: password,
            telegram: nil
        ) { [weak self] result in
            self?.presenter?.setLoading(false)
            switch result {
            case .success(let auth):
                self?.store.save(auth)
                self?.presenter?.successLogInOrSignIn()
            case .failure(let error):
                switch error {
                case AuthServiceError.badRequest(let message):
                    self?.presenter?.failureLogIn(message: message ?? "Incorrect data")
                case AuthServiceError.forbidden:
                    self?.presenter?.failureLogIn(message: "Access denied")
                default:
                    self?.presenter?.failureLogIn(message: "Unknown server error")
                }
            }
        }
    }
}
