import Foundation

protocol MiningCalendarPresenterInput: AnyObject {
    func showStartScreen()
    func successCheduleRegimeChange(success: Bool)
    func applyPlannedModeDates(_ modeDates: [HardwareAccordionCell.Mode: Set<Date>], forDeviceId: String)
}

typealias MiningCalendarPresenterOutput = MiningCalendarViewControllerInput

final class MiningCalendarPresenter {
    weak var viewController: MiningCalendarPresenterOutput?
}

// MARK: - MiningCalendarPresenterInput

extension MiningCalendarPresenter: MiningCalendarPresenterInput {

    func showStartScreen() {
        viewController?.showStartScreen()
    }

    func successCheduleRegimeChange(success: Bool) {
        viewController?.successCheduleRegimeChange(isSuccess: success)
    }

    func applyPlannedModeDates(_ modeDates: [HardwareAccordionCell.Mode : Set<Date>], forDeviceId: String) {
        viewController?.applyPlannedModeDates(modeDates, forDeviceId: forDeviceId)
    }
}
