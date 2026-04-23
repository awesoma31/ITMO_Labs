struct HashrateResponse: Decodable {
    let time: String
    let value: Double
    
    enum CodingKeys: String, CodingKey {
        case time
        case value
    }
}
