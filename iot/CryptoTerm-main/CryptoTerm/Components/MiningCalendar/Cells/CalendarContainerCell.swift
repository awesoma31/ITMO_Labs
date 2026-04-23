import UIKit

final class CalendarContainerCell: UICollectionViewCell {

    // MARK: Public properties

    static let reuseId = Constants.reuseId

    // MARK: Private properties

    private var didSetup = false

    // MARK: Public methods

    func embed(header: UIView, divider: UIView, calendar: UIView, legend: UIView) {
        if header.superview !== contentView {
            header.removeFromSuperview()
            contentView.addSubview(header)
        }
        if divider.superview !== contentView {
            divider.removeFromSuperview()
            contentView.addSubview(divider)
        }
        if calendar.superview !== contentView {
            calendar.removeFromSuperview()
            contentView.addSubview(calendar)
        }
        if legend.superview !== contentView {
            legend.removeFromSuperview()
            contentView.addSubview(legend)
        }

        if !didSetup {
            header.translatesAutoresizingMaskIntoConstraints = false
            divider.translatesAutoresizingMaskIntoConstraints = false
            calendar.translatesAutoresizingMaskIntoConstraints = false
            legend.translatesAutoresizingMaskIntoConstraints = false

            NSLayoutConstraint.activate([
                header.topAnchor.constraint(equalTo: contentView.topAnchor, constant: Constants.headerTopInset),
                header.leadingAnchor.constraint(equalTo: contentView.leadingAnchor, constant: Constants.headerHorizontalInset),
                header.trailingAnchor.constraint(equalTo: contentView.trailingAnchor, constant: -Constants.headerHorizontalInset),
                header.heightAnchor.constraint(equalToConstant: Constants.headerHeight),

                divider.topAnchor.constraint(equalTo: header.bottomAnchor, constant: Constants.dividerTopInset),
                divider.leadingAnchor.constraint(equalTo: contentView.leadingAnchor, constant: Constants.dividerHorizontalInset),
                divider.trailingAnchor.constraint(equalTo: contentView.trailingAnchor, constant: -Constants.dividerHorizontalInset),
                divider.heightAnchor.constraint(equalToConstant: Constants.dividerHeight),

                calendar.topAnchor.constraint(equalTo: divider.bottomAnchor),
                calendar.leadingAnchor.constraint(equalTo: contentView.leadingAnchor, constant: Constants.calendarHorizontalInset),
                calendar.trailingAnchor.constraint(equalTo: contentView.trailingAnchor, constant: -Constants.calendarHorizontalInset),
                calendar.heightAnchor.constraint(equalToConstant: Constants.calendarHeight),

                legend.topAnchor.constraint(equalTo: calendar.bottomAnchor, constant: Constants.legendTopInset),
                legend.leadingAnchor.constraint(equalTo: calendar.leadingAnchor),
                legend.trailingAnchor.constraint(equalTo: calendar.trailingAnchor),
                legend.bottomAnchor.constraint(equalTo: contentView.bottomAnchor, constant: -Constants.legendBottomInset)
            ])

            didSetup = true
        }
    }
}

// MARK: - Constants

private extension CalendarContainerCell {
    enum Constants {
        static let reuseId: String = "CalendarContainerCell"

        static let headerTopInset: CGFloat = 16
        static let headerHorizontalInset: CGFloat = 26
        static let headerHeight: CGFloat = 19

        static let dividerTopInset: CGFloat = 28
        static let dividerHorizontalInset: CGFloat = 26
        static let dividerHeight: CGFloat = 1

        static let calendarHorizontalInset: CGFloat = 16
        static let calendarHeight: CGFloat = 350

        static let legendTopInset: CGFloat = 10
        static let legendBottomInset: CGFloat = 12
    }
}

