import UIKit

final class MainCoordinator: Coordinator {

    // MARK: Public properties

    let navigationController: UINavigationController

    // MARK: Private properties

    private let authConfigurator = AuthConfigurator()
    private let homeScreenConfigurator = HomeScreenConfigurator()
    private let miningCalendarConfigurator = MiningCalendarConfugurator()
    private let dependencyContainer = DependencyContainer()
    private var homeScreen: HomeViewController?

    // MARK: Init

    init(navigationController: UINavigationController) {
        self.navigationController = navigationController
    }

    // MARK: Public methods

    func start() {
        let store = dependencyContainer.authSessionStore
        let authServicce = dependencyContainer.authServicce

        guard let lastSession = store.readSession() else {
            showAuthScreen()
            return
        }

        showLoadingScreen()

        authServicce.refresh(refreshToken: lastSession.refreshToken) { [weak self] result in
            switch result {
            case .success(let authResponse):
                store.save(authResponse)
                self?.showMainScreen()
            case .failure(_):
                store.clear()
                self?.showAuthScreen()
            }
        }
    }

    func showAuthScreen() {
        let authController = authConfigurator.configure(container: dependencyContainer)
        authController.delegate = self
        self.homeScreen = nil
        navigationController.setViewControllers([authController], animated: true)
    }

    func showMainScreen() {
        let homeScreen = homeScreenConfigurator.configure(container: dependencyContainer)
        homeScreen.delegate = self
        self.homeScreen = homeScreen
        navigationController.setViewControllers([homeScreen], animated: true)
    }

    func showCoinsScreen() {
        let coinsScreen = CoinsViewController()
        coinsScreen.delegate = self
        navigationController.pushViewController(coinsScreen, animated: true)
    }

    func hideCoinsScreen(title: String, iconName: String) {
        homeScreen?.setCoinsButton(title: title, iconName: iconName)
        navigationController.popViewController(animated: true)
    }

    func showMimingCalendar(devices: [DeviceRow], openedDeviceId: String) {
        let miningCalendar = miningCalendarConfigurator.configure(
            container: dependencyContainer,
            devices: devices,
            openedDeviceId: openedDeviceId
        )
        miningCalendar.delegate = self
        navigationController.pushViewController(miningCalendar, animated: true)
    }

    func hideMimingCalendar() {
        navigationController.popViewController(animated: true)
    }

    // MARK: Private methods

    private func showLoadingScreen() {
        let viewController = LoadingViewController()
        navigationController.setViewControllers([viewController], animated: false)
    }
}
