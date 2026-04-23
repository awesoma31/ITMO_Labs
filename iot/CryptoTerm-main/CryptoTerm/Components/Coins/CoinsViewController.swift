import UIKit

final class CoinsViewController: UIViewController {

    // MARK: Public properties

    weak var delegate: Coordinator?

    // MARK: Private properties

    private let tableView: UITableView = {
        let tableView = UITableView()
        tableView.translatesAutoresizingMaskIntoConstraints = false
        tableView.layer.shadowColor = UIColor(
            red: Constants.shadowColorRed,
            green: Constants.shadowColorGreen,
            blue: Constants.shadowColorBlue,
            alpha: Constants.shadowColorAlpha
        ).cgColor
        tableView.layer.shadowOpacity = Constants.shadowOpacity
        tableView.layer.shadowRadius = Constants.shadowRadius
        tableView.layer.shadowOffset = Constants.shadowOffset
        tableView.backgroundColor = UIColor(
            red: Constants.tableBackgroundRed,
            green: Constants.tableBackgroundGreen,
            blue: Constants.tableBackgroundBlue,
            alpha: Constants.tableBackgroundAlpha
        )
        tableView.layer.cornerRadius = Constants.tableCornerRadius
        tableView.layer.maskedCorners = Constants.tableMaskedCorners
        tableView.rowHeight = Constants.tableRowHeight
        tableView.register(CoinsCell.self, forCellReuseIdentifier: CoinsCell.reuseId)
        return tableView
    }()

    private var items: [CoinsRow] = [
        .init(title: Constants.defaultCoinTitle, nameIcon: Constants.defaultCoinIconName)
    ]

    // MARK: Lifecycle

    override func viewDidLoad() {
        super.viewDidLoad()

        view.backgroundColor = .black

        title = Constants.screenTitle
        navigationItem.largeTitleDisplayMode = .never

        setupTable()
        configureNavBarAppearance()
    }

    // MARK: Private methods

    private func setupTable() {
        view.addSubview(tableView)
        tableView.dataSource = self
        tableView.delegate = self

        NSLayoutConstraint.activate([
            tableView.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor, constant: Constants.tableTopInset),
            tableView.leadingAnchor.constraint(equalTo: view.leadingAnchor, constant: Constants.tableHorizontalInset),
            tableView.trailingAnchor.constraint(equalTo: view.trailingAnchor, constant: -Constants.tableHorizontalInset),
            tableView.bottomAnchor.constraint(equalTo: view.bottomAnchor)
        ])
    }

    private func configureNavBarAppearance() {
        guard let navBar = navigationController?.navigationBar else { return }

        let appearance = UINavigationBarAppearance()
        appearance.configureWithOpaqueBackground()
        appearance.backgroundColor = .black
        appearance.shadowColor = .clear
        appearance.titleTextAttributes = [
            .foregroundColor: UIColor.white,
            .font: UIFont(name: Constants.navBarTitleFontName, size: Constants.navBarTitleFontSize) as Any
        ]

        if let img = UIImage(named: Constants.backChevronImageName)?.withRenderingMode(.alwaysTemplate) {
            appearance.setBackIndicatorImage(img, transitionMaskImage: img)
        }

        navBar.standardAppearance = appearance
        navBar.scrollEdgeAppearance = appearance
        navBar.compactAppearance = appearance

        navBar.tintColor = UIColor(
            red: Constants.navBarTintRed,
            green: Constants.navBarTintGreen,
            blue: Constants.navBarTintBlue,
            alpha: Constants.navBarTintAlpha
        )
    }
}

// MARK: - UITableViewDataSource/Delegate

extension CoinsViewController: UITableViewDataSource, UITableViewDelegate {

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        items.count
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let item = items[indexPath.row]
        let cell = tableView.dequeueReusableCell(withIdentifier: CoinsCell.reuseId, for: indexPath) as! CoinsCell
        cell.configure(title: item.title, iconName: item.nameIcon)
        return cell
    }

    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let item = items[indexPath.row]
        delegate?.hideCoinsScreen(title: item.title, iconName: item.nameIcon)
    }
}

// MARK: - Constants

private extension CoinsViewController {
    enum Constants {
        static let shadowColorRed: CGFloat = 0
        static let shadowColorGreen: CGFloat = 0
        static let shadowColorBlue: CGFloat = 0
        static let shadowColorAlpha: CGFloat = 0.03

        static let shadowOpacity: Float = 1
        static let shadowRadius: CGFloat = 28.6
        static let shadowOffset: CGSize = CGSize(width: 0, height: 23)

        static let tableBackgroundRed: CGFloat = 0.102
        static let tableBackgroundGreen: CGFloat = 0.102
        static let tableBackgroundBlue: CGFloat = 0.102
        static let tableBackgroundAlpha: CGFloat = 1

        static let tableCornerRadius: CGFloat = 15
        static let tableMaskedCorners: CACornerMask = [.layerMaxXMinYCorner, .layerMinXMinYCorner]
        static let tableRowHeight: CGFloat = 54

        static let tableTopInset: CGFloat = 10
        static let tableHorizontalInset: CGFloat = 15

        static let screenTitle: String = "Currency"

        static let navBarTitleFontName: String = "Lato-Bold"
        static let navBarTitleFontSize: CGFloat = 12

        static let backChevronImageName: String = "chevron_back"

        static let navBarTintRed: CGFloat = 0.671
        static let navBarTintGreen: CGFloat = 0.671
        static let navBarTintBlue: CGFloat = 0.671
        static let navBarTintAlpha: CGFloat = 1

        static let defaultCoinTitle: String = "Bitcoin"
        static let defaultCoinIconName: String = "bitcoin"
    }
}

