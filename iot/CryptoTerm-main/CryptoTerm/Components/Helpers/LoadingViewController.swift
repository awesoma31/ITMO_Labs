import UIKit

final class LoadingViewController: UIViewController {

    // MARK: Private properties

    private let overlay = LoadingOverlayView()

    // MARK: Lifecycle

    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .black

        view.addSubview(overlay)

        NSLayoutConstraint.activate([
            overlay.topAnchor.constraint(equalTo: view.topAnchor),
            overlay.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            overlay.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            overlay.bottomAnchor.constraint(equalTo: view.bottomAnchor)
        ])

        overlay.show(text: Constants.defaultText, animated: false)
    }

    // MARK: Public methods

    func configure(text: String) {
        overlay.show(text: text, animated: false) // или просто overlay.setText, если добавишь
    }
}

// MARK: - Constants

private extension LoadingViewController {
    enum Constants {
        static let defaultText: String = "Signing you in…"
    }
}

