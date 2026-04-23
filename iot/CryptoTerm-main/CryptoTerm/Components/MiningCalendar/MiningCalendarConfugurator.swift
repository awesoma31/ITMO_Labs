final class MiningCalendarConfugurator {
    func configure(
        container: DependencyContainer,
        devices: [DeviceRow],
        openedDeviceId: String
    ) -> MiningCalendarViewController {
        let interactor = container.makeMiningCalendarInteractor()
        let viewController = MiningCalendarViewController(devices: devices, openedDeviceId: openedDeviceId)
        let presenter = MiningCalendarPresenter()

        viewController.interactor = interactor
        interactor.presenter = presenter
        presenter.viewController = viewController

        return viewController
    }
}
