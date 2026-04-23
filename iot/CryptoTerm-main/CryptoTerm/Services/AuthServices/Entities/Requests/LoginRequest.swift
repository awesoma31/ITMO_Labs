struct LoginRequest: Encodable {
    let usernameOrEmail: String
    let password: String

    enum CodingKeys: String, CodingKey {
        case password
        case usernameOrEmail = "username_or_email"
    }
}
