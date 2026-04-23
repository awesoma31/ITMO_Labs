import UIKit

final class AuthTextFieldView: UIView {

    // MARK: Public properties

    var action: (() -> Void)?
    var onReturn: (() -> Void)?

    var isSecurity: Bool {
        textField.isSecureTextEntry
    }

    var text: String {
        get { textField.text ?? "" }
        set { textField.text = newValue }
    }

    // MARK: Private properties

    private let fieldView: UIView = {
        let view = UIView()
        view.translatesAutoresizingMaskIntoConstraints = false
        view.backgroundColor = Constants.fieldBackgroundColor
        view.layer.cornerRadius = Constants.fieldCornerRadius
        view.layer.masksToBounds = true
        view.layer.borderWidth = Constants.fieldBorderWidth
        return view
    }()

    private let textField: UITextField = {
        let textField = UITextField()
        textField.translatesAutoresizingMaskIntoConstraints = false
        textField.textColor = Constants.textColor
        textField.font = Constants.textFieldFont
        textField.autocapitalizationType = .none
        textField.autocorrectionType = .no
        textField.spellCheckingType = .no
        textField.textContentType = .oneTimeCode
        let leftPad = UIView(frame: CGRect(x: 0, y: 0, width: Constants.leftPadWidth, height: Constants.leftPadHeight))
        textField.leftView = leftPad
        textField.leftViewMode = .always
        return textField
    }()

    private let rightButton: UIButton = {
        let button = UIButton()
        button.translatesAutoresizingMaskIntoConstraints = false
        return button
    }()

    private var state: State = .normal {
        didSet {
            applyState()
        }
    }

    private var normalPlaceholder: String = ""
    private var placeholderBeforeFocus: String = ""

    // MARK: Init

    override init(frame: CGRect) {
        super.init(frame: frame)
        setup()
        applyState()
    }

    required init?(coder: NSCoder) {
        super.init(coder: coder)
        setup()
        applyState()
    }

    // MARK: Lifecycle

    override var intrinsicContentSize: CGSize {
        CGSize(width: UIView.noIntrinsicMetric, height: Constants.fieldHeight)
    }

    // MARK: Public methods

    func configure(
        buttonImage: UIImage?,
        placeholderText: String,
        isSecurity: Bool = false,
        isEnabled: Bool = true,
        buttonAction: (() -> Void)? = nil
    ) {
        rightButton.setImage(buttonImage, for: .normal)
        rightButton.isUserInteractionEnabled = isEnabled
        normalPlaceholder = placeholderText
        textField.isSecureTextEntry = isSecurity
        action = buttonAction
        
        applyPlaceholderNormal()
    }

    func setButtonImage(image: UIImage?) {
        rightButton.setImage(image, for: .normal)
    }

    func toggleSecirity() {
        textField.isSecureTextEntry = !textField.isSecureTextEntry
    }

    func setSecurity(isSecurity: Bool) {
        textField.isSecureTextEntry = isSecurity
    }

    func becomeActive() {
        textField.becomeFirstResponder()
    }

    func resignActive() {
        textField.resignFirstResponder()
    }

    func getTextField() -> UITextField? {
        return textField
    }

    func setState(newState: State) {
        state = newState
    }

    func setButtonEnabled(isEnabled: Bool) {
        rightButton.isUserInteractionEnabled = isEnabled
    }

    // MARK: Private methods

    private func setup() {
        backgroundColor = .clear

        addSubview(fieldView)

        textField.delegate = self
        fieldView.addSubview(textField)

        rightButton.addTarget(self, action: #selector(buttonTap), for: .touchUpInside)
        fieldView.addSubview(rightButton)

        NSLayoutConstraint.activate([
            fieldView.topAnchor.constraint(equalTo: topAnchor),
            fieldView.leadingAnchor.constraint(equalTo: leadingAnchor),
            fieldView.trailingAnchor.constraint(equalTo: trailingAnchor),
            fieldView.heightAnchor.constraint(equalToConstant: Constants.fieldHeight),
            fieldView.bottomAnchor.constraint(equalTo: bottomAnchor),

            rightButton.trailingAnchor.constraint(equalTo: fieldView.trailingAnchor, constant: Constants.rightButtonTrailingOffset),
            rightButton.topAnchor.constraint(equalTo: fieldView.topAnchor, constant: Constants.rightButtonTopOffset),
            rightButton.bottomAnchor.constraint(equalTo: fieldView.bottomAnchor, constant: Constants.rightButtonBottomOffset),
            rightButton.widthAnchor.constraint(equalToConstant: Constants.rightButtonWidth),

            textField.leadingAnchor.constraint(equalTo: fieldView.leadingAnchor),
            textField.topAnchor.constraint(equalTo: fieldView.topAnchor),
            textField.bottomAnchor.constraint(equalTo: fieldView.bottomAnchor),
            textField.trailingAnchor.constraint(equalTo: rightButton.leadingAnchor, constant: Constants.textFieldRightOffset)
        ])
    }

    private func applyPlaceholderNormal() {
        let attrs: [NSAttributedString.Key: Any] = [
            .foregroundColor: Constants.placeholderColor,
            .font: Constants.textFieldFont
        ]
        textField.attributedPlaceholder = NSAttributedString(string: normalPlaceholder, attributes: attrs)
    }

    private func applyPlaceholderError(_ message: String) {
        let attrs: [NSAttributedString.Key: Any] = [
            .foregroundColor: Constants.errorBorderColor,
            .font: Constants.textFieldFont
        ]
        textField.attributedPlaceholder = NSAttributedString(string: message, attributes: attrs)
    }

    private func applyState() {
        switch state {
        case .normal:
            fieldView.layer.borderColor = Constants.normalBorderColor.cgColor
            applyPlaceholderNormal()
            
        case .focused:
            fieldView.layer.borderColor = Constants.focusedBorderColor.cgColor
            applyPlaceholderNormal()
            
        case .error(let message):
            fieldView.layer.borderColor = Constants.errorBorderColor.cgColor
            applyPlaceholderError(message)
            
        case .correct:
            fieldView.layer.borderColor = Constants.correctBorderColor.cgColor
            applyPlaceholderNormal()
        }
    }

    @objc private func buttonTap() {
        action?()
    }
}

// MARK: - UITextFieldDelegate

extension AuthTextFieldView: UITextFieldDelegate {

    func textFieldDidBeginEditing(_ textField: UITextField) {
        state = .focused
        placeholderBeforeFocus = normalPlaceholder
        normalPlaceholder = ""
        applyPlaceholderNormal()
    }

    func textFieldDidEndEditing(_ textField: UITextField) {
        state = .normal
        normalPlaceholder = placeholderBeforeFocus
        placeholderBeforeFocus = ""
        applyPlaceholderNormal()
    }

    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        onReturn?()
        return true
    }
}

// MARK: - State

extension AuthTextFieldView {
    enum State: Equatable {
        case normal
        case focused
        case error(message: String)
        case correct
    }
}

// MARK: - Constants

private extension AuthTextFieldView {
    enum Constants {
        // Colors
        static let fieldBackgroundColor = UIColor(red: 0, green: 0, blue: 0, alpha: 1)
        static let textColor = UIColor(red: 1, green: 1, blue: 1, alpha: 1)
        static let placeholderColor = UIColor.white

        static let normalBorderColor = UIColor(red: 0.231, green: 0.231, blue: 0.231, alpha: 1)
        static let focusedBorderColor = UIColor(red: 0.17, green: 0.211, blue: 0.583, alpha: 1)
        static let errorBorderColor = UIColor(red: 0.498, green: 0, blue: 0, alpha: 1)
        static let correctBorderColor = UIColor(red: 0, green: 0.522, blue: 0.052, alpha: 1)

        // Layout
        static let fieldCornerRadius: CGFloat = 15
        static let fieldBorderWidth: CGFloat = 1
        static let fieldHeight: CGFloat = 50

        static let leftPadWidth: CGFloat = 20
        static let leftPadHeight: CGFloat = 1

        static let rightButtonTrailingOffset: CGFloat = -13
        static let rightButtonTopOffset: CGFloat = 13
        static let rightButtonBottomOffset: CGFloat = -13
        static let rightButtonWidth: CGFloat = 24

        static let textFieldRightOffset: CGFloat = -10

        // Font
        static let textFieldFont = UIFont(name: "Lato-Bold", size: 16) ?? UIFont.systemFont(ofSize: 16, weight: .bold)
    }
}
