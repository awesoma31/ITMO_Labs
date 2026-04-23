class HomeScreenConfigurator {
    func configure(container: DependencyContainer) -> HomeViewController {
        let controller = HomeViewController()
        let presenter = HomePresenter()
        let interactor = container.makeHomeInteractor()

        controller.interactor = interactor
        interactor.presenter = presenter
        presenter.viewController = controller

        return controller
    }
}

