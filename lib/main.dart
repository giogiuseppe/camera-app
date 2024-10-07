import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';
import 'package:path_provider/path_provider.dart';
import 'package:flutter/services.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return const MaterialApp(
      debugShowCheckedModeBanner: false,
      home: MainPage(),
    );
  }
}

class MainPage extends StatefulWidget {
  const MainPage({super.key});

  @override
  State<MainPage> createState() => _MainPageState();
}

class _MainPageState extends State<MainPage> {
  final ImagePicker _imagePicker = ImagePicker();
  static const platform = MethodChannel('com.example.client/save_image');

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFF202124),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            IconButton(
              onPressed: _handleCameraButtonPress,
              icon: const Icon(
                Icons.camera,
                color: Colors.greenAccent,
                size: 48,
              ),
            ),
          ],
        ),
      ),
    );
  }

  Future<void> _handleCameraButtonPress() async {
    final XFile? image = await _imagePicker.pickImage(
      source: ImageSource.camera,
    );

    if (image != null) {
      final directory = await getExternalStorageDirectory();
      final path = directory?.path;
      final savedImagePath = '$path/image.jpg';
      await image.saveTo(savedImagePath);

      await _saveImageToGallery(savedImagePath);
    }
  }

  Future<void> _saveImageToGallery(String imagePath) async {
    try {
      await platform
          .invokeMethod('saveImageToGallery', {'imagePath': imagePath});
    } on PlatformException catch (e) {
      print("Erro ao salvar a imagem: ${e.message}");
    }
  }
}
