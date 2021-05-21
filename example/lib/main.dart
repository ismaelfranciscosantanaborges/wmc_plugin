import 'package:flutter/material.dart';
import 'dart:async';
import 'dart:typed_data';
import 'package:flutter/services.dart';
import 'package:wmc_plugin/wmc_plugin.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _result = 'Unknown';

  @override
  void initState() {
    super.initState();
    /*
     Haber solicitado permiso de READ_PHONE_STATE para el caso de 
     Android ya que por default se envia el codigo de identificador en null en 
     ios no es necesario
     */
    initializeWmc();
    sendRequest();
    //sendPost();
    //sendPut();
    //sendResource();
  }

  Future<void> initializeWmc() async {
    String initializeWmc;
    try {
      initializeWmc =
          await WmcPlugin.initialize("3QQ98ED92353DER781CA361183764D9");
    } on PlatformException {
      initializeWmc = 'Failed to initialize WMC.';
    }

    setState(() {
      _result = initializeWmc;
    });
  }

  Future<void> sendRequest() async {
    Map result;
    try {
      result = await WmcPlugin.request(
        uri: "http://dummy.restapiexample.com/api/v1/employee/1",
        method: "GET",
        options: {'content-type': 'application/json'},
        data: {'abc': '123'},
      );
    } on PlatformException {
      print("Ocurrió un error");
    } catch (e) {
      print("Llegó a este error: ${e.toString()}");
    }

    print(result);
    setState(() {
      _result = result.toString();
    });
  }

  Future<void> sendResource() async {
    Uint8List result;

    try {
      result = await WmcPlugin.resource(
          "https://worldmobileconnection.com/wp-content/uploads/2018/03/LOGO-WORLD-MOBILE.png");
    } on PlatformException {}
    setState(() {
      _result = result.toString();
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Text('Running on: $_result\n'),
        ),
      ),
    );
  }
}
