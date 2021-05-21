//
//  Utils.swift
//  Runner
//
//  Created by Jorge Tlahuechtl Juarez on 30/04/20.
//  Copyright © 2020 The Chromium Authors. All rights reserved.
//

import Foundation
import UIKit
import CoreTelephony


class Utils {
    static func getTypeConnection() -> String{
        
        let networkStatus = ReachabilityCustom().connectionStatus()
        var transport = "" as String
        
        switch networkStatus {
        case .Unknown, .Offline:
            transport = ""
        case .Online(.WWAN):
            transport = "mobile"
        case .Online(.WiFi):
            transport = "wifi"
        }
        return transport
    }
    
    static func getMnc() -> String{
        let networkInfo =  CTTelephonyNetworkInfo()
        if let carrier = networkInfo.subscriberCellularProvider{
            let mnc = carrier.mobileNetworkCode
            return mnc ?? "050"
        }
        return "050"
    }
    
    static func getMcc() -> String{
        let networkInfo =  CTTelephonyNetworkInfo()
        if let carrier = networkInfo.subscriberCellularProvider{
            let mcc = carrier.mobileCountryCode
            return mcc ?? "334"
        }
        
        return "334"
    }
    
    static func getUUID() -> String{
        var uuid = UserDefaults.standard.string(forKey: "uuid") ?? ""
        if uuid.isEmpty{
            uuid = (UIDevice.current.identifierForVendor?.uuidString)!
            UserDefaults.standard.set(uuid, forKey: "uuid")
        }
        return uuid
    }
    
    static func getNetwork() -> String{
        let networkInfo =  CTTelephonyNetworkInfo()
        let networkString = networkInfo.currentRadioAccessTechnology
        
        var network = "" as String
        
        switch networkString {
        case CTRadioAccessTechnologyGPRS,CTRadioAccessTechnologyEdge,CTRadioAccessTechnologyCDMA1x:
            network = "2G"
        case CTRadioAccessTechnologyLTE:
            network = "4G"
        default:
            network = "3G"
        }
        
        return network
    }
    
    //Date to milliseconds
    static func currentTimeInMiliseconds() -> Int {
        let currentDate = Date()
        let since1970 = currentDate.timeIntervalSince1970
        return Int(since1970 * 1000)
    }
    
    
    static func setApiKey(apikey:String){
        let defaults = UserDefaults.init()
        defaults.set(apikey, forKey: "ApiKey")
    }
    
    static func getApiKey() -> String?{
        let defaults = UserDefaults.init()
        return defaults.string(forKey: "ApiKey") ?? ""
    }
    
    static func clearAll(){
        let defaults = UserDefaults.standard
        let dictionary = defaults.dictionaryRepresentation()
        dictionary.keys.forEach { key in
            defaults.removeObject(forKey: key)
        }
    }
    
    
    static func setSession(session:Session){
        let defaults = UserDefaults.standard
         
        let data = try! JSONEncoder().encode(session)
        
        let json = String(data: data, encoding: .utf8)!
        
        defaults.set(json, forKey: session.transport ?? "")
        
        
    }
    
    static func getSession(transport:String) -> Session?{
        
        let defaults = UserDefaults.standard
        if let json =  defaults.string(forKey: transport) {
            let data = Data(json.utf8)
            if let userData = try? JSONDecoder().decode(Session.self, from: data){
            
                let created = userData.created! + (userData.expire_seconds! * 1000)
                let now = Utils.currentTimeInMiliseconds()
                
                if now > created{
                    return nil
                }
                
                return userData
            }
        }
        return nil
    }
}
