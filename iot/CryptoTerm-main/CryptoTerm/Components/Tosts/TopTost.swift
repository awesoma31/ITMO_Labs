import UIKit

final class TopToast {

    static func show(
        style: TopToastStyle,
        message: String,
        in view: UIView? = nil,
        duration: TimeInterval = 3.0,
        fontSize: CGFloat = 14
    ) {
        DispatchQueue.main.async {
            TopToastPresenter.shared.show(
                style: style,
                message: message,
                in: view,
                duration: duration,
                fontSize: fontSize
            )
        }
    }

    static func dismiss() {
        DispatchQueue.main.async {
            TopToastPresenter.shared.dismissCurrent(animated: true)
        }
    }
}
