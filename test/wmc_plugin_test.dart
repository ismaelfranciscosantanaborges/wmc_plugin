import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:wmc_plugin/wmc_plugin.dart';

void main() {
  const MethodChannel channel = MethodChannel('wmc_plugin');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return 'Inicialización erronea';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('initialize', () async {
    expect(await WmcPlugin.initialize("3s28cg4d368g24drt8g24988fds8dr3e"), 'Inicialización erronea');
  });
}
