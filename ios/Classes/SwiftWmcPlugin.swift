import Flutter
import UIKit

public class SwiftWmcPlugin: NSObject, FlutterPlugin {
  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "wmc_plugin", binaryMessenger: registrar.messenger())
    let instance = SwiftWmcPlugin()
    registrar.addMethodCallDelegate(instance, channel: channel)
  }

  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
    if call.method == "initialize"{
            
            guard let args = call.arguments else {
              return
            }
            
            if let myArgs = args as? [String: Any],
                let apikey = myArgs["apiKey"] as? String{
                
                
                ApiWmcModule.shared.initialize(apiKey: apikey) { (response) in
                    switch(response){
                        case .Success(let r):
                            result(r)
                        case .Error(_):
                            result(FlutterError(code: "-1", message: "Error initialize WMC", details: nil))
                    }
                }
                
            }else {
              result(FlutterError(code: "-1", message: "iOS could not extract " +
                 "flutter arguments in method: (sendParams)", details: nil))
            }
            
        }else if call.method == "request"{
            guard let args = call.arguments else {
              return
            }
            
            if let myArgs = args as? [String: Any],
                let uri = myArgs["uri"] as? String,
                let method = myArgs["method"] as? String,
                let options = myArgs["options"] as? Dictionary<String,String>,
                let data = myArgs["data"] as? Dictionary<String,String>{
                ApiWmcModule.shared.request(uri: uri, method: method, options: options, data: data) { (response) in
                     
                    switch(response){
                        case .Success(let r):
                            result(r)
                        case .Error(_):
                            result(FlutterError(code: "-1", message: "Error request WMC", details: nil))
                    }
                    
                }
            }else{
                result(FlutterError(code: "-1", message: "iOS could not extract " +
                "flutter arguments in method: (sendParams)", details: nil))
            }
            
        }else if call.method == "resource"{
            guard let args = call.arguments else {
              return
            }
            
            if let myArgs = args as? [String: Any],
                let uri = myArgs["uri"] as? String{
                ApiWmcModule.shared.getResource(uri: uri) { (response) in
                     
                    switch(response){
                        case .Success(let r):
                            result(r)
                        case .Error(_):
                            result(FlutterError(code: "-1", message: "Error request WMC", details: nil))
                    }
                    
                }
            }else{
                result(FlutterError(code: "-1", message: "iOS could not extract " +
                "flutter arguments in method: (sendParams)", details: nil))
            }
        }else{
            result(FlutterMethodNotImplemented)
        } 
  }
}
