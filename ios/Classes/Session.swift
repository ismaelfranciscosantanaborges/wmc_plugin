//
//  Session.swift
//  Runner
//
//  Created by Jorge Tlahuechtl Juarez on 30/04/20.
//  Copyright © 2020 The Chromium Authors. All rights reserved.
//

import Foundation


struct Session : Codable {
    let username : String?
    let sessionid : String?
    let gw : String?
    let password : String?
    let expire_seconds : Int?
    var transport: String? 
    var created:Int?

}
