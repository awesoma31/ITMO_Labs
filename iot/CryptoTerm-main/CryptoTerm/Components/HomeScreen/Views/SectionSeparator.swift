import UIKit

final class SectionSeparatorView: UICollectionReusableView {

    // MARK: Public properties

    static let reuseId = Constants.reuseId

    // MARK: Private properties

    private let line: UIView = {
        let v = UIView()
        v.translatesAutoresizingMaskIntoConstraints = false
        v.backgroundColor = Constants.lineColor
        return v
    }()

    // MARK: Init

    override init(frame: CGRect) {
        super.init(frame: frame)
        backgroundColor = .clear
        addSubview(line)

        NSLayoutConstraint.activate([
            line.heightAnchor.constraint(equalToConstant: Constants.lineHeight),
            line.leadingAnchor.constraint(equalTo: leadingAnchor, constant: Constants.leadingInset),
            line.trailingAnchor.constraint(equalTo: trailingAnchor, constant: Constants.trailingInset),
            line.centerYAnchor.constraint(equalTo: centerYAnchor)
        ])
    }

    required init?(coder: NSCoder) {
        fatalError()
    }
}

// MARK: - Constants

private extension SectionSeparatorView {
    enum Constants {
        static let reuseId = "SectionSeparatorView"

        static let lineColor = UIColor(red: 0.102, green: 0.102, blue: 0.102, alpha: 1)

        static let lineHeight: CGFloat = 1
        static let leadingInset: CGFloat = 6
        static let trailingInset: CGFloat = -6
    }
}

