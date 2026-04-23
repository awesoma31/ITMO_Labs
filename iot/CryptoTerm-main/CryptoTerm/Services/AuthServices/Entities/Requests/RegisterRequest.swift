struct RegisterRequest: Encodable {
    let username: String
    let email: String
    let password: String
    let telegram: String?
}
