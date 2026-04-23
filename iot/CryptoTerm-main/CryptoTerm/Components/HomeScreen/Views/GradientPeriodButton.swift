import UIKit

final class GradientPeriodButton: UIButton {

    private let gradientLayer = CAGradientLayer()
    var borderColor: UIColor?

    var isGradientOn: Bool = false {
        didSet {
            updateAppearance()
        }
    }

    override init(frame: CGRect) {
        super.init(frame: frame)
        commonInit()
    }

    required init?(coder: NSCoder) {
        super.init(coder: coder)
        commonInit()
    }

    private func commonInit() {
        translatesAutoresizingMaskIntoConstraints = false
        layer.cornerRadius = 15
        layer.masksToBounds = true
        layer.borderWidth = 1
        layer.borderColor = borderColor?.cgColor ?? UIColor(red: 0.376, green: 0.376, blue: 0.376, alpha: 1).cgColor
        titleLabel?.font = UIFont(name: "Lato-Bold", size: 12)
        setTitleColor(UIColor(red: 0.502, green: 0.502, blue: 0.502, alpha: 1), for: .normal)
        backgroundColor = UIColor(red: 0.102, green: 0.102, blue: 0.102, alpha: 1)
        gradientLayer.colors = [
            UIColor(red: 0.678, green: 0.706, blue: 0.953, alpha: 1).cgColor,
            UIColor(red: 0.412, green: 0.463, blue: 0.922, alpha: 1).cgColor,
            UIColor(red: 0.232, green: 0.261, blue: 0.521, alpha: 1).cgColor
        ]
        gradientLayer.locations = [0, 0.5, 1]
        gradientLayer.startPoint = CGPoint(x: 1, y: 0.5)
        gradientLayer.endPoint = CGPoint(x: 0, y: 0.5)
        gradientLayer.cornerRadius = 15
        gradientLayer.actions = [
            "bounds": NSNull(), "position": NSNull(), "frame": NSNull(),
            "colors": NSNull(), "locations": NSNull(), "contents": NSNull()
        ]
        layer.insertSublayer(gradientLayer, at: 0)
        updateAppearance()
    }

    override func layoutSubviews() {
        super.layoutSubviews()
        CATransaction.begin()
        CATransaction.setDisableActions(true)
        gradientLayer.frame = bounds
        CATransaction.commit()
        if let titleLabel { bringSubviewToFront(titleLabel) }
        if let imageView { bringSubviewToFront(imageView) }
    }

    private func updateAppearance() {
        if isGradientOn {
            backgroundColor = .clear
            setTitleColor(.white, for: .normal)
            self.titleLabel?.font = UIFont(name: "Lato-Bold", size: 12)
            layer.borderColor = UIColor.clear.cgColor
            gradientLayer.isHidden = false
        } else {
            backgroundColor = UIColor(red: 0.102, green: 0.102, blue: 0.102, alpha: 1)
            setTitleColor(UIColor(red: 0.502, green: 0.502, blue: 0.502, alpha: 1), for: .normal)
            layer.borderColor = borderColor?.cgColor ?? UIColor(red: 0.376, green: 0.376, blue: 0.376, alpha: 1).cgColor
            self.titleLabel?.font = UIFont(name: "Lato-Bold", size: 12)
            gradientLayer.isHidden = true
        }
        setNeedsLayout()
    }
}

