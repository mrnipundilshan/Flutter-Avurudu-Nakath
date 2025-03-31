import 'package:avurudu_nakath/GetStartPage.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'notification_service.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();

  // Set preferred orientations to portrait only
  await SystemChrome.setPreferredOrientations([
    DeviceOrientation.portraitUp,
    DeviceOrientation.portraitDown,
  ]);

  // Set system UI overlay style
  SystemChrome.setSystemUIOverlayStyle(
    const SystemUiOverlayStyle(
      statusBarColor: Colors.transparent,
      statusBarIconBrightness: Brightness.dark,
      systemNavigationBarColor: Color(0xffFFBE45),
      systemNavigationBarIconBrightness: Brightness.dark,
    ),
  );

  await NotificationService.init(); // Initialize notifications
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      title: 'Avurudu Nakath App',
      theme: ThemeData(
        primaryColor: const Color(0xffFFBE45),
        scaffoldBackgroundColor: const Color(0xffFFBE45),
        colorScheme: ColorScheme.fromSeed(
          seedColor: const Color(0xffFFBE45),
          primary: const Color(0xffFFBE45),
          secondary: const Color(0xFFFFF1D6),
        ),
        useMaterial3: true,
        pageTransitionsTheme: const PageTransitionsTheme(
          builders: {
            TargetPlatform.android: ZoomPageTransitionsBuilder(),
            TargetPlatform.iOS: CupertinoPageTransitionsBuilder(),
          },
        ),
        appBarTheme: const AppBarTheme(
          backgroundColor: Color(0xffFFBE45),
          elevation: 0,
          systemOverlayStyle: SystemUiOverlayStyle.dark,
        ),
      ),
      home: const GetStartPage(),
    );
  }
}
