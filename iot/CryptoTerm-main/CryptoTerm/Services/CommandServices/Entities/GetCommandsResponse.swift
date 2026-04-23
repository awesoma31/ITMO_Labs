struct GetCommandsResponse: Decodable {
    let commandId: String
    let commandName: String

    enum CodingKeys: String, CodingKey {
        case commandId = "command_id"
        case commandName = "command_name"
    }
}
