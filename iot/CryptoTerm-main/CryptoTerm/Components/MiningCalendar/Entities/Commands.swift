struct Commands: Comparable {

    static func < (lhs: Commands, rhs: Commands) -> Bool {
        lhs.power < rhs.power
    }

    let power: Int
    let hashrate: Int
    let name: String
}
