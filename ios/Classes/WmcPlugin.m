#import "WmcPlugin.h"
#if __has_include(<wmc_plugin/wmc_plugin-Swift.h>)
#import <wmc_plugin/wmc_plugin-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "wmc_plugin-Swift.h"
#endif

@implementation WmcPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftWmcPlugin registerWithRegistrar:registrar];
}
@end
