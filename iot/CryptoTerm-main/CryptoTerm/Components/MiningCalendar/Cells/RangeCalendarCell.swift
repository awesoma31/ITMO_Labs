import UIKit
import FSCalendar

final class RangeCalendarCell: FSCalendarCell {

    // MARK: Private properties

    private let selectionLayer = CAShapeLayer()
    private let pillInsets = UIEdgeInsets(top: 0, left: 0, bottom: 0, right: 0)
    private let pillCornerRadius: CGFloat = 15
    private let enabledTitleColor = UIColor(white: 1.0, alpha: 1.0)
    private let disabledTitleColor = UIColor(white: 1.0, alpha: 0.25)

    // MARK: Init

    override init(frame: CGRect) {
        super.init(frame: frame)

        selectionLayer.fillColor = UIColor.clear.cgColor
        selectionLayer.isHidden = true

        contentView.layer.insertSublayer(selectionLayer, at: 0)

        titleLabel.textAlignment = .center
        titleLabel.textColor = UIColor(red: 1, green: 1, blue: 1, alpha: 1)
        titleLabel.font = UIFont(name: "Lato-Regular", size: 12)
    }

    required init!(coder aDecoder: NSCoder!) {
        super.init(coder: aDecoder)
    }

    // MARK: Lifecycle

    override func layoutSubviews() {
        super.layoutSubviews()

        selectionLayer.frame = contentView.bounds

        titleLabel.frame = contentView.bounds
        titleLabel.center = CGPoint(x: contentView.bounds.midX, y: contentView.bounds.midY)

        subtitleLabel?.isHidden = true
        eventIndicator?.isHidden = true
    }

    override func prepareForReuse() {
        super.prepareForReuse()
        setDisabled(false)
        selectionLayer.isHidden = true
        selectionLayer.path = nil
        selectionLayer.fillColor = UIColor.clear.cgColor
    }

    // MARK: Public methods

    func applySelection(style: SelectionStyle, color: UIColor) {
        guard style != .none else {
            selectionLayer.isHidden = true
            selectionLayer.path = nil
            return
        }

        selectionLayer.isHidden = false
        selectionLayer.fillColor = color.cgColor

        var rect = contentView.bounds.inset(by: pillInsets)
        rect = rect.insetBy(dx: -0.35, dy: 0)

        let path: UIBezierPath
        switch style {
        case .single:
            path = UIBezierPath(roundedRect: rect, cornerRadius: pillCornerRadius)

        case .start:
            path = UIBezierPath(
                roundedRect: rect,
                byRoundingCorners: [.topLeft, .bottomLeft],
                cornerRadii: CGSize(width: pillCornerRadius, height: pillCornerRadius)
            )

        case .middle:
            path = UIBezierPath(rect: rect)

        case .end:
            path = UIBezierPath(
                roundedRect: rect,
                byRoundingCorners: [.topRight, .bottomRight],
                cornerRadii: CGSize(width: pillCornerRadius, height: pillCornerRadius)
            )

        case .none:
            path = UIBezierPath()
        }

        selectionLayer.path = path.cgPath
    }

    func setDisabled(_ disabled: Bool) {
        titleLabel.textColor = disabled ? disabledTitleColor : enabledTitleColor

        isUserInteractionEnabled = !disabled

        if disabled {
            selectionLayer.isHidden = true
            selectionLayer.path = nil
            selectionLayer.fillColor = UIColor.clear.cgColor
        }
    }
}
