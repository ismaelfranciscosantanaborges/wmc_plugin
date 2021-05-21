//
//  NetworkManager.swift
//  Runner
//
//  Created by Jorge Tlahuechtl Juarez on 30/04/20.
//  Copyright © 2020 The Chromium Authors. All rights reserved.
//

import Foundation

import Alamofire

class NetworkManager : Alamofire.SessionManager {
    
    
    public static let sharedManager: SessionManager = {
        let configuration = URLSessionConfiguration.default
        configuration.httpAdditionalHeaders = SessionManager.defaultHTTPHeaders
        configuration.timeoutIntervalForRequest = 40
        configuration.timeoutIntervalForResource = 40
        
        let manager = Alamofire.SessionManager(configuration: configuration)
        return manager
    }()
    
    override init(configuration: URLSessionConfiguration, delegate: SessionDelegate, serverTrustPolicyManager: ServerTrustPolicyManager? = nil) {
        super.init(configuration: configuration, delegate: delegate, serverTrustPolicyManager: serverTrustPolicyManager)
        
    }
    
    required public init(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}
