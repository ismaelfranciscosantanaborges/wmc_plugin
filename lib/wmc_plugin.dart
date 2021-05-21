import 'dart:async';
import 'dart:typed_data';
import 'package:flutter/services.dart';

class WmcPlugin {
  static const MethodChannel _channel = const MethodChannel('wmc_plugin');

  /*
   * Inicializacion de WMC
   */
  static Future<String> initialize(String apikey) async {
    String _result;
    try {
      final String result =
          await _channel.invokeMethod('initialize', {"apiKey": apikey});

      _result = result;
    } on PlatformException catch (e) {
      _result = "${e.message}";
    }
    return _result;
  }

  /*
   * Método que realiza una solicitud request (GET,DELETE)
   */
  static Future<Map> request({
    required String uri,
    required String method,
    required Map options,
    required Map data,
  }) async {
    Map _result;
    try {
      final Map result = await _channel.invokeMethod('request',
          {"uri": uri, "method": method, "options": options, "data": data});

      _result = result;
    } on PlatformException catch (e) {
      _result = {'error': e.message};
    }
    return _result;
  }

  /*
   * Método que obtiene la información en bytes de un recursos (Imagen)
   */
  static Future<Uint8List?> resource(String uri) async {
    Uint8List? _result;
    try {
      final Uint8List result =
          await _channel.invokeMethod('resource', {"uri": uri});

      _result = result;
    } on PlatformException catch (e) {
      _result = null;
    }
    return _result;
  }
}
