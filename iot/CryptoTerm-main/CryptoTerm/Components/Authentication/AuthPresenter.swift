protocol AuthPresenterInput: AnyObject {
    func successLogInOrSignIn()
    func failureLogIn(message: String)
    func failureSignIn(message: String)
    func setLoading(_ isLoading: Bool)
}

typealias AuthPresenterOutput = AuthViewControllerInput

final class AuthPresenter {

    // MARK: Public propirties

    weak var viewController: AuthPresenterOutput?

    // MARK: Private methods

    private func isEmailExits(message: String) -> Bool {
        message.contains("User with email ") && message.contains(" already exists")
    }

    private func isUsernameExists(message: String) -> Bool {
        message.contains("User with username ") && message.contains(" already exists")
    }

    private func isEmailNotExists(messsage: String) -> Bool {
        messsage.contains("User not found")
    }

    private func isIncorrectPassword(message: String) -> Bool {
        message.contains("Invalid credentials")
    }
}

// MARK: - AuthPresenterInput

extension AuthPresenter: AuthPresenterInput {
    func successLogInOrSignIn() {
        viewController?.successLogInOrSignIn()
    }

    func failureLogIn(message: String) {
        if isEmailExits(message: message) {
            viewController?.setEmailState(newState: .error(message: "Email already exists"))
        }

        if isUsernameExists(message: message) {
            viewController?.setUsernameState(newState: .error(message: "Username already exists"))
        }
    }

    func failureSignIn(message: String) {
        if isEmailNotExists(messsage: message) {
            viewController?.setEmailState(newState: AuthTextFieldView.State.error(message: "Email doesn’t exist"))
        }

        if isIncorrectPassword(message: message) {
            viewController?.setPasswordState(newState: .error(message: "Invalid password"))
        }
    }

    func setLoading(_ isLoading: Bool) {
        viewController?.setLoading(isLoading)
    }
}
