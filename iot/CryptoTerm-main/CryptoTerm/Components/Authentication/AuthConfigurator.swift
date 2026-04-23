class AuthConfigurator {
    func configure(container: DependencyContainer) -> AuthViewController {
        let controller = AuthViewController()
        let presenter = AuthPresenter()
        let interactor = container.makeAuthInteractor()

        controller.interactor = interactor
        interactor.presenter = presenter
        presenter.viewController = controller

        return controller
    }
}
