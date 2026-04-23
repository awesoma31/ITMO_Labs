struct AuthResponse: Codable {
    let accessToken: String
    let refreshToken: String
    let userId: String
    let username: String
    let email: String?

    enum CodingKeys: String, CodingKey {
        case accessToken = "access_token"
        case refreshToken = "refresh_token"
        case userId = "user_id"
        case username
        case email
    }
}
