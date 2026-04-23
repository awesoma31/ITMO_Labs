import UIKit

final class PriceInputRowView: UIView {

    // MARK: Public properties

    let textField: UITextField = {
        let tf = UITextField()
        tf.translatesAutoresizingMaskIntoConstraints = false
        tf.textColor = Constants.textColor
        tf.font = UIFont(name: Constants.latoRegularFontName, size: Constants.fontSize)
        tf.autocorrectionType = .no
        tf.spellCheckingType = .no
        tf.keyboardType = .decimalPad
        tf.returnKeyType = .done
        tf.borderStyle = .none
        tf.backgroundColor = .clear
        return tf
    }()

    // MARK: Private properties

    var text: String {
        get {
            textField.text ?? Constants.emptyString
        }
        set {
            textField.text = newValue
        }
    }

    private let bg: UIView = {
        let view = UIView()
        view.translatesAutoresizingMaskIntoConstraints = false
        view.backgroundColor = Constants.backgroundColor
        view.layer.cornerRadius = Constants.cornerRadius
        view.layer.borderWidth = Constants.borderWidth
        view.layer.borderColor = Constants.borderColor.cgColor
        return view
    }()

    private let placeholderText = Constants.placeholderText
    private var placeholderBeforeFocus: String = Constants.emptyString

    // MARK: Init

    init() {
        super.init(frame: .zero)
        translatesAutoresizingMaskIntoConstraints = false
        backgroundColor = .clear

        addSubview(bg)
        bg.addSubview(textField)

        applyPlaceholderNormal()

        textField.delegate = self

        NSLayoutConstraint.activate([
            bg.topAnchor.constraint(equalTo: topAnchor),
            bg.leadingAnchor.constraint(equalTo: leadingAnchor),
            bg.trailingAnchor.constraint(equalTo: trailingAnchor),
            bg.bottomAnchor.constraint(equalTo: bottomAnchor),

            textField.leadingAnchor.constraint(equalTo: bg.leadingAnchor, constant: Constants.textFieldLeading),
            textField.trailingAnchor.constraint(equalTo: bg.trailingAnchor, constant: Constants.textFieldTrailing),
            textField.topAnchor.constraint(equalTo: bg.topAnchor),
            textField.bottomAnchor.constraint(equalTo: bg.bottomAnchor)
        ])
    }

    required init?(coder: NSCoder) {
        fatalError()
    }

    // MARK: Private methods

    private func applyPlaceholderNormal() {
        let attrs: [NSAttributedString.Key: Any] = [
            .foregroundColor: Constants.placeholderColor,
            .font: UIFont(name: Constants.latoRegularFontName, size: Constants.fontSize) ?? UIFont.systemFont(ofSize: Constants.fontSize)
        ]
        textField.attributedPlaceholder = NSAttributedString(
            string: placeholderText,
            attributes: attrs
        )
    }
}

// MARK: - UITextFieldDelegate

extension PriceInputRowView: UITextFieldDelegate {

    func textFieldDidBeginEditing(_ textField: UITextField) {
        placeholderBeforeFocus = placeholderText

        if textField.text == placeholderText || textField.text?.isEmpty == true {
            textField.text = Constants.emptyString
            textField.textColor = Constants.textColor
        }
    }

    func textFieldDidEndEditing(_ textField: UITextField) {
        if textField.text?.isEmpty == true {
            applyPlaceholderNormal()
        }
    }

    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        return true
    }
}

// MARK: - Constants

private extension PriceInputRowView {
    enum Constants {
        static let textColor = UIColor(red: 1, green: 1, blue: 1, alpha: 1)
        static let backgroundColor = UIColor(red: 0, green: 0, blue: 0, alpha: 1)

        static let cornerRadius: CGFloat = 15
        static let borderWidth: CGFloat = 1
        static let borderColor = UIColor(red: 0.231, green: 0.231, blue: 0.231, alpha: 1)

        static let placeholderText = "Price per kW/h"
        static let placeholderColor = UIColor(red: 0.502, green: 0.502, blue: 0.502, alpha: 1)

        static let latoRegularFontName = "Lato-Regular"
        static let fontSize: CGFloat = 12

        static let textFieldLeading: CGFloat = 16
        static let textFieldTrailing: CGFloat = -16

        static let emptyString = ""
    }
}

