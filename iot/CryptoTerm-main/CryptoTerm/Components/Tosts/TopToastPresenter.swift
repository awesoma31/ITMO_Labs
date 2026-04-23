import UIKit

final class TopToastPresenter {

    // MARK: Public properties

    static let shared = TopToastPresenter()

    // MARK: Private properties

    private weak var currentToastView: TopToastView?
    private var dismissWorkItem: DispatchWorkItem?

    // MARK: Public methods

    func show(
        style: TopToastStyle,
        message: String,
        in view: UIView?,
        duration: TimeInterval,
        fontSize: CGFloat
    ) {
        dismissCurrent(animated: false)

        guard let hostView = view ?? keyWindowRootView else { return }

        let toast = TopToastView(style: style, message: message, fontSize: fontSize)
        toast.onDismiss = { [weak self] in
            self?.dismissCurrent(animated: true)
        }

        hostView.addSubview(toast)
        currentToastView = toast

        toast.translatesAutoresizingMaskIntoConstraints = false
        let top = toast.topAnchor.constraint(
            equalTo: hostView.safeAreaLayoutGuide.topAnchor,
            constant: Constants.toastTopInset
        )
        NSLayoutConstraint.activate([
            top,
            toast.leadingAnchor.constraint(equalTo: hostView.leadingAnchor, constant: Constants.toastHorizontalInset),
            toast.trailingAnchor.constraint(equalTo: hostView.trailingAnchor, constant: -Constants.toastHorizontalInset)
        ])

        hostView.layoutIfNeeded()

        toast.alpha = Constants.hiddenAlpha
        toast.transform = CGAffineTransform(translationX: Constants.transformX, y: Constants.transformY)

        UIView.animate(
            withDuration: Constants.showAnimationDuration,
            delay: Constants.animationDelay,
            options: [.curveEaseOut]
        ) {
            toast.alpha = Constants.visibleAlpha
            toast.transform = .identity
        }

        let work = DispatchWorkItem { [weak self] in
            self?.dismissCurrent(animated: true)
        }
        dismissWorkItem = work
        DispatchQueue.main.asyncAfter(deadline: .now() + duration, execute: work)
    }

    func dismissCurrent(animated: Bool) {
        dismissWorkItem?.cancel()
        dismissWorkItem = nil

        guard let toast = currentToastView else { return }
        currentToastView = nil

        let remove = {
            toast.removeFromSuperview()
        }

        guard animated else { remove(); return }

        UIView.animate(
            withDuration: Constants.hideAnimationDuration,
            delay: Constants.animationDelay,
            options: [.curveEaseIn]
        ) {
            toast.alpha = Constants.hiddenAlpha
            toast.transform = CGAffineTransform(translationX: Constants.transformX, y: Constants.transformY)
        } completion: { _ in
            remove()
        }
    }

    // MARK: Private methods

    private var keyWindowRootView: UIView? {
        let scenes = UIApplication.shared.connectedScenes
        let windowScene = scenes
            .compactMap { $0 as? UIWindowScene }
            .first { $0.activationState == .foregroundActive }

        let keyWindow = windowScene?.windows.first { $0.isKeyWindow }
        ?? UIApplication.shared.windows.first { $0.isKeyWindow }

        return keyWindow
    }
}

// MARK: - Constants

private extension TopToastPresenter {
    enum Constants {
        static let toastTopInset: CGFloat = 12
        static let toastHorizontalInset: CGFloat = 16

        static let hiddenAlpha: CGFloat = 0
        static let visibleAlpha: CGFloat = 1

        static let transformX: CGFloat = 0
        static let transformY: CGFloat = -40

        static let showAnimationDuration: TimeInterval = 0.25
        static let hideAnimationDuration: TimeInterval = 0.2
        static let animationDelay: TimeInterval = 0
    }
}

