import UIKit

protocol Coordinator: AnyObject {
    var navigationController: UINavigationController { get }
    func start()
    func showAuthScreen()
    func showMainScreen()
    func showCoinsScreen()
    func hideCoinsScreen(title: String, iconName: String)
    func showMimingCalendar(devices: [DeviceRow], openedDeviceId: String)
    func hideMimingCalendar()
}
