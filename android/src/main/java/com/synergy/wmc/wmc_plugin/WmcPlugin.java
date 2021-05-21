package com.synergy.wmc.wmc_plugin;

import android.content.Context;

import androidx.annotation.NonNull;

import java.util.Map;
import android.os.StrictMode;

import io.flutter.app.FlutterApplication;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import android.util.Log;


/** WmcPlugin */
public class WmcPlugin implements FlutterPlugin, MethodCallHandler {
  
  private Context mContext;
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;

  @Override
  public void onAttachedToEngine(FlutterPluginBinding flutterPluginBinding) {
    mContext = flutterPluginBinding.getApplicationContext();
    channel = new MethodChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), "wmc_plugin");
    channel.setMethodCallHandler(this);
  }

  @Override
  public void onDetachedFromEngine(FlutterPluginBinding flutterPluginBinding) {
    mContext = flutterPluginBinding.getApplicationContext();
    channel = new MethodChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), "wmc_plugin");
    channel.setMethodCallHandler(this);
  }


  // This static function is optional and equivalent to onAttachedToEngine. It supports the old
  // pre-Flutter-1.12 Android projects. You are encouraged to continue supporting
  // plugin registration via this function while apps migrate to use the new Android APIs
  // post-flutter-1.12 via https://flutter.dev/go/android-project-migration.
  //
  // It is encouraged to share logic between onAttachedToEngine and registerWith to keep
  // them functionally equivalent. Only one of onAttachedToEngine or registerWith will be called
  // depending on the user's project. onAttachedToEngine or registerWith must both be defined
  // in the same class.
  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "wmc_plugin");
    channel.setMethodCallHandler(new WmcPlugin());
    
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull final Result result) {

    System.out.println(mContext);
    if (android.os.Build.VERSION.SDK_INT > 9) {
      StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
      StrictMode.setThreadPolicy(policy);
    }

    if (call.method.equals("initialize")) {
      String apiKey = call.argument("apiKey");

      ApiWmcModule.initialize(mContext,apiKey, new ResultInitListener() {
        @Override
        public void onSuccess(String data) {
          result.success(data);
        }

        @Override
        public void onError(String error, String message, Exception e) {
          result.error(error,message,e);
        }
      });
    }  else if (call.method.equals("request")) {
      String uri = call.argument("uri");
      String method = call.argument("method");
      Map<String,String> options = call.argument("options");
      Map<String,String>  data = call.argument("data");
      System.out.println(uri);
      System.out.println(method);
      System.out.println(options);
      System.out.println(data);

      ApiWmcModule.request(mContext, uri, method, options, data, new ResultRequestListener() {
        @Override
        public void onSuccess(Map<String, String> data) {
          result.success(data.toString());
        }

        @Override
        public void onError(String error, String message, Exception e) {
          result.error(error,message,e);
        }
      });
    }else if (call.method.equals("resource")) {
      String uri = call.argument("uri");

      ApiWmcModule.getResource(mContext, uri, new ResultRequestDataListener() {
        @Override
        public void onSuccess(byte[] data) {
          result.success(data);
        }

        @Override
        public void onError(String error, String message, Exception e) {
          result.error(error,message,e);
        }
      });

    }else{
      result.notImplemented();
    }
  }
}
