//
//  ApiWmcModule.swift
//  Runner
//
//  Created by Jorge Tlahuechtl Juarez on 30/04/20.
//  Copyright © 2020 The Chromium Authors. All rights reserved.
//

import Foundation
import Alamofire

class ApiWmcModule{
    
    static let shared = ApiWmcModule()
    
    let UrlWMC = "https://api.worldmobileconnection.com/v2/"
    
    
    /**
        Método que inicializa WMC
     */
    func initialize(apiKey:String, completion: @escaping (Result<String>) -> Void){
        if !apiKey.isEmpty{
            
            Utils.setApiKey(apikey: apiKey)
            
            getToken(ApiKey: apiKey) { (result) in
                switch (result){
                case .Success(_): 
                    completion(.Success("Inicializacón correcta"))
                case.Error(let error):
                    completion(.Error(error))
                }
            }
        }
    }
    
    /**
       Método que obtiene un token valido para usarse en la api de WMC
    */
    func getToken(ApiKey:String,completion: @escaping (Result<Token>) -> Void){
        Alamofire.request(self.UrlWMC+"gettoken", method: .post, parameters: ["apikey":ApiKey], encoding: URLEncoding.default, headers: nil).responseJSON { response in
            
            switch(response.result) {
            case .success(_):
                
                //let responseString: String? = String(data: response.data!, encoding: .utf8)
                let token = try! JSONDecoder().decode(Token.self, from: response.data!)
                
                
                print(token)
                completion(.Success(token))
                
                
            case .failure(_):
                print("error gettoken")
                print(response.error as Any)
                completion(.Error((response.response?.statusCode) ?? 999))
            }
        }
    }
    
    /**
    * Obtiene una session valida otorgada por WMC
    */
    func getUser(token:String,completion: @escaping (Result<Session>) -> Void){
        
        let mcc = Utils.getMcc()
        let mnc = Utils.getMnc()
        let uuid = Utils.getUUID()
        let bundleID = Bundle.main.bundleIdentifier!
        let nsObject: AnyObject? = Bundle.main.infoDictionary!["CFBundleShortVersionString"] as AnyObject?
        let version = nsObject as! String
        let network = Utils.getNetwork()
        let device_os = "iOS \(UIDevice.current.systemVersion)"
        let locale = NSLocale.current.languageCode
        let transport = Utils.getTypeConnection()
        
        let parameters: [String: String] = [
            "token":token ,
            "mcc":mcc ,
            "mnc":mnc ,
            "uniqid":uuid ,
            "transport":transport ,
            "device_os":device_os ,
            "device_name":UIDevice.current.modelName ,
            "appid":bundleID ,
            "app_Version":version ,
            "language": locale ?? "es" ,
            "network_type":network ]
        
        //print("tokenuser = \(parameters)")
         
        
        
        Alamofire.request(self.UrlWMC+"getuser", method: .post,
                          parameters: parameters,
                          encoding: URLEncoding.default, headers: nil).responseData { response in
                            
                switch(response.result) {
                    case .success(_):
                        //let responseString: String? = String(data: response.data!, encoding: .utf8)
                        
                        var session = try! JSONDecoder().decode(Session.self, from: response.data!)
                        session.created = Utils.currentTimeInMiliseconds()
                        session.transport = transport
                        Utils.setSession(session: session)
                        completion(.Success(session))
                        break
                        
                    case .failure(_):
                        print("Error getUser")
                        print(response.error as Any)
                        completion(.Error((response.response?.statusCode) ?? 999))
                        break
                }
        }
        
    }
    
    
    /**
    * Método que ejecuta un petición
    * @param uri Url a realizar el request
    * @param method Metodo GET o POST
    * @param sesion objecto que contiene la información de sesion para conexión con el proxy
    * @param options
    * @param parameters
    * @param listener
    */
     
    func request(uri:String,method:String,options:Dictionary<String,String>,data:Dictionary<String,String>,completion success: @escaping (Result<Dictionary<String,String>>) -> Void){
        let transport = Utils.getTypeConnection()
        //Obtiene y verifica session valida
         
        
        if let session = Utils.getSession(transport: transport) {
            ServiceHttp.shared.request(session:session, uri: uri, method: method, options: options, data: data, completion: success)
        }else{
            guard let apiKey = Utils.getApiKey(),!apiKey.isEmpty else{
                return
            }
            getToken(ApiKey: apiKey) { (reponse) in
                switch (reponse){
                    case .Success(let obj):
                        let token = obj.Token
                        self.getUser(token: token) { (r) in
                            switch(r){
                                case .Success(let session):
                                    ServiceHttp.shared.request(session:session, uri: uri, method: method, options: options, data: data, completion: success)
                                case .Error(let error):
                                    success(.Error(error))
                            }
                    }
                    case .Error(let error):
                        success(.Error(error))
                }
            }
        }
    }
    
    func getResource(uri:String,completion: @escaping (Result<Data>) -> Void){
        let transport = Utils.getTypeConnection()
        
        if let session = Utils.getSession(transport: transport) {
            print(session)
            ServiceHttp.shared.getData(session: session, uri: uri,completion: completion)
        }else{
            guard let apiKey = Utils.getApiKey(),!apiKey.isEmpty else{
                return
            }
            getToken(ApiKey: apiKey) { (reponse) in
                switch (reponse){
                    case .Success(let obj):
                        let token = obj.Token
                        self.getUser(token: token) { (r) in
                            switch(r){
                                case .Success(let session):
                                    ServiceHttp.shared.getData(session: session, uri: uri,completion: completion)
                                case .Error(let error):
                                    completion(.Error(error))
                            }
                    }
                    case .Error(let error):
                        completion(.Error(error))
                }
            }
        }
    }
}
 

struct JSONStringArrayEncoding: ParameterEncoding {
    private let myString: String

    init(string: String) {
        self.myString = string
    }

    func encode(_ urlRequest: URLRequestConvertible, with parameters: Parameters?) throws -> URLRequest {
        var urlRequest = urlRequest.urlRequest

        let data = myString.data(using: .utf8)!

        if urlRequest?.value(forHTTPHeaderField: "Content-Type") == nil {
            urlRequest?.setValue("application/json", forHTTPHeaderField: "Content-Type")
        }

        urlRequest?.httpBody = data

        return urlRequest!
    }
}

extension String {
    func deletingPrefix(_ prefix: String) -> String {
        guard self.hasPrefix(prefix) else { return self }
        return String(self.dropFirst(prefix.count))
    }
}

